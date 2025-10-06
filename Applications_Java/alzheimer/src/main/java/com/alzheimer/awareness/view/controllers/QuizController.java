package com.alzheimer.awareness.view.controllers;

import com.alzheimer.awareness.model.Question;
import com.alzheimer.awareness.model.QuizResult;
import com.alzheimer.awareness.model.User;
import com.alzheimer.awareness.service.DatabaseService;
import com.alzheimer.awareness.viewmodel.QuizViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class QuizController {

    @FXML private Label questionNumberLabel;
    @FXML private Label questionLabel;
    @FXML private Button optionA;
    @FXML private Button optionB;
    @FXML private Button optionC;
    @FXML private Button optionD;
    @FXML private Button nextBtn;
    @FXML private Button previousBtn;
    @FXML private Button restartBtn;
    @FXML private ProgressBar progressBar;
    @FXML private VBox resultsPane;
    @FXML private Label scoreLabel;
    @FXML private Label riskLabel;
    @FXML private Label recommendationLabel;

    private QuizViewModel viewModel;
    private Button[] optionButtons;
    private int selectedOption = -1;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void initialize() {
        viewModel = new QuizViewModel();
        optionButtons = new Button[]{optionA, optionB, optionC, optionD};

        for (int i = 0; i < optionButtons.length; i++) {
            final int optionIndex = i;
            optionButtons[i].setOnAction(e -> selectOption(optionIndex));
        }

        nextBtn.setOnAction(e -> handleNext());
        previousBtn.setOnAction(e -> handlePrevious());
        restartBtn.setOnAction(e -> restartQuiz());

        resultsPane.setVisible(false);
        loadCurrentQuestion();
        updateUI();
    }

    private void selectOption(int optionIndex) {
        selectedOption = optionIndex;
        viewModel.answerCurrentQuestion(optionIndex);
        updateOptionStyles();
    }

    private void handleNext() {
        if (selectedOption == -1) {
            return;
        }
        
        if (viewModel.hasNextQuestion()) {
            viewModel.nextQuestion();
            loadCurrentQuestion();
            updateUI();
        } else {
            showResults();
        }
    }

    private void handlePrevious() {
        if (viewModel.hasPreviousQuestion()) {
            viewModel.previousQuestion();
            loadCurrentQuestion();
            updateUI();
        }
    }

    private void loadCurrentQuestion() {
        Question currentQuestion = viewModel.getCurrentQuestion();
        if (currentQuestion != null) {
            questionLabel.setText(currentQuestion.getQuestion());

            String[] options = currentQuestion.getOptions();
            for (int i = 0; i < optionButtons.length; i++) {
                if (i < options.length) {
                    optionButtons[i].setText(options[i]);
                    optionButtons[i].setVisible(true);
                } else {
                    optionButtons[i].setVisible(false);
                }
            }

            // Restore selection (if any)
            selectedOption = viewModel.getResponse(viewModel.getCurrentQuestionIndex());
            updateOptionStyles();
        }
    }

    private void updateOptionStyles() {
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].getStyleClass().removeAll("quiz-option-selected");
            if (i == selectedOption) {
                optionButtons[i].getStyleClass().add("quiz-option-selected");
            }
        }
    }

    private void updateUI() {
        questionNumberLabel.setText(String.format("Question %d of %d",
                viewModel.getCurrentQuestionIndex() + 1,
                viewModel.getNumQuestions()));

        double progress = (double) (viewModel.getCurrentQuestionIndex() + 1) / viewModel.getNumQuestions();
        progressBar.setProgress(progress);

        previousBtn.setVisible(viewModel.hasPreviousQuestion());

        if (viewModel.hasNextQuestion()) {
            nextBtn.setText("Next");
        } else {
            nextBtn.setText("Finish Assessment");
        }
    }

    private void showResults() {
        QuizResult result = viewModel.getFinalResult();
        if (currentUser != null) {
            DatabaseService.saveQuizResult(currentUser.getId(), result);
        }
        scoreLabel.setText("Screening Complete");
        riskLabel.setText("Risk Level: " + result.getRiskIndicator());
        if (recommendationLabel != null) {
            recommendationLabel.setText(result.getRecommendation());
            recommendationLabel.setVisible(true);
        }
        resultsPane.setVisible(true);

        Stage stage = (Stage) resultsPane.getScene().getWindow();
        stage.setHeight(900);
    }

    private void restartQuiz() {
        viewModel = new QuizViewModel();
        selectedOption = -1;
        resultsPane.setVisible(false);
        loadCurrentQuestion();
        updateUI();
    }
}