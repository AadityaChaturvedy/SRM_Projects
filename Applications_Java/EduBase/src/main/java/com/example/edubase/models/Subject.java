package com.example.edubase.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Subject {
    private long id;
    private long studentId;
    private final String subjectName;
    private final IntegerProperty attendance = new SimpleIntegerProperty();

    // Cycle test and total fields
    private final IntegerProperty cycle1 = new SimpleIntegerProperty();
    private final IntegerProperty cycle2 = new SimpleIntegerProperty();
    private final IntegerProperty cycle3 = new SimpleIntegerProperty();
    private final IntegerProperty cycle4 = new SimpleIntegerProperty();
    private final IntegerProperty total = new SimpleIntegerProperty();

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getSubjectName() { return subjectName; }

    public int getAttendance() { return attendance.get(); }
    public void setAttendance(int value) { attendance.set(value); }
    public IntegerProperty attendanceProperty() { return attendance; }

    public int getCycle1() { return cycle1.get(); }
    public void setCycle1(int value) { cycle1.set(value); }
    public IntegerProperty cycle1Property() { return cycle1; }

    public int getCycle2() { return cycle2.get(); }
    public void setCycle2(int value) { cycle2.set(value); }
    public IntegerProperty cycle2Property() { return cycle2; }

    public int getCycle3() { return cycle3.get(); }
    public void setCycle3(int value) { cycle3.set(value); }
    public IntegerProperty cycle3Property() { return cycle3; }

    public int getCycle4() { return cycle4.get(); }
    public void setCycle4(int value) { cycle4.set(value); }
    public IntegerProperty cycle4Property() { return cycle4; }

    public int getTotal() { return total.get(); }
    public void setTotal(int value) { total.set(value); }
    public IntegerProperty totalProperty() { return total; }
}
