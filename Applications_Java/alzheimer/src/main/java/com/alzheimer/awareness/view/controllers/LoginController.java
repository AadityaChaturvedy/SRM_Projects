package com.alzheimer.awareness.view.controllers;

import com.alzheimer.awareness.model.User;
import com.alzheimer.awareness.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        registerLink.setOnAction(event -> openRegisterView());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        User user = DatabaseService.loginUser(username, password);

        if (user != null) {
            openMainApp(user);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    private void openMainApp(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setCurrentUser(user);

            Stage stage = new Stage();
            stage.setTitle("Alzheimer's Awareness App");
            stage.setScene(new Scene(root, 800, 600));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRegisterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root, 800, 600));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
