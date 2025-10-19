package com.srmist.academia.dto;

import java.util.List;

public class StudentDetails {
    private String regNumber;
    private String name;
    private int batch;
    private String mobile;
    private String department;
    private int semester;
    private List<Course> courses;

    // Getters and Setters
    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getBatch() { return batch; }
    public void setBatch(int batch) { this.batch = batch; }
    
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
