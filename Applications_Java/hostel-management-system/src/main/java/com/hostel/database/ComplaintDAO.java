package com.hostel.database;

import com.hostel.models.Complaint;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO implements Observable<List<Complaint>> {

    private List<Observer<List<Complaint>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<Complaint>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<Complaint>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<Complaint> data) {
        for (Observer<List<Complaint>> observer : observers) {
            observer.update(data);
        }
    }

    public void createComplaint(Complaint complaint) throws SQLException {
        String sql = "INSERT INTO complaints (student_id, complaint, date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, complaint.getStudentId());
            pstmt.setString(2, complaint.getComplaintText());
            pstmt.setString(3, complaint.getDate());
            pstmt.setString(4, complaint.getStatus());
            pstmt.executeUpdate();
            notifyObservers(getAllComplaints());
        }
    }

    public List<Complaint> getComplaintsByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM complaints WHERE student_id = ?";
        List<Complaint> complaints = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                complaints.add(new Complaint(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("complaint"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return complaints;
    }

    public List<Complaint> getAllComplaints() throws SQLException {
        String sql = "SELECT * FROM complaints";
        List<Complaint> complaints = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                complaints.add(new Complaint(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("complaint"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return complaints;
    }

    public void updateComplaint(Complaint complaint) throws SQLException {
        String sql = "UPDATE complaints SET student_id = ?, complaint = ?, date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, complaint.getStudentId());
            pstmt.setString(2, complaint.getComplaintText());
            pstmt.setString(3, complaint.getDate());
            pstmt.setString(4, complaint.getStatus());
            pstmt.setInt(5, complaint.getId());
            pstmt.executeUpdate();
            notifyObservers(getAllComplaints());
        }
    }

    public void deleteComplaint(int id) throws SQLException {
        String sql = "DELETE FROM complaints WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllComplaints());
        }
    }

    public void populateMockData() throws SQLException {
        if (getAllComplaints().isEmpty()) {
            System.out.println("Populating complaints with mock data...");
            createComplaint(new Complaint(0, 1, "Leaky faucet in room 101", "2025-10-20", "Pending"));
            createComplaint(new Complaint(0, 2, "Broken light in hallway", "2025-10-22", "Resolved"));
            createComplaint(new Complaint(0, 3, "Clogged toilet in bathroom 2", "2025-10-25", "Pending"));
            createComplaint(new Complaint(0, 4, "No hot water in shower", "2025-10-26", "In Progress"));
            createComplaint(new Complaint(0, 5, "Wi-Fi not working in Block B", "2025-10-27", "Resolved"));
            createComplaint(new Complaint(0, 1, "Leaky faucet in room 101", "2025-10-28", "Pending")); // Recurring issue
            createComplaint(new Complaint(0, 2, "Broken light in hallway", "2025-10-29", "Pending")); // Recurring issue
            createComplaint(new Complaint(0, 3, "Clogged toilet in bathroom 2", "2025-10-30", "Pending")); // Recurring issue
            System.out.println("Mock data for complaints populated.");
        }
    }
}
