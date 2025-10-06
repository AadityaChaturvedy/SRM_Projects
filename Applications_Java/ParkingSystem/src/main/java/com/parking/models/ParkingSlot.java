package com.parking.models;

import javafx.beans.property.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import java.time.LocalDateTime;

public class ParkingSlot {
    private final IntegerProperty number;
    private final BooleanProperty occupied;
    private final StringProperty currentVehicle;
    private final ObjectProperty<LocalDateTime> occupiedSince;

    public ParkingSlot(int number) {
        this.number = new SimpleIntegerProperty(number);
        this.occupied = new SimpleBooleanProperty(false);
        this.currentVehicle = new SimpleStringProperty("");
        this.occupiedSince = new SimpleObjectProperty<>(null);
    }

    // Getters
    public int getNumber() { return number.get(); }
    public boolean isOccupied() { return occupied.get(); }
    public String getCurrentVehicle() { return currentVehicle.get(); }
    public LocalDateTime getOccupiedSince() { return occupiedSince.get(); }

    // Property getters for JavaFX bindings
    public IntegerProperty numberProperty() { return number; }
    public BooleanProperty occupiedProperty() { return occupied; }
    public StringProperty currentVehicleProperty() { return currentVehicle; }
    public ObjectProperty<LocalDateTime> occupiedSinceProperty() { return occupiedSince; }

    // Setters
    public void setOccupied(boolean occupied) { this.occupied.set(occupied); }
    public void setCurrentVehicle(String vehicle) { this.currentVehicle.set(vehicle); }
    public void setOccupiedSince(LocalDateTime time) { this.occupiedSince.set(time); }
}