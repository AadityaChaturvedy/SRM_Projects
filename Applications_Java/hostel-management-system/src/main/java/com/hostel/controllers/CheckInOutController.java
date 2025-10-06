package com.hostel.controllers;

import com.hostel.database.RoomDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Room;
import com.hostel.models.Student;
import com.hostel.utils.CheckInOutService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CheckInOutController {

    @FXML
    private TextField studentIdField;

    @FXML
    private TextField roomIdField;

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, Integer> studentIdColumn;

    @FXML
    private TableColumn<Student, String> studentNameColumn;

    @FXML
    private TableColumn<Student, Integer> allocatedRoomIdColumn;

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private TableColumn<Room, Integer> roomIdColumnTable;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, Integer> roomCapacityColumn;

    @FXML
    private TableColumn<Room, String> roomStatusColumn;

    private final StudentDAO studentDAO = new StudentDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final CheckInOutService checkInOutService = new CheckInOutService();

    @FXML
    private void initialize() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        allocatedRoomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        roomIdColumnTable.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        roomStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadStudents();
        loadRooms();
    }

    private void loadStudents() {
        try {
            ObservableList<Student> students = FXCollections.observableArrayList(studentDAO.getAllStudents());
            studentTable.setItems(students);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    private void checkIn() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            int roomId = Integer.parseInt(roomIdField.getText());

            if (checkInOutService.checkInStudent(studentId, roomId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student checked in successfully.");
                loadStudents();
                loadRooms();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Check-in failed. Check student ID, room ID, or room capacity.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Student ID and Room ID must be numbers.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during check-in.");
        }
    }

    @FXML
    private void checkOut() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());

            if (checkInOutService.checkOutStudent(studentId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student checked out successfully.");
                loadStudents();
                loadRooms();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Check-out failed. Student might not be checked in.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Student ID must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred during check-out.");
        }
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) studentIdField.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
