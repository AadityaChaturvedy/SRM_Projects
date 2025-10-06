package com.hostel.database;

import com.hostel.models.Notification;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO implements Observable<List<Notification>> {

    private List<Observer<List<Notification>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<Notification>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<Notification>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<Notification> data) {
        for (Observer<List<Notification>> observer : observers) {
            observer.update(data);
        }
    }

    public void createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, message, timestamp, is_read) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getMessage());
            pstmt.setString(3, notification.getTimestamp());
            pstmt.setBoolean(4, notification.isRead());
            pstmt.executeUpdate();
            notifyObservers(getAllNotifications());
        }
    }

    public List<Notification> getAllNotifications() throws SQLException {
        String sql = "SELECT * FROM notifications ORDER BY timestamp DESC";
        List<Notification> notifications = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getString("timestamp"),
                        rs.getBoolean("is_read")
                ));
            }
        }
        return notifications;
    }

    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY timestamp DESC";
        List<Notification> notifications = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("message"),
                            rs.getString("timestamp"),
                            rs.getBoolean("is_read")
                    ));
                }
            }
        }
        return notifications;
    }

    public void markNotificationAsRead(int id) throws SQLException {
        String sql = "UPDATE notifications SET is_read = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, true);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            notifyObservers(getAllNotifications());
        }
    }

    public void deleteNotification(int id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllNotifications());
        }
    }
}