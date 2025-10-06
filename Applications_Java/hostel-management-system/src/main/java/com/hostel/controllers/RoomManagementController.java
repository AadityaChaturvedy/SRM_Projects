package com.hostel.controllers;

import com.hostel.database.RoomDAO;
import com.hostel.models.Room;
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

public class RoomManagementController implements Observer<List<Room>> {

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private TableColumn<Room, Integer> idColumn;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, Integer> capacityColumn;

    @FXML
    private TableColumn<Room, String> statusColumn;

    private final RoomDAO roomDAO = new RoomDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        roomDAO.addObserver(this);
        loadRooms();
    }

    @Override
    public void update(List<Room> rooms) {
        roomTable.setItems(FXCollections.observableArrayList(rooms));
    }

    private void loadRooms() {
        try {
            ObservableList<Room> rooms = FXCollections.observableArrayList(roomDAO.getAllRooms());
            roomTable.setItems(rooms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addRoom() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddRoom.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Room");
            stage.showAndWait();
            loadRooms();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editRoom() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditRoom.fxml"));
                Parent root = loader.load();

                EditRoomController controller = loader.getController();
                controller.setRoom(selectedRoom);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit Room");
                stage.showAndWait();
                loadRooms();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteRoom() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Room");
            alert.setHeaderText("Are you sure you want to delete this room?");
            alert.setContentText(selectedRoom.getRoomNumber());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        roomDAO.deleteRoom(selectedRoom.getId());
                        loadRooms();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) roomTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
