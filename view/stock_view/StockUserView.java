package view.stock_view;

import controller.stock_controller.Stock_Controller_Impl;
import controller.stock_controller.Stock_User_Controller;
import vo.Items.Item;
import vo.Stocks.Stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class StockUserView {
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static final Stock_Controller_Impl sc = new Stock_Controller_Impl(); // 컨트롤러
    private static final Stock_User_Controller su = new Stock_User_Controller();



    // 입고 메뉴
    public void stockUserMenu() throws IOException {
        while (true) {
            System.out.println();
            System.out.print("""
                    ============== [입고 메뉴] ===============
                    
                    1. 입고 요청                2. 입고 고지서 출력
                    3. 입고 요청 삭제            4. 입고 현황 조회
                    0. 이전 메뉴로
                    
                    ===========================================
                    [입고 메뉴를 선택해주세요 : ]: """);

            int menu = Integer.parseInt(br.readLine());

            switch (menu) {
                case 1 -> stockRequest();
                case 2 -> stockPrint();
                case 3 -> deleteStock();
                case 4 -> stockCurrentSearch();
                //case 2 -> selectStockNotice();
                //case 3 -> deleteStock();
                //case 4 -> selectCurrentStock();
                case 0 -> {
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
            }
        }
    }


    // 1. 입고 요청 (Item 테이블 전체 컬럼 입력)
    public void stockRequest() {
        try {
            System.out.println("============== [입고 요청] ===============");
            System.out.println("===========[ 아이템 정보 입력 ] =============");

            System.out.print("상품 이름 입력: ");
            String itemName = br.readLine();

            System.out.print("상품 가격 입력: ");
            int itemPrice = Integer.parseInt(br.readLine());

            System.out.print("무게 입력: ");
            int weight = Integer.parseInt(br.readLine());

            System.out.print("조립 여부 입력: ");
            String assemble = br.readLine();

            System.out.print("고객 이름 입력: ");
            String customerName = br.readLine();

            System.out.print("가로 길이 입력: ");
            float width = Float.parseFloat(br.readLine());

            System.out.print("세로 길이 입력: ");
            float height = Float.parseFloat(br.readLine());

            System.out.print("층 높이 입력: ");
            float levelHeight = Float.parseFloat(br.readLine());

            System.out.print("공간 이름 입력: ");
            String spaceName = br.readLine();

            System.out.print("카테고리 입력: ");
            String category = br.readLine();

            System.out.print("사용자 번호 입력: ");
            int userID = Integer.parseInt(br.readLine());

            System.out.print("재질 입력: ");
            String material = br.readLine();

            System.out.print("입고 수량 입력: ");
            int stock_p_quantity = Integer.parseInt(br.readLine());

            Item item = new Item();
            item.setItemName(itemName);
            item.setItemPrice(itemPrice);
            item.setWeight(weight);
            item.setAssemble(assemble);
            item.setCustomerName(customerName);
            item.setMaterial(material);
            item.setWidth(width);
            item.setHeight(height);
            item.setLevelHeight(levelHeight);
            item.setSpaceName(spaceName);
            item.setCategory(category);

            // Stock 객체 생성 및 Item 정보 세팅
            // 나머지는 사용자가 입력한 값을 넣지만 Process는 승인대기를 기본값으로 저장
            Stock stock = new Stock();
            stock.setUID(userID);
            stock.setStock_p_quantity(stock_p_quantity);
            stock.setStockingProcess("승인대기");

            // 위에 입력한 값들 result에 담아서 controller로 전송
            Stock_User_Controller controller = new Stock_User_Controller(); // 실제 컨트롤러/DAO 객체
            int result = controller.stockRequest(item, stock);

            if (result > 0) {
                System.out.println("입고 요청이 정상적으로 DB에 저장되었습니다.");
            } else {
                System.out.println("입고 요청에 실패했습니다.");
            }


        } catch (IOException | NumberFormatException e) {
            System.err.println("입력 오류: 올바른 값을 입력해주세요.");
        } catch (Exception e) {
            System.err.println("입고 요청 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // 2. 입고 고지서 조회 (승인 된것만)
    public void stockPrint() {
        try {
            System.out.println("============== [입고 고지서 출력] ===============");
            System.out.print("출력할 입고 고지서 번호(stockID)를 입력하세요: ");
            int stockID = Integer.parseInt(br.readLine());
            System.out.print("출력할 유저 번호(userID)를 입력하세요 : ");
            int userID = Integer.parseInt(br.readLine());

            Stock stock = su.stockPrint(stockID,userID); // DAO에서 VIEW 혹은 프로시저 호출

            if (stock != null) {
                System.out.println("고지서를 성공적으로 조회했습니다.");
                System.out.println("==============================================");
                System.out.println("  [입고 고지서]");
                System.out.println("----------------------------------------------");
                System.out.printf("  입고 번호: %d%n", stock.getStockID());
                System.out.printf("  입고 수량: %d개%n", stock.getStock_p_quantity());
                System.out.printf("  입고 상태: %s%n", stock.getStockingProcess());
                System.out.printf("  입고 날짜: %s%n", stock.getStockingDate());
                System.out.println("==============================================");
            } else {
                System.out.println("해당 입고 번호에 대한 재고 정보가 없습니다.");
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("오류: 유효한 입고 번호를 입력해주세요.");
        }
    }




    // 4. 입고 현황 조회
    public void stockCurrentSearch() {
        System.out.println("============== [입고 현황 조회] ===============");
        try {
            System.out.print("유저번호를 입력해주세요 : ");
            int uID = Integer.parseInt(br.readLine());
            List<Stock> stocks = su.stockCurrentSearch(uID); // DAO 호출

            if (stocks.isEmpty()) {
                System.out.println("창고에는 입고된 재고가 없습니다.");
                return;
            }

            System.out.printf("%-10s %-10s %-20s %-10s %-15s%n %-15s%n",
                    "입고번호", "아이템ID", "아이템명", "수량", "상태", "입고일");
            System.out.println("------------------------------------------------------------");

            for (Stock stock : stocks) {
                Item item = stock.getItem();
                System.out.printf("%-10d %-10d %-20s %-10d %-15s %-15s%n",
                        stock.getStockID(),
                        (item != null ? item.getItemID() : 0),
                        (item != null ? item.getItemName() : "-"),
                        stock.getStock_p_quantity(),
                        stock.getStockingProcess(),
                        stock.getStockingDate());
            }

            System.out.println("==========================================================");

        } catch (Exception e) {
            System.err.println("입고 현황 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }




    // 3. 입고 요청 삭제
    public void deleteStock () {
        System.out.println("============== [입고 요청 삭제] ===============");
        try {
            System.out.print("삭제할 입고 번호(stockID) 입력: ");
            int stockID = Integer.parseInt(br.readLine());

            int result = su.deleteStock(stockID);

            if (result > 0) {
                System.out.println("입고 요청이 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("삭제할 수 없습니다. (이미 승인되었거나 잘못된 번호)");
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("오류: 올바른 숫자를 입력해주세요.");
        }
    }
    
}


