package controller.warehouse_section_controller;

import model.warehouse_service.WarehouseSectionDAO;
import util.AppSession;
import view.warehouse_view.WarehouseSectionAdminView;
import vo.Members.Admin;
import vo.Members.Role;
import vo.Warehouses.WarehouseSection;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Warehouse_Section_Controller_Impl implements Warehouse_Section_Controller {

    private final WarehouseSectionDAO sectionDAO;
    private final WarehouseSectionAdminView sectionAdminView;

    // 싱글톤
    private static Warehouse_Section_Controller_Impl controller;

    private Warehouse_Section_Controller_Impl() {
        this.sectionDAO = WarehouseSectionDAO.getInstance();
        this.sectionAdminView = new WarehouseSectionAdminView();
    }

    public static Warehouse_Section_Controller_Impl getInstance() {
        if (controller == null) {
            controller = new Warehouse_Section_Controller_Impl();
        }

        return controller;
    }

    // 구역 선택 메뉴
    @Override
    public void choiceSectionMenu(int choice) throws IOException {
        switch (choice) {
            case 1: // 구역 등록
                insertSectionV2();
                break;
            case 2: // 구역 수정
                updateSection();
                break;
            case 3: // 구역 삭제
                deleteSection();
                break;
            case 4: // 구역 조회
                // selectSectionById();
                int warehouseID = sectionAdminView.getWarehouseIdInput();
                selectSectionWarehouseID(warehouseID);
                break;
            case 5:
                break;
            default:
                sectionAdminView.displayError("메뉴 번호를 잘못 입력하였음");
                break;
        }
    }

    // 구역 등록(입력)
//    @Override
//    public WarehouseSection insertSection() throws IOException {
//
//        Optional<Admin> currentAdminOpt = AppSession.get().currentAdmin();
//
//        if (currentAdminOpt.isEmpty() || currentAdminOpt.get().getRole() != Role.Master) {
//            sectionAdminView.displayError("총관리자(Master) 권한이 필요합니다.");
//            return null;
//        }
//
//        // View 호출 -> 사용자로부터 등록할 구역 정보 받아옴
//        WarehouseSection newSection = sectionAdminView.insertWarehouseSection();
//        // DAO 에 정보 전달-> DB에 저장 요청 -> ID가 부여된 객체 받음
//        WarehouseSection insertSection = sectionDAO.insertSection(newSection);
//
//        if (insertSection != null) {
//            sectionAdminView.displaySuccess("구역 정보 등록 성공 ", insertSection);
//        } else {
//            sectionAdminView.displayError("구역 정보 등록 실패");
//        }
//
//        return insertSection;
//    }

    /**
     * 구역 등록 V2
     * [전체 수정] 대화형으로 창고 구역을 등록
     */
    @Override
    public WarehouseSection insertSectionV2() throws IOException {
        Optional<Admin> currentAdminOpt = AppSession.get().currentAdmin();

        if (currentAdminOpt.isEmpty() || currentAdminOpt.get().getRole() != Role.Master) {
            sectionAdminView.displayError("총관리자(Master) 권한이 필요합니다.");
            return null;
        }

        System.out.println("\n[창고 구역 등록]");

        // 어느 창고에 등록할지 ID를 먼저 받음
        int warehouseId = sectionAdminView.getWarehouseIdInput();

        // 관리자가 'n'을 누를 때까지 구역 추가를 반복
        while (true) {
            // View 를 통해 구역 1개의 정보를 받아옴
            WarehouseSection newSection = sectionAdminView.insertWarehouseSectionV2();
            newSection.setWarehouseID(warehouseId); // 처음에 입력받은 창고 ID 설정

            // DAO 에 등록을 요청하고 결과를 받음
            Map<String, Integer> result = sectionDAO.insertSectionV2(newSection);

            int resultCode = result.get("resultCode");
            int remainingCapacity = result.get("remainingCapacity");

            // DAO 가 반환한 결과에서 새로 생성된 구역 ID를 가져옴
            int newSectionId = result.get("newSectionId");

            // 결과에 따라 메시지를 출력
            if (resultCode == 1) { // 성공
                sectionAdminView.displayMessage(">> '" + newSection.getSectionName() + "' (ID: " + newSectionId + ") 구역 등록 성공");
                sectionAdminView.displayMessage("   [남은 창고 수용량: " + remainingCapacity + "]");
                // 남은 용량이 0이 되면 사용자에게 알리고 반복을 중단
                if (remainingCapacity == 0) {
                    sectionAdminView.displayMessage(">> 창고 수용량이 가득 찼으므로 등록을 종료합니다.");
                    break;
                }
            } else if (resultCode == -1) { // 용량 부족
                sectionAdminView.displayError("용량이 부족하여 등록할 수 없습니다.");
                sectionAdminView.displayMessage("[현재 남은 수용량: " + remainingCapacity + "]");
                continue; // 용량이 부족하면 재입력
            } else { // 기타 DB 오류
                sectionAdminView.displayError("구역 등록에 실패했습니다.");
                break;
            }

            // 6. 계속 추가할지 물어보고, 'n'이면 반복을 중단합니다.
            if (!sectionAdminView.askToAddAnother()) {
                break;
            }
        }
        return null; // 이 메서드는 과정만 처리하므로 반환값은 의미 없음
    }


    // 구역 수정
    @Override
    public int updateSection() throws IOException {

        Optional<Admin> currentAdminOpt = AppSession.get().currentAdmin();

        if (currentAdminOpt.isEmpty() || currentAdminOpt.get().getRole() != Role.Master) {
            sectionAdminView.displayError("총관리자(Master) 권한이 필요합니다.");
            return 0;
        }

        WarehouseSection sectionUpdate = sectionAdminView.updateWarehouseSection();

        int result = sectionDAO.updateSectionV2(sectionUpdate);

        if (result > 0) {
            WarehouseSection warehouseSection = sectionDAO.selectById(sectionUpdate.getId());

            sectionAdminView.displaySuccess("구역 정보 수정 성공 ", warehouseSection);
        } else {
            sectionAdminView.displayError("구역 정보 수정 실패");
        }

        return result;
    }

    // 구역 삭제
    @Override
    public int deleteSection() throws IOException {

        Optional<Admin> currentAdminOpt = AppSession.get().currentAdmin();

        if (currentAdminOpt.isEmpty() || currentAdminOpt.get().getRole() != Role.Master) {
            sectionAdminView.displayError("총관리자(Master) 권한이 필요합니다.");
            return 0;
        }

        int sectionDelete = sectionAdminView.deleteWarehouseSection();

        int result = sectionDAO.deleteSection(sectionDelete);

        if (result > 0) {
            sectionAdminView.displayMessage("구역 ID: " + sectionDelete + "삭제 완료");
        } else {
            sectionAdminView.displayError("구역 정보 삭제 실패");
        }

        return result;
    }

    // 구역 ID 조회
    @Override
    public WarehouseSection selectSectionById() throws IOException {
        int sectionId = sectionAdminView.searchWarehouseSection();

        WarehouseSection section = sectionDAO.selectById(sectionId);

        sectionAdminView.displaySuccess("구역 ID " + sectionId + " 조회 결과", section);

        return section;
    }

    @Override
    public List<WarehouseSection> selectSectionWarehouseID(int warehouseID) {
        List<WarehouseSection> sectionList = sectionDAO.getSections(warehouseID);
        sectionAdminView.displaySuccess("창고 ID " + warehouseID + "의 전체 구역 조회 결과", sectionList);
        return sectionList;
    }

}
