package controller.request_controller;

import controller.notice_controller.NoticeControllerImpl;
import model.request_service.RequestDAO;
import view.request_view.RequestAdminView;
import view.request_view.RequestUserView;
import vo.Requests.Request;
import vo.Requests.Request.RequestType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Request_Controller {

    private final RequestUserView userView;
    private final RequestAdminView adminView;
    private final RequestDAO dao;
    private final int userId;
    private final boolean isAdmin;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Request_Controller(int userId, boolean isAdmin) {
        this.userView = new RequestUserView();
        this.adminView = new RequestAdminView();
        this.dao = new RequestDAO();
        this.userId = userId;
        this.isAdmin = isAdmin;
    }

    public void run() throws IOException {
        if (isAdmin) adminMenuLoop();
        else userMenuLoop();
    }

<<<<<<< HEAD
    private void adminMenuLoop() throws IOException {
        while (true) {
            int mainChoice = adminView.mainMenu();
            if (mainChoice == 3) break;

            if (mainChoice == 1) {
                requestMenuLoop();
            }
            else if (mainChoice == 2) {
                NoticeControllerImpl noticeController = new NoticeControllerImpl(true);
                noticeController.run();
            }
            else System.out.println("잘못된 입력입니다.");
        }
    }

    private void requestMenuLoop() throws IOException {
        while (true) {
            int choice = adminView.requestMenu();
            if (choice == 3) break;
            if (choice == 1) oneToOneAdminLoop();
            else if (choice == 2) boardAdminLoop();
            else System.out.println("잘못된 입력입니다.");
        }
    }

    private void oneToOneAdminLoop() throws IOException {
        while (true) {
            int choice = adminView.oneToOneMenu();
            if (choice == 3) break;
            if (choice == 1) {
                List<Request> list = dao.selectAllRequests(RequestType.onetoone);
                adminView.selectAllRequests(list);
            } else if (choice == 2) {
                Request req = adminView.updateResponse();
                if (req != null) {
                    if (dao.updateResponse(req.getRequestID(), req.getR_response()) > 0)
                        System.out.println("답변 등록/수정 완료");
                    else System.out.println("답변 등록/수정 실패");
=======
    // --- 관리자 메인 메뉴 루프 ---
    private void adminMenuLoop() throws IOException {
        while (true) {
            int mainChoice = adminView.mainMenu();
            switch (mainChoice) {
                case 1 -> requestMenuLoop();
                case 2 -> {
                    // 공지사항 메뉴로 이동
                    NoticeControllerImpl noticeController = new NoticeControllerImpl(true); // 관리자 모드
                    noticeController.run(); // 뒤로가기 선택 시 돌아옴
>>>>>>> develop
                }
                case 3 -> {
                    System.out.println("뒤로갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

<<<<<<< HEAD
    private void boardAdminLoop() throws IOException {
        while (true) {
            int choice = adminView.requestBoardMenu();
            if (choice == 4) break;
            if (choice == 1) {
                List<Request> list = dao.selectAllRequests(RequestType.board);
                adminView.selectAllRequests(list);
            } else if (choice == 2) {
                Request req = adminView.updateResponse();
                if (req != null) {
                    if (dao.updateResponse(req.getRequestID(), req.getR_response()) > 0)
                        System.out.println("답변 등록/수정 완료");
                    else System.out.println("답변 등록/수정 실패");
                }
            } else if (choice == 3) {
                int id = adminView.deleteRequest();
                if (id > 0) {
                    if (dao.deleteRequest(0, id) > 0)
                        System.out.println("삭제 완료");
                    else System.out.println("삭제 실패");
                }
            }
        }
    }

    private void userMenuLoop() throws IOException {
        while (true) {
            int mainChoice = userView.mainMenu();
            if (mainChoice == 3) break;
            if (mainChoice == 1) userRequestLoop();
            else if (mainChoice == 2) {
                NoticeControllerImpl noticeController = new NoticeControllerImpl(false);
                noticeController.run();
            } else System.out.println("잘못된 입력입니다.");
=======
    // --- 사용자 메인 메뉴 루프 ---
    private void userMenuLoop() throws IOException {
        while (true) {
            int mainChoice = userView.mainMenu();
            switch (mainChoice) {
                case 1 -> userRequestLoop();
                case 2 -> {
                    // 공지사항 메뉴로 이동
                    NoticeControllerImpl noticeController = new NoticeControllerImpl(false); // 사용자 모드
                    noticeController.run(); // 뒤로가기 선택 시 돌아옴
                }
                case 3 -> {
                    System.out.println("뒤로갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
>>>>>>> develop
        }
    }

    // --- 문의 관리 메뉴 ---
    private void requestMenuLoop() throws IOException {
        while (true) {
            int choice = adminView.requestMenu();
            switch (choice) {
                case 1 -> oneToOneAdminLoop();
                case 2 -> boardAdminLoop();
                case 3 -> { return; }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // --- 1:1 문의 관리 루프 ---
    private void oneToOneAdminLoop() throws IOException {
        while (true) {
            int choice = adminView.oneToOneMenu();
            switch (choice) {
                case 1 -> {
                    List<Request> list = dao.selectAllRequests(RequestType.onetoone);
                    adminView.selectAllRequests(list);
                }
                case 2 -> {
                    Request req = adminView.updateResponse();
                    if (req != null) {
                        if (dao.updateResponse(req.getRequestID(), req.getR_response()) > 0)
                            System.out.println("답변 등록/수정 완료");
                        else System.out.println("답변 등록/수정 실패");
                    }
                }
                case 3 -> { return; }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // --- 문의 게시판 관리 루프 ---
    private void boardAdminLoop() throws IOException {
        while (true) {
            int choice = adminView.requestBoardMenu();
            switch (choice) {
                case 1 -> {
                    List<Request> list = dao.selectAllRequests(RequestType.board);
                    adminView.selectAllRequests(list);
                }
                case 2 -> {
                    Request req = adminView.updateResponse();
                    if (req != null) {
                        if (dao.updateResponse(req.getRequestID(), req.getR_response()) > 0)
                            System.out.println("답변 등록/수정 완료");
                        else System.out.println("답변 등록/수정 실패");
                    }
                }
                case 3 -> {
                    int id = adminView.deleteRequest();
                    if (id > 0) {
                        if (dao.deleteRequest(0, id) > 0)
                            System.out.println("삭제 완료");
                        else System.out.println("삭제 실패");
                    }
                }
                case 4 -> { return; }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // --- 사용자 1:1/게시판 루프 ---
    private void userRequestLoop() throws IOException {
        while (true) {
            int choice = userView.requestMenu();
            switch (choice) {
                case 1 -> requestBoardLoop();
                case 2 -> oneToOneLoop();
                case 3 -> { return; }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void requestBoardLoop() throws IOException {
        while (true) {
            int choice = userView.requestBoardMenu();
            switch (choice) {
                case 1 -> {
                    Request req = userView.createRequest();
                    req.setUid(userId);
                    req.setR_type(RequestType.board);
                    int id = dao.createRequest(req);
                    System.out.println(id > 0 ? "등록 완료, ID: " + id : "등록 실패");
                }
                case 2 -> {
                    int id = userView.updateRequest();
                    if (id > 0) {
                        System.out.print("제목: ");
                        String title = reader.readLine();
                        System.out.print("내용: ");
                        String content = reader.readLine();
                        if (dao.updateRequest(userId, id, title, content) > 0)
                            System.out.println("수정 완료");
                        else System.out.println("수정 실패");
                    }
                }
                case 3 -> userView.selectAllRequest(dao.selectAllRequests(RequestType.board));
                case 4 -> userView.selectMyRequest(dao.selectMyRequest(userId, RequestType.board));
                case 5 -> {
                    int id = userView.deleteRequest();
                    if (id > 0) {
                        if (dao.deleteRequest(userId, id) > 0)
                            System.out.println("삭제 완료");
                        else System.out.println("삭제 실패");
                    }
                }
                default -> { return; }
            }
        }
    }

    private void oneToOneLoop() throws IOException {
        while (true) {
            int choice = userView.oneToOneMenu();
            switch (choice) {
                case 1 -> {
                    Request req = userView.createRequest();
                    req.setUid(userId);
                    req.setR_type(RequestType.onetoone);
                    int id = dao.createRequest(req);
                    System.out.println(id > 0 ? "등록 완료, ID: " + id : "등록 실패");
                }
                case 2 -> {
                    int id = userView.updateRequest();
                    if (id > 0) {
                        System.out.print("제목: ");
                        String title = reader.readLine();
                        System.out.print("내용: ");
                        String content = reader.readLine();
                        if (dao.updateRequest(userId, id, title, content) > 0)
                            System.out.println("수정 완료");
                        else System.out.println("수정 실패");
                    }
                }
                case 3 -> userView.selectMyRequest(dao.selectMyRequest(userId, RequestType.onetoone));
                case 4 -> {
                    int id = userView.deleteRequest();
                    if (id > 0) {
                        if (dao.deleteRequest(userId, id) > 0)
                            System.out.println("삭제 완료");
                        else System.out.println("삭제 실패");
                    }
                }
                default -> { return; }
            }
        }
    }
}
