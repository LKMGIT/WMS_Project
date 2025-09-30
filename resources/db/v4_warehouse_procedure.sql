####################### 창고 프로시저 #######################
######################### 창고 관리 ########################
-- 창고 등록
DROP PROCEDURE IF EXISTS sp_InsertWarehouse;
DELIMITER $$
CREATE PROCEDURE sp_InsertWarehouse(
    IN p_warehouseName VARCHAR(50),
    IN p_warehouseAddress VARCHAR(255),
    IN p_warehouseStatus VARCHAR(20),
    IN p_warehouseCityName VARCHAR(50),
    -- IN p_maxCapacity INT,
    IN p_warehouseArea INT,
    IN p_floorHeight INT,
    IN p_mid INT,
    OUT out_newWarehouseID INT
)
BEGIN
    INSERT INTO Warehouse (warehouseName, warehouseAddress, warehouseStatus, warehouseCityName,
                           warehouseArea, floorHeight, mid)
    VALUES (p_warehouseName, p_warehouseAddress, p_warehouseStatus, p_warehouseCityName,
            p_warehouseArea, p_floorHeight, p_mid);

    SET out_newWarehouseID = LAST_INSERT_ID();
END $$
DELIMITER ;

-- 창고 등록 조회시 창고 ID 가져오기
DROP PROCEDURE IF EXISTS sp_GetWarehouseId;
DELIMITER $$
CREATE PROCEDURE sp_GetWarehouseId(IN p_warehouseID INT)
BEGIN
    SELECT *
    FROM Warehouse
    WHERE warehouseID = p_warehouseID;
END $$
DELIMITER ;

-- 창고 전체 조회
delimiter $$
create procedure sp_searchAllWarehouse()
begin
    select wh.warehouseID,
           wh.warehouseName,
           wh.warehouseAddress,
           wh.warehouseStatus,
           wh.warehouseCityName,
           wh.maxCapacity,
           wh.warehouseArea,
           wh.regDate,
           wh.floorHeight,
           wh.mid,
           ad.adminName
    from Warehouse wh
             left join Admins ad
                       on wh.mid = ad.mid;
end $$
delimiter ;

-- 소재지 조회
delimiter $$
create procedure sp_selectByLocation(IN p_warehouseAddress VARCHAR(255))
begin
    select wh.warehouseID,
           wh.warehouseName,
           wh.warehouseAddress,
           wh.warehouseStatus,
           wh.warehouseCityName,
           wh.maxCapacity,
           wh.warehouseArea,
           wh.regDate,
           wh.floorHeight,
           wh.mid,
           ad.adminName
    from Warehouse wh
             join Admins ad on wh.mid = ad.mid
    where wh.warehouseAddress like CONCAT('%', p_warehouseAddress, '%');
end $$
delimiter ;

-- 이름 조회
DROP PROCEDURE IF EXISTS sp_searchByName;
delimiter $$
create procedure sp_searchByName(in p_warehouseName varchar(50))
begin
    select wh.warehouseID,
           wh.warehouseName,
           wh.warehouseAddress,
           wh.warehouseStatus,
           wh.warehouseCityName,
           wh.maxCapacity,
           wh.warehouseArea,
           wh.regDate,
           wh.floorHeight,
           wh.mid,
           ad.adminName
    from Warehouse wh
             join Admins ad
                  on wh.mid = ad.mid
    where wh.warehouseName like CONCAT('%', p_warehouseName, '%');
end $$
delimiter ;

-- 면적(사이즈) 조회
DROP PROCEDURE IF EXISTS sp_selectBySize;
DELIMITER $$
CREATE PROCEDURE sp_selectBySize(IN p_warehouseArea INT)
BEGIN
    SELECT wh.warehouseID,
           wh.warehouseName,
           wh.warehouseAddress,
           wh.warehouseStatus,
           wh.warehouseCityName,
           wh.maxCapacity,
           wh.warehouseArea,
           wh.regDate,
           wh.floorHeight,
           wh.mid,
           ad.adminName
    FROM Warehouse wh
             JOIN Admins ad ON wh.mid = ad.mid
    WHERE wh.warehouseArea = p_warehouseArea;
END $$
DELIMITER ;

-- 상태 조회
DROP PROCEDURE IF EXISTS sp_getWarehouseStatus;
delimiter $$
create procedure sp_getWarehouseStatus(in whStatus VARCHAR(20))
begin
    select wh.warehouseID,
           wh.warehouseName,
           wh.warehouseAddress,
           wh.warehouseStatus,
           wh.warehouseCityName,
           wh.maxCapacity,
           wh.warehouseArea,
           wh.regDate,
           wh.floorHeight,
           wh.mid,
           ad.adminName
    from Warehouse wh
             join Admins ad on ad.mid = wh.mid
    where wh.warehouseStatus like CONCAT('%', whStatus, '%');
end $$
delimiter ;

-- 창고 수정
DROP PROCEDURE IF EXISTS sp_updateWarehouse;
DELIMITER $$
CREATE PROCEDURE sp_updateWarehouse(
    IN p_warehouseID INT,
    IN p_newWarehouseName VARCHAR(50),
    IN p_newWarehouseAddress VARCHAR(255),
    IN p_newWarehouseStatus VARCHAR(20),
    IN p_newWarehouseCityName VARCHAR(50),
    IN p_newWarehouseArea INT,
    IN p_newFloorHeight INT,
    IN p_newMid INT
)
BEGIN
    UPDATE Warehouse
    SET -- COALESCE 함수: 입력값이 NULL이면 기존 값을, NULL이 아니면 새 값을 사용
        warehouseName     = COALESCE(p_newWarehouseName, warehouseName),
        warehouseAddress  = COALESCE(p_newWarehouseAddress, warehouseAddress),
        warehouseStatus   = COALESCE(p_newWarehouseStatus, warehouseStatus),
        warehouseCityName = COALESCE(p_newWarehouseCityName, warehouseCityName),
        warehouseArea     = COALESCE(p_newWarehouseArea, warehouseArea),
        floorHeight       = COALESCE(p_newFloorHeight, floorHeight),
        mid               = COALESCE(p_newMid, mid)
    WHERE warehouseID = p_warehouseID;
END $$
DELIMITER ;
-- 창고 삭제
delimiter $$
create procedure sp_deleteWarehouse(in p_warehouseID int)
begin
    delete
    from Warehouse
    where warehouseID = p_warehouseID;
end $$
delimiter ;
######################### 구역 관리 ########################
-- 구역 등록
DROP PROCEDURE IF EXISTS sp_InsertSectionV2;
DELIMITER $$
CREATE PROCEDURE sp_InsertSectionV2(
    IN p_warehouseID INT,
    IN p_sectionName VARCHAR(50),
    IN p_newMaxVol INT, -- 구역의 최대 허용 부피 < 창고 최대 수용량

    OUT out_resultCode INT, -- 처리 결과 (1 성공, -1 용량 부족)
    OUT out_remainingCapacity INT, -- 입력 후 창고에 남은 용량
    OUT out_newSectionID INT -- 새로 등록된 구역 ID
)
BEGIN
    DECLARE v_warehouseMaxCapacity INT; -- 창고 최대 수용량 변수
    DECLARE v_currentSectionsTotalVol INT; -- 현재 등록된 모든 구역들의 부피 총합 변수
    DECLARE v_remainingCapacity INT; -- 남은 용량 변수

    -- 창고 최대 수용량을 값을 조회하여 변수에 저장
    SELECT maxCapacity INTO v_warehouseMaxCapacity FROM Warehouse WHERE warehouseID = p_warehouseID;

    -- 해당 창고에 이미 등록된 모든 구역들의 최대 부피를 합산하여 변수에 저장
    -- IF NULL(..., 0) 은 등록된 구역이 하나도 없어 합계가 NULL 인 경우 0으로 처리
    SELECT IFNULL(SUM(maxVol), 0)
    INTO v_currentSectionsTotalVol
    FROM WarehouseSection
    WHERE warehouseID = p_warehouseID;

    -- 남은 용량 = 전체 용량 - 현재 사용 용량
    SET v_remainingCapacity = v_warehouseMaxCapacity - v_currentSectionsTotalVol;

    -- 새로 추가하려는 구역의 부피가 남은 용량보다 크면
    IF p_newMaxVol > v_remainingCapacity THEN
        SET out_resultCode = -1; -- 용량 부족
        SET out_remainingCapacity = v_remainingCapacity; -- 현재 남은 용량을 담음
        SET out_newSectionID = 0; -- 등록된 것이 없으므로 구역 ID = 0
    ELSE
        -- 남은 용량 충분하다면 등록 성공
        INSERT INTO WarehouseSection(warehouseID, sectionName, maxVol, currentVol)
        VALUES (p_warehouseID, p_sectionName, p_newMaxVol, 0);

        SET out_resultCode = 1; -- 결과: 성공
        SET out_remainingCapacity = v_remainingCapacity - p_newMaxVol; -- 새로 사용한 만큼 용량을 추가로 뺌
        SET out_newSectionID = LAST_INSERT_ID(); -- ID 값을 가져옴
    END IF;
END $$
DELIMITER ;

-- 창고 ID 로 구역 조회
DROP PROCEDURE IF EXISTS sp_GetSectionsByWarehouseId;
DELIMITER $$
CREATE PROCEDURE sp_GetSectionsByWarehouseId(
    IN p_warehouseID INT
)
BEGIN
    SELECT * FROM WarehouseSection WHERE warehouseID = p_warehouseID;
END $$
DELIMITER ;

-- 구역 ID 로 조회하는 프로시저
DROP PROCEDURE IF EXISTS sp_GetSectionById;
DELIMITER $$
CREATE PROCEDURE sp_GetSectionById(IN p_sectionID INT)
BEGIN
    SELECT *
    FROM WarehouseSection
    WHERE sectionID = p_sectionID;
END $$
DELIMITER ;

-- 구역 정보 수정
DROP PROCEDURE IF EXISTS sp_UpdateSectionV2;
DELIMITER $$
CREATE PROCEDURE sp_UpdateSectionV2(
    -- === 입력(IN) 파라미터 ===
    IN p_sectionID INT,
    IN p_newSectionName VARCHAR(50), -- 새 구역 이름
    IN p_newMaxVol INT, -- 새 최대 허용 수용량
    -- IN p_newCurrentVol INT,

    -- === 출력(OUT) 파라미터 ===
    OUT out_resultCode INT, -- 결과 코드 (1: 성공, -1: 용량 부족)
    OUT out_message VARCHAR(255) -- 결과 메시지
)
BEGIN
    -- 변수 선언
    DECLARE v_warehouseID INT;
    DECLARE v_warehouseMaxCapacity INT;
    DECLARE v_currentSectionsTotalVol INT;
    DECLARE v_oldMaxVol INT; -- 수정 전 원래 최대 적재량
    DECLARE v_capacityChange INT; -- 변경 적재량
    DECLARE v_remainingCapacity INT; -- 남아있는 공간

    -- 1. 수정하려는 구역의 현재 정보(창고ID, 기존 maxVol)를 가져옴
    SELECT warehouseID, maxVol INTO v_warehouseID, v_oldMaxVol FROM WarehouseSection WHERE sectionID = p_sectionID;

    -- 2. 해당 창고의 전체 정보(최대 수용량, 현재 모든 구역의 총합)를 가져옴
    SELECT maxCapacity INTO v_warehouseMaxCapacity FROM Warehouse WHERE warehouseID = v_warehouseID;
    SELECT IFNULL(SUM(maxVol), 0)
    INTO v_currentSectionsTotalVol
    FROM WarehouseSection
    WHERE warehouseID = v_warehouseID;

    -- 3. 이번 수정으로 인해 변하게 될 용량의 '차이'를 계산 (새로운 값 - 기존 값)
    SET v_capacityChange = p_newMaxVol - v_oldMaxVol;

    -- 4. 현재 창고에 남아있는 용량 계산
    SET v_remainingCapacity = v_warehouseMaxCapacity - v_currentSectionsTotalVol;

    -- 5.남은 용량이 '변화량'을 감당할 수 있는지 확인
    IF v_capacityChange > v_remainingCapacity THEN
        SET out_resultCode = -1;
        SET out_message =
                CONCAT('용량이 부족하여 수정할 수 없습니다. (추가 필요 용량: ', v_capacityChange, ', 현재 남은 용량: ', v_remainingCapacity, ')');
    ELSE
        -- 용량이 충분하면 UPDATE 실행
        UPDATE WarehouseSection
        SET sectionName = p_newSectionName,
            maxVol      = p_newMaxVol
        WHERE sectionID = p_sectionID;

        SET out_resultCode = 1;
        SET out_message = '성공적으로 수정되었습니다.';
    END IF;
END $$
DELIMITER ;

-- 구역 삭제
DROP PROCEDURE IF EXISTS sp_DeleteSection;
DELIMITER $$
CREATE PROCEDURE sp_DeleteSection(
    IN p_sectionID INT
)
BEGIN
    DELETE FROM WarehouseSection WHERE sectionID = p_sectionID;
END $$
DELIMITER ;