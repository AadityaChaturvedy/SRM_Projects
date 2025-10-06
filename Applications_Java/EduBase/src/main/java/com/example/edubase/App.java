package com.example.edubase;

import com.example.edubase.services.DatabaseService;
import com.example.edubase.utils.AlertUtils;
import com.example.edubase.utils.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            DatabaseService.initializeDatabase();
        } catch (Exception e) {
            AlertUtils.showError("Database Error", "Failed to initialize the database: " + e.getMessage());
            return; // Exit if DB fails
        }

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 400);

    ThemeManager.Theme savedTheme = ThemeManager.loadThemePreference();
        ThemeManager.applyTheme(scene, savedTheme);

    stage.setTitle("EduBase - Login");
        
        stage.getIcons().add(
            new Image(App.class.getResourceAsStream("/com/example/edubase/icons/app.png"))
        );
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}