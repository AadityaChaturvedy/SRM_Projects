package com.hostel.controllers;

import com.hostel.database.RoomDAO;
import com.hostel.models.Room;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddRoomController {

    @FXML
    private TextField roomNumberField;

    @FXML
    private TextField capacityField;

    private final RoomDAO roomDAO = new RoomDAO();

    @FXML
    private void addRoom() {
        String roomNumber = roomNumberField.getText();
        String capacityText = capacityField.getText();

        if (roomNumber.isEmpty() || capacityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        try {
            int capacity = Integer.parseInt(capacityText);
            roomDAO.createRoom(new Room(0, roomNumber, capacity, "Available"));
            Stage stage = (Stage) roomNumberField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Capacity must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding room.");
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
        Stage stage = (Stage) roomNumberField.getScene().getWindow();
        stage.close();
    }
}