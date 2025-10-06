package com.parking.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Reservation {
    private final StringProperty id;
    private final StringProperty cardUID;
    private final ObjectProperty<ParkingSlot> slot;
    private final ObjectProperty<LocalDateTime> startTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final ObjectProperty<ReservationStatus> status;
    private final DoubleProperty cost;

    public Reservation(String id, String cardUID, ParkingSlot slot, 
                      LocalDateTime startTime, LocalDateTime endTime) {
        this.id = new SimpleStringProperty(id);
        this.cardUID = new SimpleStringProperty(cardUID);
        this.slot = new SimpleObjectProperty<>(slot);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
        this.status = new SimpleObjectProperty<>(ReservationStatus.PENDING);
        this.cost = new SimpleDoubleProperty(0.0);
    }

    // Getters
    public String getId() { return id.get(); }
    public String getCardUID() { return cardUID.get(); }
    public ParkingSlot getSlot() { return slot.get(); }
    public LocalDateTime getStartTime() { return startTime.get(); }
    public LocalDateTime getEndTime() { return endTime.get(); }
    public ReservationStatus getStatus() { return status.get(); }
    public double getCost() { return cost.get(); }

    // Property getters for JavaFX bindings
    public StringProperty idProperty() { return id; }
    public StringProperty cardUIDProperty() { return cardUID; }
    public ObjectProperty<ParkingSlot> slotProperty() { return slot; }
    public ObjectProperty<LocalDateTime> startTimeProperty() { return startTime; }
    public ObjectProperty<LocalDateTime> endTimeProperty() { return endTime; }
    public ObjectProperty<ReservationStatus> statusProperty() { return status; }
    public DoubleProperty costProperty() { return cost; }

    // Setters
    public void setEndTime(LocalDateTime endTime) { this.endTime.set(endTime); }
    public void setStatus(ReservationStatus status) { this.status.set(status); }
    public void setCost(double cost) { this.cost.set(cost); }
}