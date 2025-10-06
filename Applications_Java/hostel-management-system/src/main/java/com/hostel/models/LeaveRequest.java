package com.hostel.models;

public class LeaveRequest {
    private int id;
    private int studentId;
    private String studentName; // For display purposes
    private String startDate;
    private String endDate;
    private String reason;
    private String status;
    private String requestDate;

    public LeaveRequest() {}

    public LeaveRequest(int id, int studentId, String startDate, String endDate, String reason, String status, String requestDate) {
        this.id = id;
        this.studentId = studentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.requestDate = requestDate;
    }

    // Getters
    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getRequestDate() { return requestDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setReason(String reason) { this.reason = reason; }
    public void setStatus(String status) { this.status = status; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
}
