package com.alzheimer.awareness.view.controllers;

import com.alzheimer.awareness.model.User;
import com.alzheimer.awareness.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        User existingUser = DatabaseService.loginUser(username, password);
        if (existingUser != null) {
            errorLabel.setText("Username already exists.");
            return;
        }

        User newUser = DatabaseService.registerUser(username, password);

        if (newUser != null) {
            // Registration successful, close window
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Registration failed. Please try again.");
        }
    }
}
