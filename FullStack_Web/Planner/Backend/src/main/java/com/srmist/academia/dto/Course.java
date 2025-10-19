package com.srmist.academia.dto;

public class Course {
    private String sNo;
    private String courseCode;
    private String courseTitle;
    private String credit;
    private String regnType;
    private String category;
    private String courseType;
    private String facultyName;
    private String slot;
    private String roomNo;
    private String academicYear;

    // Getters and Setters
    public String getSNo() { return sNo; }
    public void setSNo(String sNo) { this.sNo = sNo; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    
    public String getCredit() { return credit; }
    public void setCredit(String credit) { this.credit = credit; }
    
    public String getRegnType() { return regnType; }
    public void setRegnType(String regnType) { this.regnType = regnType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }
    
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
}
