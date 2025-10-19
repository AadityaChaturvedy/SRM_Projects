package com.srmist.academia.dto;

public class CalendarEvent {
    private String date; // yyyy-MM-dd
    private String day;
    private String event;
    private int doCount;

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    
    public int getDoCount() { return doCount; }
    public void setDoCount(int doCount) { this.doCount = doCount; }
}
