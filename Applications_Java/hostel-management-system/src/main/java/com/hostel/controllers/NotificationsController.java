package com.hostel.controllers;

import com.hostel.database.NotificationDAO;
import com.hostel.models.Notification;
import com.hostel.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class NotificationsController implements Observer<List<Notification>> {

    @FXML
    private TableView<Notification> notificationTable;

    @FXML
    private TableColumn<Notification, Integer> idColumn;

    @FXML
    private TableColumn<Notification, String> messageColumn;

    @FXML
    private TableColumn<Notification, String> timestampColumn;

    @FXML
    private TableColumn<Notification, Boolean> isReadColumn;

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        isReadColumn.setCellValueFactory(new PropertyValueFactory<>("isRead"));

        notificationDAO.addObserver(this);
        loadNotifications();
    }

    @Override
    public void update(List<Notification> notifications) {
        notificationTable.setItems(FXCollections.observableArrayList(notifications));
    }

    private void loadNotifications() {
        try {
            ObservableList<Notification> notifications = FXCollections.observableArrayList(notificationDAO.getAllNotifications());
            notificationTable.setItems(notifications);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void markAsRead() {
        Notification selectedNotification = notificationTable.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            try {
                notificationDAO.markNotificationAsRead(selectedNotification.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteNotification() {
        Notification selectedNotification = notificationTable.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Notification");
            alert.setHeaderText("Are you sure you want to delete this notification?");
            alert.setContentText(selectedNotification.getMessage());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        notificationDAO.deleteNotification(selectedNotification.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) notificationTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
