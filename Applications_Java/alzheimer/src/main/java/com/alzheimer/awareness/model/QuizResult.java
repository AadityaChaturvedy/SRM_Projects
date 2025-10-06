package com.alzheimer.awareness.model;

import java.time.LocalDateTime;

public class QuizResult {
    private int score;
    private int totalQuestions;
    private String riskIndicator;
    private String recommendation;
    private LocalDateTime completedAt;

    public QuizResult(int score, int totalQuestions, String riskIndicator, String recommendation) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.riskIndicator = riskIndicator;
        this.recommendation = recommendation;
        this.completedAt = LocalDateTime.now();
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public String getRiskIndicator() {
        return riskIndicator;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
