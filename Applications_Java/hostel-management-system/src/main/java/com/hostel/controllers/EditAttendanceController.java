package com.hostel.controllers;

import com.hostel.database.AttendanceDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Attendance;
import com.hostel.models.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditAttendanceController {

    @FXML
    private TextField studentIdField;

    @FXML
    private DatePicker dateField;

    @FXML
    private ComboBox<String> statusField;

    private Attendance attendance;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @FXML
    public void initialize() {
        statusField.setItems(FXCollections.observableArrayList("Present", "Absent"));
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
        studentIdField.setText(String.valueOf(attendance.getStudentId()));
        dateField.setValue(LocalDate.parse(attendance.getDate()));
        statusField.setValue(attendance.getStatus());
    }

    @FXML
    private void editAttendance() {
        String studentIdText = studentIdField.getText().trim();
        LocalDate date = dateField.getValue();
        String status = statusField.getValue();

        if (studentIdText.isEmpty() || date == null || status == null || status.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdText);

            // Validate Student ID exists
            Student student = studentDAO.getStudentById(studentId);
            if (student == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Student with ID " + studentId + " does not exist.");
                return;
            }

            attendance.setStudentId(studentId);
            attendance.setDate(date.format(DateTimeFormatter.ISO_DATE));
            attendance.setStatus(status);

            attendanceDAO.updateAttendance(attendance);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Attendance updated successfully!");
            Stage stage = (Stage) studentIdField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Student ID must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating attendance: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) studentIdField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
