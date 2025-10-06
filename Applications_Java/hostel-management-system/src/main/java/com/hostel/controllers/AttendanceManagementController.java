package com.hostel.controllers;

import com.hostel.database.AttendanceDAO;
import com.hostel.models.Attendance;
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

public class AttendanceManagementController {

    @FXML
    private TableView<Attendance> attendanceTable;

    @FXML
    private TableColumn<Attendance, Integer> idColumn;

    @FXML
    private TableColumn<Attendance, Integer> studentIdColumn;

    @FXML
    private TableColumn<Attendance, String> dateColumn;

    @FXML
    private TableColumn<Attendance, String> statusColumn;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        attendanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadAttendance();
    }

    private void loadAttendance() {
        try {
            ObservableList<Attendance> attendances = FXCollections.observableArrayList(attendanceDAO.getAllAttendance());
            attendanceTable.setItems(attendances);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addAttendance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddAttendance.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Attendance");
            stage.showAndWait();
            loadAttendance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editAttendance() {
        Attendance selectedAttendance = attendanceTable.getSelectionModel().getSelectedItem();
        if (selectedAttendance != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditAttendance.fxml"));
                Parent root = loader.load();

                EditAttendanceController controller = loader.getController();
                controller.setAttendance(selectedAttendance);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit Attendance");
                stage.showAndWait();
                loadAttendance();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteAttendance() {
        Attendance selectedAttendance = attendanceTable.getSelectionModel().getSelectedItem();
        if (selectedAttendance != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Attendance");
            alert.setHeaderText("Are you sure you want to delete this attendance record?");
            alert.setContentText("Student ID: " + selectedAttendance.getStudentId() + ", Date: " + selectedAttendance.getDate());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        attendanceDAO.deleteAttendance(selectedAttendance.getId());
                        loadAttendance();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) attendanceTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
