package com.hostel.database;

import com.hostel.models.Visitor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitorDAO {

    public void createVisitor(Visitor visitor) throws SQLException {
        String sql = "INSERT INTO visitors (student_id, visitor_name, relation, visit_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, visitor.getStudentId());
            pstmt.setString(2, visitor.getVisitorName());
            pstmt.setString(3, visitor.getRelation());
            pstmt.setString(4, visitor.getVisitDate());
            pstmt.setString(5, visitor.getStatus());
            pstmt.executeUpdate();
        }
    }

    public List<Visitor> getAllVisitors() throws SQLException {
        String sql = "SELECT v.*, s.name as student_name FROM visitors v JOIN students s ON v.student_id = s.id ORDER BY v.visit_date DESC";
        List<Visitor> visitors = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Visitor visitor = new Visitor();
                visitor.setId(rs.getInt("id"));
                visitor.setStudentId(rs.getInt("student_id"));
                visitor.setStudentName(rs.getString("student_name"));
                visitor.setVisitorName(rs.getString("visitor_name"));
                visitor.setRelation(rs.getString("relation"));
                visitor.setVisitDate(rs.getString("visit_date"));
                visitor.setStatus(rs.getString("status"));
                visitors.add(visitor);
            }
        }
        return visitors;
    }

    public void updateVisitorStatus(int visitorId, String status) throws SQLException {
        String sql = "UPDATE visitors SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, visitorId);
            pstmt.executeUpdate();
        }
    }

    public void populateMockData() {
        try {
            List<Visitor> visitors = getAllVisitors();
            if (visitors.isEmpty()) {
                System.out.println("Populating visitors with mock data...");
                Visitor v1 = new Visitor();
                v1.setStudentId(1);
                v1.setVisitorName("John Doe");
                v1.setRelation("Father");
                v1.setVisitDate("2025-11-01");
                v1.setStatus("Checked In");
                createVisitor(v1);

                Visitor v2 = new Visitor();
                v2.setStudentId(2);
                v2.setVisitorName("Jane Smith");
                v2.setRelation("Mother");
                v2.setVisitDate("2025-11-02");
                v2.setStatus("Checked Out");
                createVisitor(v2);

                Visitor v3 = new Visitor();
                v3.setStudentId(1);
                v3.setVisitorName("Peter Jones");
                v3.setRelation("Friend");
                v3.setVisitDate("2025-11-03");
                v3.setStatus("Pending");
                createVisitor(v3);

                System.out.println("Mock data for visitors populated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
