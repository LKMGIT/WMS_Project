package controller.notice_controller;

import model.request_service.NoticeDAO;
import view.notice_view.NoticeAdminView;
import view.notice_view.NoticeUserView;
import vo.Requests.Notice;

import java.util.List;

public class NoticeControllerImpl implements Notice_Controller {

    private final NoticeAdminView adminView;
    private final NoticeUserView userView;
    private final NoticeDAO dao;
    private final boolean isAdmin;

    public NoticeControllerImpl(boolean isAdmin) {
        this.adminView = new NoticeAdminView();
        this.userView = new NoticeUserView();
        this.dao = new NoticeDAO();
        this.isAdmin = isAdmin;
    }

    public void run() {
        if (isAdmin) adminMenuLoop();
        else userMenuLoop();
    }

    private void adminMenuLoop() {
        while (true) {
            int choice = adminView.noticeAdminMenu();
            switch (choice) {
                case 1 -> createNotice();
                case 2 -> updateNotice();
                case 3 -> deleteNotice();
                case 4 -> selectAll();
                case 5 -> selectNotice();
                case 6 -> { System.out.println("뒤로갑니다."); return; }
            }
        }
    }

    private void userMenuLoop() {
        while (true) {
            int choice = userView.noticeUserMenu();
            switch (choice) {
                case 1 -> selectAll();
                case 2 -> selectNotice();
                case 3 -> { System.out.println("뒤로갑니다."); return; }
            }
        }
    }

    @Override
    public int createNotice() {
        Notice notice = adminView.createNotice();
        int result = dao.createNotice(notice);
        System.out.println(result > 0 ? "공지 작성 완료" : "공지 작성 실패");
        return result;
    }

    @Override
    public int updateNotice() {
        int noticeID = adminView.inputNoticeID("수정");
        Notice existing = dao.selectNotice(noticeID);
        if (existing == null) {
            System.out.println("공지 존재하지 않음");
            return 0;
        }

        Notice updated = adminView.createNotice();
        updated.setNoticeID(noticeID);
        int result = dao.updateNotice(updated);
        System.out.println(result > 0 ? "공지 수정 완료" : "공지 수정 실패");
        return result;
    }

    @Override
    public int deleteNotice() {
        int noticeID = adminView.inputNoticeID("삭제");
        int result = dao.deleteNotice(noticeID);
        System.out.println(result > 0 ? "공지 삭제 완료" : "공지 삭제 실패");
        return result;
    }

    @Override
    public void selectAll() {
        List<Notice> notices = dao.selectAll();
        if (isAdmin) adminView.selectAll(notices);
        else userView.selectAll(notices);
    }

    @Override
    public void selectNotice() {
        int noticeID;
        if (isAdmin) noticeID = adminView.inputNoticeID("조회");
        else noticeID = dao.selectAll().isEmpty() ? 0 : adminView.inputNoticeID("조회");

        Notice notice = dao.selectNotice(noticeID);
        if (isAdmin) adminView.selectNotice(notice);
        else userView.selectNotice(notice);
    }
}
