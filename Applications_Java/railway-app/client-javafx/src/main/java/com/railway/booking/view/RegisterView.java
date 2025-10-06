package com.railway.booking.view;

import com.google.gson.JsonObject;
import com.railway.booking.service.ApiClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class RegisterView {

    private final ApiClient apiClient = new ApiClient();

    public void show(Stage stage, Runnable onBackToLogin) {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("card");
        Label title = new Label("👤 Create New Account");
        title.getStyleClass().add("title-label");
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.getStyleClass().add("search-card");

        TextField userField = new TextField();
        userField.setPromptText("Choose Username");
        userField.setPrefWidth(250);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Create Password");
        passField.setPrefWidth(250);

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm Password");
        confirmPassField.setPrefWidth(250);

        Button regBtn = new Button("✅ Create Account");
        regBtn.setPrefWidth(150);
        Button backBtn = new Button("⬅ Back to Login");
        backBtn.setPrefWidth(150);
        backBtn.getStyleClass().add("secondary-button");
        Label msg = new Label();

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(regBtn, backBtn);

        regBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText();
            String confirmPassword = confirmPassField.getText();
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                msg.setText("❌ Please fill all fields");
                msg.getStyleClass().setAll("error-label");
                return;
            }
            if (!password.equals(confirmPassword)) {
                msg.setText("❌ Passwords do not match");
                msg.getStyleClass().setAll("error-label");
                return;
            }
            if (password.length() < 4) {
                msg.setText("❌ Password must be at least 4 characters");
                msg.getStyleClass().setAll("error-label");
                return;
            }
            Map<String, String> data = Map.of("username", username, "password", password);
            try {
                JsonObject json = apiClient.post("/register", data);
                if (json.get("success").getAsBoolean()) {
                    msg.setText("✅ Registration successful! You can now login.");
                    msg.getStyleClass().setAll("success-label");
                } else {
                    msg.setText("❌ Username already exists. Choose another.");
                    msg.getStyleClass().setAll("error-label");
                }
            } catch (Exception ex) {
                msg.setText("❌ Registration failed: " + ex.getMessage());
                msg.getStyleClass().setAll("error-label");
            }
        });
        backBtn.setOnAction(e -> onBackToLogin.run());
        formBox.getChildren().addAll(
                new Label("Join Railway Booking System"),
                userField, passField, confirmPassField, buttonBox, msg
        );
        layout.getChildren().addAll(title, formBox);
        Scene scene = new Scene(layout, 450, 550);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
