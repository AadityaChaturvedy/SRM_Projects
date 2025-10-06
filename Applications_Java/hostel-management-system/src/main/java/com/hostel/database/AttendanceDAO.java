package com.hostel.database;

import com.hostel.models.Attendance;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO implements Observable<List<Attendance>> {

    private List<Observer<List<Attendance>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<Attendance>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<Attendance>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<Attendance> data) {
        for (Observer<List<Attendance>> observer : observers) {
            observer.update(data);
        }
    }

    public void createAttendance(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setString(2, attendance.getDate());
            pstmt.setString(3, attendance.getStatus());
            pstmt.executeUpdate();
            notifyObservers(getAllAttendance());
        }
    }

    public List<Attendance> getAttendanceByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE student_id = ?";
        List<Attendance> attendances = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(new Attendance(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return attendances;
    }

    public List<Attendance> getAllAttendance() throws SQLException {
        String sql = "SELECT * FROM attendance";
        List<Attendance> attendances = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(new Attendance(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return attendances;
    }

    public void updateAttendance(Attendance attendance) throws SQLException {
        String sql = "UPDATE attendance SET student_id = ?, date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setString(2, attendance.getDate());
            pstmt.setString(3, attendance.getStatus());
            pstmt.setInt(4, attendance.getId());
            pstmt.executeUpdate();
            notifyObservers(getAllAttendance());
        }
    }

    public void deleteAttendance(int id) throws SQLException {
        String sql = "DELETE FROM attendance WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllAttendance());
        }
    }

    public void populateMockData() throws SQLException {
        if (getAllAttendance().isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                for (int j = 0; j < 7; j++) {
                    String date = java.time.LocalDate.now().minusDays(j).toString();
                    String status = (Math.random() < 0.9) ? "Present" : "Absent";
                    createAttendance(new Attendance(0, i, date, status));
                }
            }
        }
    }
}
