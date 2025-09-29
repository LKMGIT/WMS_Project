package model.request_service;

import vo.Requests.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBUtil;

public class NoticeDAO {

    // 공지 작성
    public int createNotice(Notice notice) {
        String sql = "INSERT INTO Notice(n_title, n_content, n_createAt, n_updateAt, n_priority, mid) VALUES (?, ?, NOW(), NOW(), ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, notice.getN_title());
            pstmt.setString(2, notice.getN_content());
            pstmt.setInt(3, notice.getN_priority());
            pstmt.setInt(4, notice.getMid());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) return 0;

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 공지 수정
    public int updateNotice(Notice notice) {
        String sql = "UPDATE Notice SET n_title = ?, n_content = ?, n_updateAt = NOW(), n_priority = ? WHERE noticeID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, notice.getN_title());
            pstmt.setString(2, notice.getN_content());
            pstmt.setInt(3, notice.getN_priority());
            pstmt.setInt(4, notice.getNoticeID());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 공지 삭제
    public int deleteNotice(int noticeID) {
        String sql = "DELETE FROM Notice WHERE noticeID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noticeID);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 전체 공지 조회
    public List<Notice> selectAll() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM Notice ORDER BY n_priority DESC, n_createAt DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToNotice(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 공지 상세 조회
    public Notice selectNotice(int noticeID) {
        String sql = "SELECT * FROM Notice WHERE noticeID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noticeID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToNotice(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ResultSet → Notice 매핑
    private Notice mapResultSetToNotice(ResultSet rs) throws SQLException {
        Notice notice = new Notice();
        notice.setNoticeID(rs.getInt("noticeID"));
        notice.setN_title(rs.getString("n_title"));
        notice.setN_content(rs.getString("n_content"));

        Timestamp createTs = rs.getTimestamp("n_createAt");
        if (createTs != null) notice.setN_createAt(createTs.toLocalDateTime());

        Timestamp updateTs = rs.getTimestamp("n_updateAt");
        if (updateTs != null) notice.setN_updateAt(updateTs.toLocalDateTime());

        notice.setN_priority(rs.getInt("n_priority"));
        notice.setMid(rs.getInt("mid"));

        return notice;
    }
}
