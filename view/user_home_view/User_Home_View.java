package view.user_home_view;

import controller.notice_controller.NoticeControllerImpl;
import controller.notice_controller.Notice_Controller;
import controller.request_controller.Request_Controller;
import util.AppSession;
import view.inventory_view.InventoryAdminView;
import view.inventory_view.InventoryUserVIew;
import view.member_view.AdminView;
import view.member_view.UserView;
import view.request_view.RequestAdminView;
import view.request_view.RequestUserView;
import view.shipment_view.ShipmentAdminView;
import view.shipment_view.ShipmentUserView;
import view.stock_view.StockAdminView;
import view.stock_view.StockUserView;
import view.warehouse_view.WarehouseAdminView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class User_Home_View {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    UserView userView = new UserView();
    AdminView adminView = new AdminView();

    RequestUserView requestUserView = new RequestUserView();
    RequestAdminView requestAdminView = new RequestAdminView();

    InventoryAdminView inventoryAdminView = new InventoryAdminView();
    InventoryUserVIew inventoryUserVIew = new InventoryUserVIew();

    StockUserView stockUserView = new StockUserView();
    StockAdminView stockAdminView = new StockAdminView();

    ShipmentUserView shipmentUserView = new ShipmentUserView();
    ShipmentAdminView shipmentAdminView = new ShipmentAdminView();

    WarehouseAdminView warehouseAdminView = new WarehouseAdminView();


    public void userHome() throws IOException {
        while (true) {
            System.out.println("""
                    ===========[ 사용자 홈 화면 ] ==========
                    ============ [ 메뉴 선택 ] ============

                    1. 내 정보 관리          4. 입고 관리
                    2. 게시판               5. 출고 관리
                    3. 내 재고 관리          6. 로그아웃

                    ======================================
                    """);


            int choice = Integer.parseInt(br.readLine());

            switch (choice) {
                case 1 -> {
                    boolean goHome = userView.memberMenu();
                    if (goHome) return;   // 메서드 종료 → 호출자(메인 메뉴)로 복귀
                }
                case 2 -> {
                    int userId = 0;
                    boolean isAdmin = AppSession.get().isAdmin();

                    new Request_Controller(userId, isAdmin).run();
                }

                case 4 -> stockUserView.stockUserMenu();

             case 5 -> {shipmentUserView.shipmentUserMenu();}

                case 6 -> {
                    return;
                }
            }


        }
    }
}






