package com.hostel.database;

import com.hostel.models.LeaveRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    public void createLeaveRequest(LeaveRequest leaveRequest) throws SQLException {
        String sql = "INSERT INTO leave_requests (student_id, start_date, end_date, reason, status, request_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, leaveRequest.getStudentId());
            pstmt.setString(2, leaveRequest.getStartDate());
            pstmt.setString(3, leaveRequest.getEndDate());
            pstmt.setString(4, leaveRequest.getReason());
            pstmt.setString(5, leaveRequest.getStatus());
            pstmt.setString(6, leaveRequest.getRequestDate());
            pstmt.executeUpdate();
        }
    }

    public List<LeaveRequest> getAllLeaveRequests() throws SQLException {
        String sql = "SELECT l.*, s.name as student_name FROM leave_requests l JOIN students s ON l.student_id = s.id ORDER BY l.request_date DESC";
        List<LeaveRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LeaveRequest request = new LeaveRequest();
                request.setId(rs.getInt("id"));
                request.setStudentId(rs.getInt("student_id"));
                request.setStudentName(rs.getString("student_name"));
                request.setStartDate(rs.getString("start_date"));
                request.setEndDate(rs.getString("end_date"));
                request.setReason(rs.getString("reason"));
                request.setStatus(rs.getString("status"));
                request.setRequestDate(rs.getString("request_date"));
                requests.add(request);
            }
        }
        return requests;
    }

    public void updateLeaveRequestStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE leave_requests SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();
        }
    }

    public void populateMockData() {
        try {
            List<LeaveRequest> requests = getAllLeaveRequests();
            if (requests.isEmpty()) {
                System.out.println("Populating leave requests with mock data...");
                createLeaveRequest(new LeaveRequest(0, 1, "2025-11-01", "2025-11-05", "Family event", "Pending", "2025-10-20"));
                createLeaveRequest(new LeaveRequest(0, 2, "2025-11-10", "2025-11-12", "Medical appointment", "Approved", "2025-10-22"));
                createLeaveRequest(new LeaveRequest(0, 3, "2025-12-20", "2026-01-10", "Winter vacation", "Rejected", "2025-10-25"));
                System.out.println("Mock data for leave requests populated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
