package com.alzheimer.awareness.model;

public class Question {
    private String question;
    private String[] options;
    private String category;

    public Question() {}

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
