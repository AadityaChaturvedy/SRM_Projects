package com.hostel.controllers;

import com.hostel.database.RoomDAO;
import com.hostel.models.Room;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EditRoomController {

    @FXML
    private TextField roomNumberField;

    @FXML
    private TextField capacityField;

    private Room room;

    private final RoomDAO roomDAO = new RoomDAO();

    public void setRoom(Room room) {
        this.room = room;
        roomNumberField.setText(room.getRoomNumber());
        capacityField.setText(String.valueOf(room.getCapacity()));
    }

    @FXML
    private void editRoom() {
        room.setRoomNumber(roomNumberField.getText());
        room.setCapacity(Integer.parseInt(capacityField.getText()));

        try {
            roomDAO.updateRoom(room);
            Stage stage = (Stage) roomNumberField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) roomNumberField.getScene().getWindow();
        stage.close();
    }
}
