package model.request_service;

import util.DBUtil;
import vo.Requests.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    // --- 문의 등록 ---
    public int createRequest(Request request) {
        String sql = "INSERT INTO Request(uid, r_title, r_content, r_type, r_status, r_createdAt, r_updatedAt) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, request.getUid());
            pstmt.setString(2, request.getR_title());
            pstmt.setString(3, request.getR_content());
            pstmt.setString(4, request.getR_type().name());
            pstmt.setString(5, request.getR_status());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // --- 사용자 본인 문의 수정 ---
    public int updateRequest(int uid, int requestID, String title, String content) {
        String sql = "UPDATE Request SET r_title=?, r_content=?, r_updatedAt=NOW() WHERE requestID=? AND uid=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, requestID);
            pstmt.setInt(4, uid);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // --- 사용자 본인 문의 삭제 ---
    public int deleteRequest(int uid, int requestID) {
        String sql;


        if (uid == 0) {
            sql = "DELETE FROM Request WHERE requestID=?";
        } else {
            sql = "DELETE FROM Request WHERE requestID=? AND uid=?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requestID);

            if (uid != 0) {
                pstmt.setInt(2, uid);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // --- 관리자 답변 등록/수정 ---
    public int updateResponse(int requestID, String response) {
        String sql = "UPDATE Request SET r_status='답변완료', r_response=?, r_updatedAt=NOW() WHERE requestID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, response);
            pstmt.setInt(2, requestID);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // --- 전체 조회 (타입별) ---
    public List<Request> selectAllRequests(Request.RequestType type) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT * FROM Request WHERE r_type=? ORDER BY r_createdAt DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- 사용자 본인 문의 조회 (타입별 필터 적용) ---
    public List<Request> selectMyRequest(int uid, Request.RequestType type) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT * FROM Request WHERE uid=? AND r_type=? ORDER BY r_createdAt DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uid);
            pstmt.setString(2, type.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // --- ResultSet → Request 매핑 ---
    private Request mapResultSetToRequest(ResultSet rs) throws SQLException {
        Request req = new Request();
        req.setRequestID(rs.getInt("requestID"));
        req.setUid(rs.getInt("uid"));
        req.setR_title(rs.getString("r_title"));
        req.setR_content(rs.getString("r_content"));
        req.setR_response(rs.getString("r_response"));
        req.setR_status(rs.getString("r_status"));

        String type = rs.getString("r_type");
        if (type != null) req.setR_type(Request.RequestType.valueOf(type));
        else req.setR_type(Request.RequestType.board);

        Timestamp created = rs.getTimestamp("r_createdAt");
        if (created != null) req.setR_createAt(created.toLocalDateTime());

        Timestamp updated = rs.getTimestamp("r_updatedAt");
        if (updated != null) req.setR_updateAt(updated.toLocalDateTime());

        return req;
    }
}
