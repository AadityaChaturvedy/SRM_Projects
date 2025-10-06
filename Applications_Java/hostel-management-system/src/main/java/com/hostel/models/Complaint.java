package com.hostel.models;

public class Complaint {

    private int id;
    private int studentId;
    private String complaintText;
    private String date;
    private String status;

    public Complaint(int id, int studentId, String complaintText, String date, String status) {
        this.id = id;
        this.studentId = studentId;
        this.complaintText = complaintText;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getComplaintText() {
        return complaintText;
    }

    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
