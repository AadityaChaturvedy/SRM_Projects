package com.hostel.database;

import com.hostel.models.Student;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements Observable<List<Student>> {

    private List<Observer<List<Student>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<Student>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<Student>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<Student> data) {
        for (Observer<List<Student>> observer : observers) {
            observer.update(data);
        }
    }

    public void createStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, email, phone, address, room_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setString(4, student.getAddress());
            pstmt.setInt(5, student.getRoomId());
            pstmt.executeUpdate();
            notifyObservers(getAllStudents());
        }
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("room_id")
                );
            }
        }
        return null;
    }

    public Student getStudentByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("room_id")
                );
            }
        }
        return null;
    }

    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("room_id")
                ));
            }
        }
        return students;
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, address = ?, room_id = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setString(4, student.getAddress());
            pstmt.setInt(5, student.getRoomId());
            pstmt.setInt(6, student.getId());
            pstmt.executeUpdate();
            notifyObservers(getAllStudents());
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllStudents());
        }
    }

    public int getOccupiedCountForRoom(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE room_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void populateMockData() throws SQLException {
        if (getAllStudents().isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                String name = "Student " + i;
                String email = "student" + i + "@example.com";
                String phone = "123456789" + i;
                String address = "Address " + i;
                int roomId = i;
                createStudent(new Student(0, name, email, phone, address, roomId));
            }
        }
    }
}
