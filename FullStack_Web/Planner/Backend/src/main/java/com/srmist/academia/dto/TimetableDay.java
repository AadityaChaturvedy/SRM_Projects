package com.srmist.academia.dto;

import java.util.List;

public class TimetableDay {
    private int dayOrder;
    private List<TimetableEntry> schedule;

    // Getters and Setters
    public int getDayOrder() { return dayOrder; }
    public void setDayOrder(int dayOrder) { this.dayOrder = dayOrder; }
    
    public List<TimetableEntry> getSchedule() { return schedule; }
    public void setSchedule(List<TimetableEntry> schedule) { this.schedule = schedule; }
}
