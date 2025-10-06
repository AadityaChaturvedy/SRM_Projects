package com.hostel.controllers;

import com.hostel.database.ComplaintDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Complaint;
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

public class AddComplaintController {

    @FXML
    private TextField studentIdField;

    @FXML
    private TextField complaintField;

    @FXML
    private DatePicker dateField;

    @FXML
    private ComboBox<String> statusField;

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @FXML
    public void initialize() {
        statusField.setItems(FXCollections.observableArrayList("Pending", "Resolved"));
    }

    @FXML
    private void addComplaint() {
        String studentIdText = studentIdField.getText().trim();
        String complaintText = complaintField.getText().trim();
        LocalDate date = dateField.getValue();
        String status = statusField.getValue();

        if (studentIdText.isEmpty() || complaintText.isEmpty() || date == null || status == null || status.isEmpty()) {
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

            complaintDAO.createComplaint(new Complaint(0, studentId, complaintText, date.format(DateTimeFormatter.ISO_DATE), status));
            showAlert(Alert.AlertType.INFORMATION, "Success", "Complaint added successfully!");
            Stage stage = (Stage) studentIdField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Student ID must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding complaint: " + e.getMessage());
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