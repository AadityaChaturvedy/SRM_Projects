package com.parking.models;

public enum ReservationStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}