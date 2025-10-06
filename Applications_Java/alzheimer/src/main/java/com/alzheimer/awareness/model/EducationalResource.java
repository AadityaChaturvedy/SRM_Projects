package com.alzheimer.awareness.model;

public class EducationalResource {
    private String title;
    private String content;

    public EducationalResource(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
