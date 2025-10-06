package com.alzheimer.awareness.view.controllers;

import com.alzheimer.awareness.model.QuizResult;
import com.alzheimer.awareness.model.User;
import com.alzheimer.awareness.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class HistoryController {

    @FXML
    private ListView<QuizResult> historyListView;

    @FXML
    private Button backButton;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadHistory();
    }

    @FXML
    private void initialize() {
        backButton.setOnAction(event -> goBack());
    }

    private void loadHistory() {
        if (currentUser != null) {
            List<QuizResult> history = DatabaseService.getQuizHistory(currentUser.getId());
            historyListView.getItems().addAll(history);

            historyListView.setCellFactory(param -> new ListCell<QuizResult>() {
                @Override
                protected void updateItem(QuizResult item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Risk: " + item.getRiskIndicator() +
                                " - Date: " + item.getCompletedAt().toLocalDate());
                    }
                }
            });
        }
    }

    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
