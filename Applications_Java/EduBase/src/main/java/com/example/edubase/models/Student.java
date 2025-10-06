package com.example.edubase.models;

import javafx.beans.property.*;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty age = new SimpleIntegerProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty city = new SimpleStringProperty();
    private final StringProperty state = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();

    // Subjects mapping: key = subject name, value = Subject object
    private final Map<String, Subject> subjects = new HashMap<>();

    public Student() {}

    // Getters and Setters for standard properties
    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public int getAge() { return age.get(); }
    public void setAge(int age) { this.age.set(age); }
    public IntegerProperty ageProperty() { return age; }

    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }
    public StringProperty addressProperty() { return address; }

    public String getCity() { return city.get(); }
    public void setCity(String city) { this.city.set(city); }
    public StringProperty cityProperty() { return city; }

    public String getState() { return state.get(); }
    public void setState(String state) { this.state.set(state); }
    public StringProperty stateProperty() { return state; }

    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    // Subjects
    public Map<String, Subject> getSubjects() { return subjects; }
}