package com.parking.services;

import com.parking.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReservationService {
    private final Map<String, Reservation> reservations = new HashMap<>();
    private final ObservableList<Reservation> activeReservations = FXCollections.observableArrayList();

    public ReservationService() {
    }

    public void createReservation(String cardUID, int slotNumber) {
        String id = UUID.randomUUID().toString();
        ParkingSlot slot = new ParkingSlot(slotNumber);
        Reservation reservation = new Reservation(id, cardUID, slot, LocalDateTime.now(), null);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservations.put(id, reservation);
        activeReservations.add(reservation);
    }

    public void completeReservation(Reservation reservation) {
        reservation.setEndTime(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setCost(calculateParkingFee(reservation));
        activeReservations.remove(reservation);
    }

    public void cancelReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
        activeReservations.remove(reservation);
    }

    public ObservableList<Reservation> getActiveReservations() {
        return activeReservations;
    }

    public Optional<Reservation> findActiveReservationByCardUID(String cardUID) {
        return activeReservations.stream()
                .filter(r -> r.getCardUID().equals(cardUID) && r.getStatus() == ReservationStatus.ACTIVE)
                .findFirst();
    }

    public Optional<Reservation> handleCardScan(String cardUID) {
        Optional<Reservation> existingReservation = findActiveReservationByCardUID(cardUID);
        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            completeReservation(reservation);
            return Optional.of(reservation);
        } else {
            // Find an available slot
            // This is a simplified logic, you might want to implement a more robust slot allocation mechanism
            int availableSlot = new Random().nextInt(10) + 1;
            createReservation(cardUID, availableSlot);
            return Optional.empty();
        }
    }

    private double calculateParkingFee(Reservation reservation) {
        long minutes = ChronoUnit.MINUTES.between(reservation.getStartTime(), reservation.getEndTime());
        double pricePerHour = 50.0;
        return (minutes / 60.0) * pricePerHour;
    }
}