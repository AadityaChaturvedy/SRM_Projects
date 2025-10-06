package com.hostel.database;

import com.hostel.models.Payment;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO implements Observable<List<Payment>> {

    private List<Observer<List<Payment>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<Payment>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<Payment>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<Payment> data) {
        for (Observer<List<Payment>> observer : observers) {
            observer.update(data);
        }
    }

    public void createPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (student_id, amount, date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getStudentId());
            pstmt.setInt(2, payment.getAmount());
            pstmt.setString(3, payment.getDate());
            pstmt.setString(4, payment.getStatus());
            pstmt.executeUpdate();
            notifyObservers(getAllPayments());
        }
    }

    public List<Payment> getPaymentsByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE student_id = ?";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("amount"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return payments;
    }

    public List<Payment> getAllPayments() throws SQLException {
        String sql = "SELECT * FROM payments";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("amount"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return payments;
    }

    public void updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET student_id = ?, amount = ?, date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getStudentId());
            pstmt.setInt(2, payment.getAmount());
            pstmt.setString(3, payment.getDate());
            pstmt.setString(4, payment.getStatus());
            pstmt.setInt(5, payment.getId());
            pstmt.executeUpdate();
            notifyObservers(getAllPayments());
        }
    }

    public void deletePayment(int id) throws SQLException {
        String sql = "DELETE FROM payments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllPayments());
        }
    }

    public List<Payment> getPaymentsDueSoon(int days) throws SQLException {
        String sql = "SELECT * FROM payments WHERE date(date) BETWEEN date('now') AND date('now', '+? days') AND status = 'PENDING'";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("amount"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return payments;
    }

    public List<Payment> getOverduePayments() throws SQLException {
        String sql = "SELECT * FROM payments WHERE date(date) < date('now') AND status = 'PENDING'";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("amount"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }
        }
        return payments;
    }

    public java.util.Map<String, Integer> getPaymentStatusDistribution() throws SQLException {
        String sql = "SELECT status, COUNT(*) as count FROM payments GROUP BY status";
        java.util.Map<String, Integer> statusData = new java.util.HashMap<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                statusData.put(rs.getString("status"), rs.getInt("count"));
            }
        }
        return statusData;
    }

    public void populateMockData() throws SQLException {
        if (getAllPayments().isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                int amount = (int) (Math.random() * 1000) + 5000;
                String date = java.time.LocalDate.now().plusMonths(i % 3).toString();
                String status = (i % 2 == 0) ? "Paid" : "Pending";
                createPayment(new Payment(0, i, amount, date, status));
            }
        }
    }
}