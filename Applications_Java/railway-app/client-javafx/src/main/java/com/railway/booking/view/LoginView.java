package com.railway.booking.view;

import com.google.gson.JsonObject;
import com.railway.booking.service.ApiClient;
import com.railway.booking.util.UIUtils;
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
import java.util.function.Consumer;

public class LoginView {

    private final ApiClient apiClient = new ApiClient();

    public void show(Stage stage, Consumer<String> onLoginSuccess, Runnable onRegister) {
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("card");
        Label title = new Label("🚂 Railway Booking System");
        title.getStyleClass().add("title-label");
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.getStyleClass().add("search-card");

        TextField userField = new TextField();
        userField.setPromptText("Enter Username");
        userField.setPrefWidth(250);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter Password");
        passField.setPrefWidth(250);

        Button loginBtn = new Button("🔐 Login");
        loginBtn.setPrefWidth(150);

        Button regBtn = new Button("👤 Register");
        regBtn.setPrefWidth(150);
        regBtn.getStyleClass().add("secondary-button");

        Label msg = new Label();
        msg.getStyleClass().add("error-label");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginBtn, regBtn);

        loginBtn.setOnAction(e -> {
            if (userField.getText().trim().isEmpty() || passField.getText().isEmpty()) {
                msg.setText("Please fill all fields");
                return;
            }
            Map<String, String> data = Map.of(
                    "username", userField.getText().trim(),
                    "password", passField.getText()
            );
            try {
                JsonObject json = apiClient.post("/login", data);
                if (json.get("success").getAsBoolean()) {
                    int userId = json.get("user_id").getAsInt();
                    String username = userField.getText().trim();
                    onLoginSuccess.accept(userId + ":" + username);
                } else {
                    msg.setText("❌ Invalid credentials. Please try again.");
                }
            } catch (Exception ex) {
                msg.setText("❌ Connection error: " + ex.getMessage());
            }
        });
        regBtn.setOnAction(e -> onRegister.run());
        passField.setOnAction(e -> loginBtn.fire());
        userField.setOnAction(e -> passField.requestFocus());

        formBox.getChildren().addAll(
                new Label("Login to Your Account"),
                userField, passField, buttonBox, msg
        );
        layout.getChildren().addAll(title, formBox);
        Scene scene = new Scene(layout, 450, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
