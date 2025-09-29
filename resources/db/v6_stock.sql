DROP PROCEDURE IF EXISTS sp_StockRequest;
-- 입고 요청
DELIMITER $$
CREATE PROCEDURE sp_StockRequest(
    IN p_itemName VARCHAR(100),
    IN p_itemPrice INT,
    IN p_weight INT,
    IN p_assemble VARCHAR(50),
    IN p_customerName VARCHAR(50),
    IN p_material VARCHAR(50),
    IN p_width FLOAT,
    IN p_height FLOAT,
    IN p_levelHeight FLOAT,
    IN p_spaceName VARCHAR(50),
    IN p_category VARCHAR(50),
    IN p_stock_p_quantity INT,
    IN p_warehouseID INT,
    IN p_sectionID INT,
    IN p_uID INT
)
BEGIN
    DECLARE new_itemID INT;

    -- 트랜잭션 시작
    START TRANSACTION;

    -- 1) Item 테이블 INSERT
    INSERT INTO Item (
        itemName, itemPrice, weight, assemble, customerName, material,
         width, height, levelHeight, spaceName, category
    ) VALUES (
                 p_itemName, p_itemPrice, p_weight, p_assemble, p_customerName, p_material,
                  p_width, p_height, p_levelHeight, p_spaceName, p_category
             );

    -- 새로 생성된 itemID 가져오기
    SET new_itemID = LAST_INSERT_ID();

    -- 2) Stock 테이블 INSERT (입고 요청)
    INSERT INTO Stock (
        itemID, stock_p_quantity, warehouseID, sectionID, uID, stockingProcess, stockingDate
    ) VALUES (
                 new_itemID, p_stock_p_quantity, null, null, p_uID, '승인대기', NOW()
             );

    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;


truncate Stock;
CALL sp_StockRequest(
        '테스트상품', 10000, 5, '조립A', '고객1', '철', 10, 20, 5, 'A구역', '카테고리1',
        50, 1, 2, 1
     );

-- Stock 테이블 확인
SELECT * FROM Stock ORDER BY stockID DESC LIMIT 1;

-- Item 테이블 확인
SELECT * FROM Item ORDER BY itemID DESC LIMIT 1;




-- 입고요청한 거 삭제
DROP PROCEDURE IF EXISTS sp_deleteStock;

DELIMITER $$
CREATE PROCEDURE sp_deleteStock(
    IN p_stockID INT
)

begin
    DELETE from Stock
    where stockID = p_stockID;


end $$
delimiter ;




-- 입고현황조회
DROP PROCEDURE IF EXISTS sp_stockCurrentSearch;

DELIMITER $$
CREATE PROCEDURE sp_stockCurrentSearch(
    IN p_uID INT  -- 로그인한 사용자 ID
)
BEGIN
    -- 조회: Stock 테이블에서 uID = 로그인한 사용자 ID인 데이터만 가져오기
    SELECT
        s.stockID,
        s.stockingDate,
        s.stockingProcess,
        s.stock_p_quantity,
        s.itemID,
        s.sectionID,
        s.uID,
        s.warehouseID,
        i.itemName,
        i.itemPrice,
        i.weight,
        i.assemble,
        i.customerName,
        i.material,
        i.volume,
        i.width,
        i.height,
        i.levelHeight,
        i.spaceName,
        i.category
    FROM Stock s
             JOIN Item i ON s.itemID = i.itemID
    WHERE s.uID = p_uID;


    -- 조회 결과가 없으면 사용자 권한 없음/데이터 없음 처리
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '잘못된 stockID이거나 권한이 없습니다.';
    END IF;
END$$
DELIMITER ;




-- 입고고지서 조회 (승인 된것만)
DROP PROCEDURE IF EXISTS sp_stockPrint;
DELIMITER $$

CREATE PROCEDURE sp_stockPrint(
    IN p_stockID INT,
    IN p_userID INT   -- 요청한 사용자 ID
)
BEGIN
    DECLARE cnt INT;

    -- stockID가 요청한 유저 소유인지 확인
    SELECT COUNT(*) INTO cnt
    FROM Stock
    WHERE stockID = p_stockID
      AND uID = p_userID
      AND stockingProcess = '승인';

    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '잘못된 stockID입니다.';
    ELSE
        SELECT
            s.stockID, s.stockingDate, s.stockingProcess, s.stock_p_quantity,
            s.itemID, s.sectionID, s.uID,
            i.itemName, i.itemPrice, i.weight, i.assemble, i.customerName,
            i.material, i.volume, i.width, i.height, i.levelHeight, i.spaceName, i.category,
            w.warehouseName, w.warehouseID
        FROM Stock s
                 JOIN Item i ON s.itemID = i.itemID
                 JOIN Warehouse w ON s.warehouseID = w.warehouseID
        WHERE s.stockID = p_stockID
          AND s.uID = p_userID
          AND s.stockingProcess = '승인';
    END IF;
END$$

DELIMITER ;







-- ================================================================




-- 관리자 입고조회 (승인 대기인것만)
-- 입고 요청 현황 조회 (승인 대기)
DROP PROCEDURE IF EXISTS sp_stockRequestInformation;

DELIMITER $$
CREATE PROCEDURE sp_stockRequestInformation()
BEGIN
    SELECT s.stockID, s.stockingDate, s.stockingProcess, s.stock_p_quantity,
           s.itemID, s.warehouseID, s.sectionID, s.uID,
           i.itemName, i.itemPrice, i.weight, i.assemble, i.customerName,
           i.material, i.volume, i.width, i.height, i.levelHeight, i.spaceName, i.category
    FROM Stock s
             JOIN Item i ON s.itemID = i.itemID
    WHERE s.stockingProcess = '승인대기'
    ORDER BY s.stockingDate DESC;
END$$
DELIMITER ;

select * from Stock;
truncate Stock;





-- 관리자 승인/거절 처리
DROP PROCEDURE IF EXISTS sp_updateStockProcess;
DELIMITER $$

CREATE PROCEDURE sp_updateStockProcess(
    IN p_stockID INT,
    IN p_stokingProcess VARCHAR(20),     -- '승인' 또는 '거절'
    IN p_warehouseID INT,        -- 승인일 경우만 사용
    IN p_sectionID INT           -- 승인일 경우만 사용
)
BEGIN
    IF p_stokingProcess = '승인' THEN
        UPDATE Stock
        SET stockingProcess = '승인',
            warehouseID = p_warehouseID,
            sectionID = p_sectionID,
            stockingDate = NOW()
        WHERE stockID = p_stockID;

    ELSEIF p_stokingProcess = '거절' THEN
        UPDATE Stock
        SET stockingProcess = '거절',
            stockingDate = NOW()
        WHERE stockID = p_stockID;
    END IF;
END$$

DELIMITER ;






-- 월별 입고현화 조회
DROP PROCEDURE IF EXISTS sp_stockMonthlyReport;

DELIMITER $$
CREATE PROCEDURE sp_stockMonthlyReport(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    SELECT
        s.stockID,
        s.stockingDate,
        s.stockingProcess,
        s.stock_p_quantity,
        s.itemID,
        s.warehouseID,
        s.sectionID,
        s.uID,
        i.itemName,
        i.itemPrice
    FROM Stock s
             JOIN Item i ON s.itemID = i.itemID
    WHERE YEAR(s.stockingDate) = p_year
      AND MONTH(s.stockingDate) = p_month
    ORDER BY s.stockingDate ASC;
END$$
DELIMITER ;



-- 관리자용 승인 완료 입고현황 조회
DROP PROCEDURE IF EXISTS sp_selectApprovedStock;

DELIMITER $$
CREATE PROCEDURE sp_selectApprovedStock()
BEGIN
    SELECT s.stockID,
           s.stockingDate,
           s.stockingProcess,
           s.stock_p_quantity,
           s.itemID,
           s.warehouseID,
           s.sectionID,
           s.uID,
           i.itemName,
           i.itemPrice,
           i.weight,
           i.assemble,
           i.customerName,
           i.material,
           i.volume,
           i.width,
           i.height,
           i.levelHeight,
           i.spaceName,
           i.category
    FROM Stock s
             JOIN Item i ON s.itemID = i.itemID
    WHERE s.stockingProcess = '승인'
    ORDER BY s.stockingDate DESC;
END$$
DELIMITER ;





create table if not exists railway.shipment
(
    shipmentID          int auto_increment
        primary key,
    shippingDate        date        null,
    Shipping_p_quantity int         null,
    shippingProcess     varchar(20) null,
    waybill             varchar(50) null,
    uid                 int         null,
    itemID              int         null,
    warehouseID         int         null,
    constraint shipment_ibfk_1
        foreign key (uid) references railway.Users (uID),
    constraint shipment_ibfk_2
        foreign key (itemID) references railway.Item (itemID)
);





create table if not exists railway.Stock
(
    stockID          int auto_increment
        primary key,
    stockingDate     date        null,
    stockingProcess  varchar(20) not null,
    stock_p_quantity int         not null,
    itemID           int         not null,
    warehouseID      int         null,
    sectionID        int         null,
    uID              int         not null,
    constraint FK_stock_item
        foreign key (itemID) references railway.Item (itemID)
            on delete cascade
);








create table if not exists railway.Item
(
    itemID       int auto_increment
        primary key,
    itemName     varchar(100)   not null,
    itemPrice    int            not null,
    weight       int            not null,
    assemble     varchar(50)    not null,
    customerName varchar(50)    not null,
    material     varchar(50)    not null,
    volume       decimal(10, 3) as (((`width` * `height`) * `levelHeight`)) stored,
    width        decimal(10, 2) not null,
    height       decimal(10, 2) not null comment '부피를 구하기 위한 세로',
    levelHeight  decimal(10, 2) not null comment '부피를 구하기 위한 높이',
    spaceName    varchar(50)    not null,
    category     varchar(50)    not null
);