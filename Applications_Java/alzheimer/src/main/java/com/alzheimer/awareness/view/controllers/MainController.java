package com.alzheimer.awareness.view.controllers;

import com.alzheimer.awareness.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    @FXML private Button startQuizBtn;
    @FXML private Button findHospitalsBtn;
    @FXML private Button educationalResourcesBtn;
    @FXML private Button viewHistoryBtn;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void initialize() {
        startQuizBtn.setOnAction(e -> openQuiz());
        findHospitalsBtn.setOnAction(e -> openHospitalFinder());
        educationalResourcesBtn.setOnAction(e -> showEducationalResources());
        viewHistoryBtn.setOnAction(e -> openHistory());
    }

    private void openQuiz() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quiz.fxml"));
            Parent root = loader.load();

            QuizController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Alzheimer's Self-Assessment");
            stage.setScene(new Scene(root, 800, 600));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.show();

        } catch (Exception e) {
            System.err.println("Failed to open quiz: " + e.getMessage());
        }
    }

    private void openHospitalFinder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/hospital_finder.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Find Alzheimer's Care Centers");
            stage.setScene(new Scene(root, 900, 650)); // Made window larger
            stage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.show();

        } catch (Exception e) {
            System.err.println("Failed to open hospital finder: " + e.getMessage());
        }
    }

    private void showEducationalResources() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/educational_resources.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Educational Resources");
            stage.setScene(new Scene(root, 1000, 900));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.show();

        } catch (Exception e) {
            System.err.println("Failed to open educational resources: " + e.getMessage());
        }
    }

    private void openHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/history.fxml"));
            Parent root = loader.load();

            HistoryController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Patient Quiz History");
            stage.setScene(new Scene(root, 800, 600));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.show();

        } catch (Exception e) {
            System.err.println("Failed to open history: " + e.getMessage());
        }
    }
}
