package com.alzheimer.awareness;

import com.alzheimer.awareness.service.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:alzheimer_app.db")) {
            DatabaseService.executeSqlScript(conn, "/db/schema.sql");
            DatabaseService.executeSqlScript(conn, "/db/seed.sql");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}