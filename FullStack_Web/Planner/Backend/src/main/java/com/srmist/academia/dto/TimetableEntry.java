package com.srmist.academia.dto;

public class TimetableEntry {
    private String slot;
    private String startTime;
    private String endTime;
    // minimal course info for the slot
    private String courseCode;
    private String courseType;
    private String title;
    private String faculty;
    private String room;

    // Getters and Setters
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
    
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}
