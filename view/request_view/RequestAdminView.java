package view.request_view;

import util.input.AdaptersAndHandler.InputHandler;
import util.input.AdaptersAndHandler.BufferedReaderAdapter;
import vo.Requests.Request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class RequestAdminView {
    private final InputHandler input;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RequestAdminView() {
        this.input = new InputHandler(
                new BufferedReaderAdapter(new BufferedReader(new InputStreamReader(System.in)))
        );
    }

    // --- 메인 메뉴 ---
    public int mainMenu() {
        System.out.println("\n================= 게시판 =================");
        System.out.println("1. 문의관리 | 2. 공지사항 관리 | 3. 뒤로가기");
        System.out.println("\n========================================");
        return input.readInt("[메뉴 선택]: ", 1, 3);
    }

    // --- 문의 관리 메뉴 ---
    public int requestMenu() {
        System.out.println("\n================= 문의 관리 =================");
        System.out.println("1. 1:1 문의 관리");
        System.out.println("2. 문의 게시판 관리");
        System.out.println("3. 뒤로가기");
        System.out.println("\n===========================================");
        return input.readInt("[메뉴 선택]: ", 1, 3);
    }

    // --- 1:1 문의 관리 메뉴 ---
    public int oneToOneMenu() {
        System.out.println("\n================= 1:1 문의 관리 =================");
        System.out.println("1. 1:1 문의 조회");
        System.out.println("2. 답변/답변 수정 등록");
        System.out.println("3. 뒤로가기");
        System.out.println("\n===============================================");
        return input.readInt("[메뉴 선택]: ", 1, 3);
    }

    // --- 문의 게시판 메뉴 ---
    public int requestBoardMenu() {
        System.out.println("\n================= 문의 게시판 관리 ================");
        System.out.println("1. 모든 문의 조회");
        System.out.println("2. 답변/답변 수정 등록");
        System.out.println("3. 특정 문의 삭제");
        System.out.println("4. 뒤로가기");
        System.out.println("\n===============================================");
        return input.readInt("[메뉴 선택]: ", 1, 4);
    }

    // --- 전체 문의 조회 ---
    public void selectAllRequests(List<Request> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("등록된 문의가 없습니다.");
            return;
        }
        System.out.println("\n================================ 문의 목록 ================================");
        System.out.printf("%-5s %-20s %-10s %-20s %-16s %-10s %-10s\n", "ID", "제목", "유형", "내용", "작성일", "상태", "답변");
        for (Request req : requests) {
            String type = req.getR_type() != null ? req.getR_type().name() : "없음";
            String date = req.getR_createAt() != null ? req.getR_createAt().format(fmt) : "없음";
            String response = req.getR_response() != null ? req.getR_response() : "-";
            System.out.printf("%-5d %-20s %-10s %-20s %-16s %-10s %-10s\n",
                    req.getRequestID(), req.getR_title(), type, req.getR_content(), date, req.getR_status(), response);
        }
    }

    // --- 답변/수정 입력 ---
    public Request updateResponse() {
        int id = input.readInt("답변/수정할 문의 ID 입력: ", 1, null);
        boolean confirm = input.readYesNo("정말 답변/수정하시겠습니까?");
        if (!confirm) {
            System.out.println("수정이 취소되었습니다.");
            return null;
        }

        String content = input.readString("답변 내용을 입력해주세요: ", false);
        Request req = new Request();
        req.setRequestID(id);
        req.setR_response(content);
        return req;
    }

    // --- 문의 삭제 ---
    public int deleteRequest() {
        int id = input.readInt("삭제할 문의 ID 입력: ", 1, null);
        boolean confirm = input.readYesNo("정말 삭제하시겠습니까?");
        if (!confirm) {
            System.out.println("삭제가 취소되었습니다.");
            return -1;
        }
        return id;
    }
}
