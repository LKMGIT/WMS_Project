package controller.warehouse_controller;

import model.warehouse_service.WarehouseDAO;
import model.warehouse_service.WarehouseSectionDAO;
import util.AppSession;
import view.warehouse_view.WarehouseAdminView;
import vo.Members.Admin;
import vo.Members.Role;
import vo.Warehouses.Warehouse;
import vo.Warehouses.WarehouseSection;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 창고 관리 기능의 전체적인 흐름 제어하는 컨트롤러 클래스
 * View 로부터 사용자의 요청을 받아, 그에 맞는 DAO 로직을 취하고
 * 결과를 다시 View 에 전달하여 화면에 표시하게 함
 */
public class Warehouse_Controller_Impl implements Warehouse_Controller {

    private final WarehouseDAO warehouseDAO; // DB의 Warehouse 테이블 작업
    private final WarehouseSectionDAO sectionDAO;
    private final WarehouseAdminView warehouseAdminView; // 사용자 입출력

    // 싱글톤
    private static Warehouse_Controller_Impl controller;

    private Warehouse_Controller_Impl() {
        this.warehouseDAO = WarehouseDAO.getInstance();
        this.sectionDAO = WarehouseSectionDAO.getInstance();
        this.warehouseAdminView = new WarehouseAdminView();
    }

    public static Warehouse_Controller_Impl getInstance() {
        if (controller == null) {
            controller = new Warehouse_Controller_Impl();
        }
        return controller;
    }


    @Override
    public void choiceWarehouseMenu(int choice) throws IOException {
        switch (choice) {
            case 1: // 창고 등록
                insertWarehouse();
                break;
            case 2: // 창고 수정
                updateWarehouse();
                break;
            case 3: // 창고 삭제
                deleteWarehouse();
                break;
            case 4: // 창고 조회
                int searchChoice = warehouseAdminView.selectWarehouseMenu();
                handleSearchMenu(searchChoice); // 아래에 새로 추가할 헬퍼 메서드 호출
                break;
            case 5:
                break; // 뒤로 가기는 main 루프에서 처리
            default:
                warehouseAdminView.displayError("번호 잘못 입력하였음");
                break;
        }
    }

    /**
     * 창고 조회 하위 메뉴
     * @param choice 사용자가 조회 메뉴에서 선택한 번호
     */
    private void handleSearchMenu(int choice) throws IOException {
        switch (choice) {
            case 1: // 전체 조회
                selectAllWarehouse();
                break;
            case 2: // 소재지(도시)로 조회
                String location = warehouseAdminView.getInput("검색할 소재지(도시) 이름: ");
                selectByLocation(location);
                break;
            case 3: // 창고명으로 조회
                String name = warehouseAdminView.getInput("검색할 창고 이름: ");
                selectByName(name);
                break;
            case 4: // 면적으로 조회
                int size = warehouseAdminView.getIntInput("검색할 창고 면적: ");
                selectBySize(size);
                break;
            case 5: // 재고 상태 조회
                int warehouseId = warehouseAdminView.getIntInput("상태를 확인할 창고의 ID: ");
                String status = getWarehouseStatus(warehouseId);
                warehouseAdminView.displayMessage("\n창고 ID " + warehouseId + "의 재고 상태");
                warehouseAdminView.displayMessage("> " + status);
                break;
            case 6: // 뒤로 가기
                break;
            default:
                warehouseAdminView.displayError("조회 메뉴 번호 오입력");
                break;
        }
    }


    // 창고 등록
    @Override
    public Warehouse insertWarehouse() throws IOException {
        Optional<Admin> currentAdminOpt = AppSession.get().currentAdmin();

        if (currentAdminOpt.isEmpty() || currentAdminOpt.get().getRole() != Role.Master) {
            warehouseAdminView.displayError("총관리자(Master) 권한이 필요합니다.");
            return null;
        }

        Warehouse newWarehouse = warehouseAdminView.insertWarehouse(); // View 호출 -> 사용자로부터 등록할 창고 정보 받아옴
        Warehouse insertedWh = warehouseDAO.insertWarehouse(newWarehouse); // DAO 에 정보를 전달 -> DB 저장 요청 -> ID가 부여된 객체 받음
        if (insertedWh != null) {
            warehouseAdminView.displaySuccess("창고 등록 성공 ", insertedWh);
        } else {
            warehouseAdminView.displayError("창고 등록 실패");
        }
        return insertedWh;
    }

    // 창고 이름 조회
    @Override
    public List<Warehouse> selectByName(String wname) {
        List<Warehouse> warehouseNameList = warehouseDAO.searchByName(wname);

        if (warehouseNameList != null && !warehouseNameList.isEmpty()) {
            warehouseAdminView.displaySuccess("'" + wname + "' 이름으로 검색한 결과 ", warehouseNameList);
        } else {
            warehouseAdminView.displayError("창고 이름 조회 실패");
        }

        return warehouseNameList;
    }

    // 창고 전체 조회
    @Override
    public List<Warehouse> selectAllWarehouse() {
        List<Warehouse> warehouseList = warehouseDAO.searchAllWarehouse();

        if (warehouseList != null && !warehouseList.isEmpty()) {
            warehouseAdminView.displaySuccess("창고 전체 조회 성공 ", warehouseList);
        } else {
            warehouseAdminView.displayError("창고 전체 조회 실패");
        }

        return warehouseList;
    }

    // 창고 위치 조회
    @Override
    public List<Warehouse> selectByLocation(String waddress) {
        List<Warehouse> warehouseLocationList = warehouseDAO.selectByLocation(waddress);

        if (warehouseLocationList != null && !warehouseLocationList.isEmpty()) {
            warehouseAdminView.displaySuccess("'" + waddress + "' 소재지(도시)로 검색한 결과 ", warehouseLocationList);
        } else {
            warehouseAdminView.displayError("창고 소재지(도시) 조회 실패");
        }

        return warehouseLocationList;
    }

    // 창고 면적 조회
    @Override
    public List<Warehouse> selectBySize(int wsize) {
        List<Warehouse> warehouseSizeList = warehouseDAO.selectBySize(wsize);

        if (warehouseSizeList != null && !warehouseSizeList.isEmpty()) {
            warehouseAdminView.displaySuccess(wsize + " 면적으로 검색한 결과 ", warehouseSizeList);
        } else {
            warehouseAdminView.displayError("창고 면적 조회 실패");
        }

        return warehouseSizeList;
    }


    /**
     * 창고 ID를 받아 해당 창고의 '모든 구역'의 용량을 합산하여 상태를 반환
     * 상태: 가득 참, 보통, 여유, 비어있음, 정보 없음
     * @param wid 창고 ID
     * @return 창고 상태 문자열
     */
    @Override
    public String getWarehouseStatus(int wid) {
        WarehouseSectionDAO sectionDAO = WarehouseSectionDAO.getInstance();

        // wid 로 해당 창고에 속한 구역 정보를 List 로 가져옴
        List<WarehouseSection> sections = sectionDAO.getSections(wid);

        // 창고 정보가 없거나, 구역이 하나도 등록되지 않은 경우
        if (sections == null || sections.isEmpty()) {
            return "창고 또는 구역 정보 없음\n비어있음 (현재 사용량: 0.00%)";
        }

        // 창고의 총 용량과 현재 총 재고를 계산할 변수를 준비
        int totalCapacity = 0;
        int totalCurrentStock = 0;

        // 모든 구역을 순회하며 용량과 재고를 합산
        for (WarehouseSection section : sections) {
            totalCapacity += section.getMaxVol();
            totalCurrentStock += section.getCurrentVol();
        }

        // 총 용량 정보가 0 이하인 경우 (잘못된 데이터)
        if (totalCapacity <= 0) {
            return "용량 정보 오류";
        }

        // 합산된 값으로 점유율(%)을 계산
        double occupancyRate = ((double) totalCurrentStock / totalCapacity) * 100;

        // 점유율에 따라 상태를 결정하여 반환
        if (occupancyRate >= 95) {
            return String.format("포화 (현재 사용량: %.2f%%)", occupancyRate);
        } else if (occupancyRate >= 60) {
            return String.format("보통 (현재 사용량: %.2f%%)", occupancyRate);
        } else if (occupancyRate > 0) {
            return String.format("여유 (현재 사용량: %.2f%%)", occupancyRate);
        } else { // totalCurrentStock 이 0인 경우
            return "비어있음 (현재 사용량 : 0%)";
        }
    }

    // 창고 정보 수정
    @Override
    public int updateWarehouse() throws IOException {

        Optional<Admin> currentAdminOpt = AppSession.get().currentAdmin();

        if (currentAdminOpt.isEmpty() || currentAdminOpt.get().getRole() != Role.Master) {
            warehouseAdminView.displayError("총관리자(Master) 권한이 필요합니다.");
            return 0;
        }

        Warehouse warehouse = warehouseAdminView.updateWarehouse();

        int result = warehouseDAO.updateWarehouse(warehouse);

        if (result > 0) {
            Warehouse updatedWarehouse = warehouseDAO.selectId(warehouse.getId());
            warehouseAdminView.displaySuccess("창고 정보 수정 성공", updatedWarehouse);
        } else {
            warehouseAdminView.displayError("창고 정보 수정 실패 (ID 확인 필요)");
        }
        return result;
    }

    // 창고 삭제
    @Override
    public int deleteWarehouse() throws IOException {

        Optional<Admin> currentAdminOpt = AppSession.get().currentAdmin();

        if (currentAdminOpt.isEmpty() || currentAdminOpt.get().getRole() != Role.Master) {
            warehouseAdminView.displayError("총관리자(Master) 권한이 필요합니다.");
            return 0;
        }

        int idToDelete = warehouseAdminView.deleteWarehouse();
        int result = warehouseDAO.deleteWarehouse(idToDelete);
        if (result > 0) {
            warehouseAdminView.displayMessage("[처리 성공] 창고 ID " + idToDelete + " 삭제");
        } else {
            warehouseAdminView.displayError("창고 삭제 실패 (ID 확인 필요)");
        }
        return result;
    }
}
