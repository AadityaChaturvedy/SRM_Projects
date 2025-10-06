package com.alzheimer.awareness.viewmodel;

import com.alzheimer.awareness.model.Question;
import com.alzheimer.awareness.model.QuizResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuizViewModel {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private List<Integer> responses;

    public QuizViewModel() {
        loadQuestions();
        responses = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) responses.add(-1);
    }

    private void loadQuestions() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/quiz/patient_assessment.json"))) {
            Type listType = new TypeToken<List<Question>>(){}.getType();
            questions = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            questions = new ArrayList<>();
        }
    }

    public Question getCurrentQuestion() {
        if (currentQuestionIndex < questions.size())
            return questions.get(currentQuestionIndex);
        return null;
    }

    public void answerCurrentQuestion(int selectedIndex) {
        responses.set(currentQuestionIndex, selectedIndex);
    }

    public boolean hasNextQuestion() { return currentQuestionIndex < questions.size() - 1; }
    public void nextQuestion() { if (hasNextQuestion()) currentQuestionIndex++; }

    public boolean hasPreviousQuestion() { return currentQuestionIndex > 0; }
    public void previousQuestion() { if (hasPreviousQuestion()) currentQuestionIndex--; }

    public QuizResult getFinalResult() {
        int totalScore = 0;
        int maxScore = questions.size() * 3;
        for (int i = 0; i < responses.size(); i++)
            totalScore += Math.max(responses.get(i), 0);
        double pct = (double) totalScore / maxScore;

        String risk;
        String recommendation;
        if (pct <= 0.20) {
            risk = "Low Risk";
            recommendation = "Your responses suggest minimal concern. Maintain healthy habits and re-screen yearly.";
        } else if (pct <= 0.40) {
            risk = "Mild Concern";
            recommendation = "Some symptoms are present. Discuss with your healthcare provider.";
        } else if (pct <= 0.60) {
            risk = "Moderate Concern";
            recommendation = "Several symptoms are present. Schedule a medical evaluation.";
        } else {
            risk = "High Concern";
            recommendation = "Many symptoms present. Please seek assessment by a healthcare professional soon.";
        }
        return new QuizResult(totalScore, questions.size(), risk, recommendation);
    }

    public boolean isQuizCompleted() {
        for (int answer : responses)
            if (answer == -1) return false;
        return true;
    }

    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public int getNumQuestions() { return questions.size(); }
    public int getResponse(int i) { return responses.get(i); }
    public List<Question> getAllQuestions() { return questions; }
}
