-- 조회에 사용될 뷰
create view invenwareitem as
SELECT i.itemid,
       i.itemname,
       i.itemprice,
       i.weight,
       i.assemble,
       customername,
       i.material,
       i.volume,
       i.spacename,
       i.category,
       inv.eid,
       inv.quantity,
       inv.stockdate,
       inv.shipdate,
       w.warehouseid,
       w.warehousename,
       w.warehouseaddress,
       w.warehousestatus,
       w.warehousecityname,
       w.maxcapacity,
       w.mid,
       ws.sectionid,
       ws.sectionname,
       ws.maxvol,
       ws.currentvol
FROM Inventory inv
         JOIN
     Warehouse w ON inv.warehouseID = w.warehouseID
         JOIN
     Item i ON inv.itemID = i.itemID
         JOIN
     warehousesection ws on ws.warehouseID = w.warehouseID and ws.sectionID = inv.sectionid;


-- 관리자가 입고 승인하면 재고 테이블에 데이터가 들어가는 트리거
delimiter ##
create trigger insert_inventory
    after update
    on stock
    for each row
begin
    if new.stockingProcess = '승인' then
        insert into inventory (quantity, warehouseid, sectionid, itemid, stockdate)
        values (NEW.stock_p_quantity, NEW.warehouseid, NEW.sectionid, NEW.itemid, NEW.stockingDate);
    end if;
end ##
delimiter ;


-- 출고시 자동으로 재고량 계산해주고 재고 테이블에 출고 날짜 찍어주는 프로시저
DELIMITER $$
CREATE PROCEDURE sp_process_shipment(IN p_shipmentID INT)
BEGIN
    -- 1. 변수 선언
    DECLARE v_item_id INT;
    DECLARE v_requested_qty INT;
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_ship_date DATE;
    DECLARE v_lot_eid INT;
    DECLARE v_lot_qty INT;
    DECLARE v_remaining_to_ship INT;
    DECLARE done INT DEFAULT FALSE;
    -- 2. 재고 목록을 가져오는 커서(Cursor) 선언
    DECLARE stock_cursor CURSOR FOR
        SELECT eID, quantity
        FROM Inventory
        WHERE itemID = v_item_id
          AND quantity > 0
        ORDER BY stockDate ASC
            FOR
        UPDATE;
    -- 3. 에러 핸들러 및 트랜잭션 설정
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- 4. 출고 요청 정보 조회
    SELECT itemID, shipping_p_quantity, shipppingProcess, shippingDate
    INTO v_item_id, v_requested_qty, v_current_status, v_ship_date -- << 2. 수정된 부분: shippingDate 조회
    FROM Shippment
    WHERE shipmentID = p_shipmentID;
    IF v_current_status != '승인' THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '승인 상태인 출고 요청만 처리할 수 있습니다.';
    END IF;
    SET v_remaining_to_ship = v_requested_qty;
    -- 5. 재고 차감 로직 실행
    OPEN stock_cursor;
    read_loop:
    LOOP
        FETCH stock_cursor INTO v_lot_eid, v_lot_qty;
        IF done OR v_remaining_to_ship <= 0 THEN
            LEAVE read_loop;
        END IF;
        IF v_lot_qty >= v_remaining_to_ship THEN
            -- 현재 재고로 충분할 때
            UPDATE Inventory
            SET quantity = quantity - v_remaining_to_ship,
                shipDate = v_ship_date -- << 3. 수정된 부분: 일부만 출고돼도 날짜 기록
            WHERE eID = v_lot_eid;
            SET v_remaining_to_ship = 0;
        ELSE
            -- 현재 재고를 모두 소진해도 부족할 때
            UPDATE Inventory
            SET quantity = 0,
                shipDate = v_ship_date -- << 3. 수정된 부분: 올바른 날짜 변수 사용
            WHERE eID = v_lot_eid;
            SET v_remaining_to_ship = v_remaining_to_ship - v_lot_qty;
        END IF;
    END LOOP;
    CLOSE stock_cursor;
    -- 6. 최종 결과 확인
    IF v_remaining_to_ship > 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '재고가 부족하여 출고할 수 없습니다.';
    ELSE
        UPDATE Shippment
        SET shipppingProcess = '출고완료' -- 성공 시 출고 상태 변경, 날짜는 이미 있으므로 변경 안 함
        WHERE shipmentID = p_shipmentID;
        COMMIT;
    END IF;
END$$
DELIMITER ;