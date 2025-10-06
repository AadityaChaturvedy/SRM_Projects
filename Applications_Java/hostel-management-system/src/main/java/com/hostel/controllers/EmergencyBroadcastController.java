package com.hostel.controllers;

import com.hostel.database.NotificationDAO;
import com.hostel.database.UserDAO;
import com.hostel.models.Notification;
import com.hostel.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmergencyBroadcastController {

    @FXML
    private TextArea messageArea;

    private NotificationDAO notificationDAO;
    private UserDAO userDAO;

    @FXML
    public void initialize() {
        notificationDAO = new NotificationDAO();
        userDAO = new UserDAO();
    }

    @FXML
    private void handleBroadcast() {
        String message = messageArea.getText();
        if (message == null || message.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Message", "Please enter a message to broadcast.");
            return;
        }

        try {
            List<User> allUsers = userDAO.getAllUsers();
            for (User user : allUsers) {
                Notification notification = new Notification(0, user.getId(), message, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), false);
                notificationDAO.createNotification(notification);
            }
            showAlert(Alert.AlertType.INFORMATION, "Broadcast Sent", "The emergency message has been sent to all users.");
            messageArea.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to send broadcast.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        ((javafx.stage.Stage) messageArea.getScene().getWindow()).close();
    }
}
