-- Admin Table
create table if not exists Admins
(
    mID
               int
        auto_increment
        primary
            key,
    role
               varchar(50) not null,
    adminID    varchar(50) not null,
    adminPW    varchar(50) not null,
    adminName  varchar(50) not null,
    adminPhone varchar(50) not null
);

-- User Table 
create table if not exists Users
(
    uID
               int
        auto_increment
        primary
            key,
    name
               varchar(50) not null,
    userID     varchar(50) not null,
    userPW     varchar(50) not null,
    phone      varchar(50) not null,
    account    varchar(50) not null,
    createAt   date        not null,
    adminCheck varchar(50) not null
);


-- 문의 Table
CREATE TABLE IF NOT EXISTS Request
(
    requestID
                INT
        AUTO_INCREMENT
        PRIMARY
            KEY,
    uid
                INT
                             NOT
                                 NULL,
    r_title
                VARCHAR(255) NOT NULL,
    r_content   TEXT         NOT NULL,
    r_response  TEXT,
    r_createdAt DATETIME    DEFAULT CURRENT_TIMESTAMP,
    r_updatedAt DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    r_status    VARCHAR(50) DEFAULT '대기',
    r_type      ENUM
                    (
                        'board',
                        'onetoone'
                        )    NOT NULL
);

-- 공지 Table
CREATE TABLE IF NOT EXISTS Notice
(
    noticeID
               INT
                            NOT
                                NULL
        PRIMARY
            KEY
        AUTO_INCREMENT,
    n_title
               VARCHAR(100) NOT NULL,
    n_content  VARCHAR(255) NOT NULL,
    n_createAt DATETIME     NOT NULL,
    n_updateAt DATETIME     NOT NULL,
    n_priority INT          NOT NULL,
    mid        INT          NOT NULL
);


-- 창고 Table
DROP TABLE if exists Warehouse;
CREATE TABLE IF NOT EXISTS Warehouse
(
    warehouseID
                      INT
                                   NOT
                                       NULL
        AUTO_INCREMENT,
    warehouseName
                      VARCHAR(50)  NOT NULL,
    warehouseAddress  VARCHAR(255) NOT NULL,
    warehouseStatus   VARCHAR(20)  NOT NULL,
    warehouseCityName VARCHAR(50)  NOT NULL,
    -- maxCapacity       INT          NOT NULL,
    maxCapacity       INT AS
        (
        warehouseArea
            *
        floorHeight
            *
        0.9
        ) STORED                   NOT NULL COMMENT '창고 최대 수용량',
    warehouseArea     INT          NOT NULL,
    regDate           DATE         NOT NULL DEFAULT
                                                (
                                                    CURRENT_DATE
                                                    ),
    floorHeight       INT          NOT NULL,
    mid               INT          NOT NULL,
    PRIMARY KEY
        (
         warehouseID
            )
);


-- 창고 구역 Table 
DROP TABLE if exists WarehouseSection;
CREATE TABLE IF NOT EXISTS WarehouseSection
(
    sectionID
                INT
                            NOT
                                NULL
        AUTO_INCREMENT,
    sectionName
                VARCHAR(50) NOT NULL,
    maxVol      INT         NOT NULL,
    currentVol  INT         NULL,
    warehouseID INT         NOT NULL,
    PRIMARY KEY
        (
         sectionID
            )
);

drop table if exists Item;
CREATE TABLE `Item`
(
    `itemID`       INT auto_increment                                            NOT NULL,
    `itemName`     VARCHAR(100)                                                  NOT NULL,
    `itemPrice`    INT                                                           NOT NULL,
    `weight`       INT                                                           NOT NULL,
    `assemble`     VARCHAR(50)                                                   NOT NULL,
    `customerName` VARCHAR(50)                                                   NOT NULL,
    `material`     VARCHAR(50)                                                   NOT NULL,
    `volume`       DECIMAL(10, 3) AS (`width` * `height` * `levelHeight`) STORED NOT NULL, -- stored 를 사용한 이유는 조회할때 빠르게 하기 위함입니다. stored 를 사용하지 않으면 기본값인 VIRTUAL을 사용하는데
    `width`        DECIMAL(10, 2)                                                NOT NULL, -- VIRTUAL을 사용하게 되면 조회 할때마다 부피값 계산을 해서 출력 해주기 때문에 데이터가 엄청 많다는 가정하에 속도가 느릴거 같아 stored를 사용했습니다.
    `height`       DECIMAL(10, 2)                                                NOT NULL COMMENT '부피를 구하기 위한 세로',
    `levelHeight`  DECIMAL(10, 2)                                                NOT NULL COMMENT '부피를 구하기 위한 높이',
    `spaceName`    VARCHAR(50)                                                   NOT NULL,
    `category`     VARCHAR(50)                                                   NOT NULL,
    primary key (itemID)
);


-- 재고 관리 Table
CREATE TABLE `Inventory`
(
    `eID`         INT auto_increment NOT NULL,
    `quantity`    INT                NOT NULL,
    `warehouseID` INT                NOT NULL,
    sectionID     INT                NOT NULL,
    `itemID`      INT                NOT NULL,
    `stockDate`   DATE,
    `shipDate`    DATE default null,
    primary key (eID)
);

-- 재고 실사 Table
CREATE TABLE `InvenLog`
(
    `logID`            INT  NOT NULL,
    `eID`              INT  NOT NULL,
    `log_quantity`     INT  NOT NULL,
    `warehouseID`      INT  NOT NULL,
    `itemID`           INT  NOT NULL,
    `stockingQuantity` INT  NOT NULL,
    `stockingDate`     DATE NOT NULL,
    `shippingQuantity` INT  NOT NULL,
    `shippingDate`     DATE NOT NULL,
    `logTime`          DATE NOT NULL,
    primary key (logID)
);

drop table if exists Shippment;
create table if not exists shipment
(
    shipmentID          int auto_increment
        primary key,
    shippingDate        date        null,
    Shipping_p_quantity int         null,
    shippingProcess     varchar(20) null,
    waybill             varchar(50) null,
    uid                 int         null,
    itemID              int         null,
    constraint shipment_ibfk_1
        foreign key (uid) references Users (uid),
    constraint shipment_ibfk_2
        foreign key (itemID) references Item (itemID)
);

drop table if exists Stock;
CREATE TABLE `Stock`
(
    `stockID`          INT auto_increment NOT NULL,
    `stockingDate`     DATE               NULL,
    `stockingProcess`  VARCHAR(20)        NOT NULL,
    `stock_p_quantity` INT                NOT NULL,
    `itemID`           INT                NOT NULL,
    `warehouseID`      INT                NULL,
    sectionID          INT                NOT NULL,
    `uID`              INT                NOT NULL,
    primary key (stockID),
    constraint FK_stock_item
        foreign key (itemID) references Item (itemID)
            on delete cascade
);




