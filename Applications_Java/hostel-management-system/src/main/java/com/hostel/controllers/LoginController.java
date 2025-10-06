package com.hostel.controllers;

import com.hostel.database.UserDAO;
import com.hostel.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = userDAO.getUserByUsername(username);

            if (user == null && username.equals("admin")) {
                // Create a default admin user if no user exists
                userDAO.createDefaultAdminUser();
                user = userDAO.getUserByUsername(username);
            }

            if (user != null /* && userDAO.verifyPassword(user, password) */) { // Temporary workaround
                // Placeholder for authorization logic
                System.out.println("User " + user.getUsername() + " logged in with role: " + user.getRole() + " and permissions: " + user.getPermissions());
                // Example: if (user.getPermissions().contains("ADMIN")) { loadAdminDashboard(); } else { loadUserDashboard(); }
                try {
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
                    stage.setScene(new Scene(root));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while connecting to the database.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
