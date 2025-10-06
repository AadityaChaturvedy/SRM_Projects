package com.hostel.models;

public class Visitor {
    private int id;
    private int studentId;
    private String studentName; // For display
    private String visitorName;
    private String relation;
    private String visitDate;
    private String status;

    public Visitor() {}

    // Getters
    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getVisitorName() { return visitorName; }
    public String getRelation() { return relation; }
    public String getVisitDate() { return visitDate; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public void setRelation(String relation) { this.relation = relation; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
    public void setStatus(String status) { this.status = status; }
}
