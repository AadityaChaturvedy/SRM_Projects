package com.hostel.controllers;

import com.hostel.database.PaymentDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Payment;
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

public class AddPaymentController {

    @FXML
    private TextField studentIdField;

    @FXML
    private TextField amountField;

    @FXML
    private DatePicker dateField;

    @FXML
    private ComboBox<String> statusField;

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @FXML
    public void initialize() {
        statusField.setItems(FXCollections.observableArrayList("Paid", "Pending"));
    }

    @FXML
    private void addPayment() {
        String studentIdText = studentIdField.getText().trim();
        String amountText = amountField.getText().trim();
        LocalDate date = dateField.getValue();
        String status = statusField.getValue();

        if (studentIdText.isEmpty() || amountText.isEmpty() || date == null || status == null || status.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdText);
            int amount = Integer.parseInt(amountText);

            // Validate Student ID exists
            Student student = studentDAO.getStudentById(studentId);
            if (student == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Student with ID " + studentId + " does not exist.");
                return;
            }

            // Validate amount
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be a positive number.");
                return;
            }

            paymentDAO.createPayment(new Payment(0, studentId, amount, date.format(DateTimeFormatter.ISO_DATE), status));
            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment added successfully!");
            Stage stage = (Stage) studentIdField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Student ID and Amount must be numbers.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding payment: " + e.getMessage());
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
