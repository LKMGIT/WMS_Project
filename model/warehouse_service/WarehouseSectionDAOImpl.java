package model.warehouse_service;

import vo.Warehouses.WarehouseSection;

import java.util.List;
import java.util.Map;

/**
 * 창고 구역 데이터하기 위한 DAO 인터페이스
 * 구역 등록(입력), 수정, 삭제, 출력 기능
 */
public interface WarehouseSectionDAOImpl {

    /**
     * 창고 내부의 구역을 입력, DB에 생성된 WarehouseID 까지 포함된 객체를 반환
     * @param ws warehouseID 와 등록 정보가 담긴 신규 WarehouseSection 객체
     * @return DB의 Primary Key : sectionID 가 설정된 WarehouseSection 객체
     */
    // WarehouseSection insertSection(WarehouseSection ws);

    // 창고 구역 등록 V2
    Map<String, Integer> insertSectionV2(WarehouseSection section);


    /**
     * 특정 창고에 소속된 모든 구역 정보 출력
     * @param warehouseID 구역 정보를 조회할 창고의 ID
     * @return 해당 창고의 모든 구역 정보 리스트, 결과가 없으면 빈 리스트
     */
    List<WarehouseSection> getSections(int warehouseID);

    /**
     * ID 로 단일 구역 조회
     * @param sectionId ID 로만 조회
     * @return 해당 창고 구역
     */
    WarehouseSection selectById(int sectionId);

    /**
     * 구역 정보 수정
     * @param ws 수정할 정보가 담긴 WarehouseSection 객체
     * @return 성공적으로 수정된 row 의 수 [성공1, 실패0]
     */
    // int updateSection(WarehouseSection ws);

    int updateSectionV2(WarehouseSection ws);
    /**
     * 구역 삭제
     * @param secID 삭제할 구역 정보의 ID
     * @return 성공적으로 삭제된 row 의 수 1 or 0
     */
    int deleteSection(int secID);
}
