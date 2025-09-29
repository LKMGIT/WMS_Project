package view.request_view;

import util.input.AdaptersAndHandler.InputHandler;
import util.input.AdaptersAndHandler.BufferedReaderAdapter;
import vo.Requests.Request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestUserView {
    private final InputHandler input;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RequestUserView() {
        this.input = new InputHandler(
                new BufferedReaderAdapter(new BufferedReader(new InputStreamReader(System.in)))
        );
    }

    public int mainMenu() {
        System.out.println("\n=============== 게시판 ===============");
        System.out.println("1. 문의하기 | 2. 공지사항 | 3. 뒤로가기");
        System.out.println("\n====================================");
        return input.readInt("[메뉴 선택]: ", 1, 3);
    }

    public int requestMenu() {
        System.out.println("\n=============== 문의하기 ===============");
        System.out.println("1. 문의 게시판 | 2. 1:1 문의 | 3. 뒤로가기");
        System.out.println("\n======================================");
        return input.readInt("[메뉴 선택]: ", 1, 3);
    }

    public int requestBoardMenu() {
        System.out.println("\n=================================== 문의 게시판 메뉴 ===================================");
        System.out.println("1. 글쓰기 | 2. 내 글 수정 | 3. 전체 문의 조회 | 4. 내 문의 조회 | 5. 글 삭제 | 6. 뒤로가기");
        System.out.println("\n====================================================================================");
        return input.readInt("[메뉴 선택]: ", 1, 6);
    }

    public int oneToOneMenu() {
        System.out.println("\n=========================== 1:1 문의 메뉴 ===========================");
        System.out.println("1. 글쓰기 | 2. 내 글 수정 | 3. 내 문의 조회 | 4. 글 삭제 | 5. 뒤로가기");
        System.out.println("\n===================================================================");
        return input.readInt("[메뉴 선택]: ", 1, 5);
    }

    public Request createRequest() {
        Request request = new Request();
        request.setR_title(input.readString("제목: ", false));
        request.setR_content(input.readString("내용: ", false));
        request.setR_status("대기");
        return request;
    }

    public int updateRequest() {
        return input.readInt("수정할 글의 ID를 입력해주세요: ", 1, null);
    }

    public int deleteRequest() {
        int id = input.readInt("삭제할 글의 ID를 입력해주세요: ", 1, null);
        if (!input.readYesNo("정말 삭제하시겠습니까?")) {
            System.out.println("삭제가 취소되었습니다.");
            return -1;
        }
        return id;
    }

    public void selectAllRequest(List<Request> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("조회할 글이 없습니다.");
            return;
        }
        System.out.println("\n--- 전체 문의 목록 ---");
        System.out.println("ID\t제목\t유형\t내용\t작성일\t작성자ID");
        for (Request req : requests) {
            String type = req.getR_type() != null ? req.getR_type().name() : "없음";
            String date = req.getR_createAt() != null ? req.getR_createAt().format(fmt) : "없음";
            System.out.printf("%d\t%s\t%s\t%s\t%s\t%d\n",
                    req.getRequestID(), req.getR_title(), type, req.getR_content(),date, req.getUid());
        }
    }

    public void selectMyRequest(List<Request> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("조회할 글이 없습니다.");
            return;
        }
        System.out.println("\n--- 내 문의 목록 ---");
        System.out.println("ID\t제목\t유형\t내용\t작성일");
        for (Request req : requests) {
            String type = req.getR_type() != null ? req.getR_type().name() : "없음";
            String date = req.getR_createAt() != null ? req.getR_createAt().format(fmt) : "없음";
            System.out.printf("%d\t%s\t%s\t%s\t%s\n",
                    req.getRequestID(), req.getR_title(), type, req.getR_content(), date);
        }
    }
}
