package com.hostel.controllers;

import com.hostel.database.RoomDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Room;
import com.hostel.models.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditStudentController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField roomIdField;

    private Student student;

    private final StudentDAO studentDAO = new StudentDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String PHONE_REGEX = "^[0-9]{10}$"; // Assuming 10-digit phone number
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public void setStudent(Student student) {
        this.student = student;
        nameField.setText(student.getName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        addressField.setText(student.getAddress());
        roomIdField.setText(String.valueOf(student.getRoomId()));
    }

    @FXML
    private void editStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String roomIdText = roomIdField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || roomIdText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        // Name validation
        if (!name.matches("[a-zA-Z\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Name can only contain alphabetic characters and spaces.");
            return;
        }

        // Email validation
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email address.");
            return;
        }

        // Phone validation
        Matcher phoneMatcher = PHONE_PATTERN.matcher(phone);
        if (!phoneMatcher.matches()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid 10-digit phone number.");
            return;
        }

        try {
            // Check for unique email (excluding the current student)
            Student existingStudent = studentDAO.getStudentByEmail(email);
            if (existingStudent != null && existingStudent.getId() != student.getId()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "A student with this email already exists.");
                return;
            }

            int roomId = Integer.parseInt(roomIdText);

            // Check if room exists
            Room room = roomDAO.getRoomById(roomId);
            if (room == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Room with ID " + roomId + " does not exist.");
                return;
            }

            student.setName(name);
            student.setEmail(email);
            student.setPhone(phone);
            student.setAddress(address);
            student.setRoomId(roomId);

            studentDAO.updateStudent(student);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully!");
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Room ID must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update student: " + e.getMessage());
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
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
