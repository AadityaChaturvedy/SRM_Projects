package com.parking.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Payment {
    private final StringProperty id;
    private final ObjectProperty<Reservation> reservation;
    private final DoubleProperty amount;
    private final ObjectProperty<PaymentStatus> status;
    private final ObjectProperty<PaymentMethod> method;
    private final ObjectProperty<LocalDateTime> timestamp;
    private final StringProperty transactionId;

    public Payment(String id, Reservation reservation, double amount, 
                  PaymentMethod method) {
        this.id = new SimpleStringProperty(id);
        this.reservation = new SimpleObjectProperty<>(reservation);
        this.amount = new SimpleDoubleProperty(amount);
        this.status = new SimpleObjectProperty<>(PaymentStatus.PENDING);
        this.method = new SimpleObjectProperty<>(method);
        this.timestamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.transactionId = new SimpleStringProperty("");
    }

    // Getters
    public String getId() { return id.get(); }
    public Reservation getReservation() { return reservation.get(); }
    public double getAmount() { return amount.get(); }
    public PaymentStatus getStatus() { return status.get(); }
    public PaymentMethod getMethod() { return method.get(); }
    public LocalDateTime getTimestamp() { return timestamp.get(); }
    public String getTransactionId() { return transactionId.get(); }

    // Property getters for JavaFX bindings
    public StringProperty idProperty() { return id; }
    public ObjectProperty<Reservation> reservationProperty() { return reservation; }
    public DoubleProperty amountProperty() { return amount; }
    public ObjectProperty<PaymentStatus> statusProperty() { return status; }
    public ObjectProperty<PaymentMethod> methodProperty() { return method; }
    public ObjectProperty<LocalDateTime> timestampProperty() { return timestamp; }
    public StringProperty transactionIdProperty() { return transactionId; }

    // Setters
    public void setStatus(PaymentStatus status) { this.status.set(status); }
    public void setTransactionId(String transactionId) { this.transactionId.set(transactionId); }
}