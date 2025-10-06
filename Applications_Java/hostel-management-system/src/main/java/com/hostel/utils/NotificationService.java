package com.hostel.utils;

import com.hostel.database.NotificationDAO;
import com.hostel.models.Notification;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    public void createNotification(int userId, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Notification notification = new Notification(0, userId, message, timestamp, false);
        try {
            notificationDAO.createNotification(notification);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}