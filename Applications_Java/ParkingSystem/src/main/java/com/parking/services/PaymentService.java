package com.parking.services;

import com.parking.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentService {
    private final ObservableList<Payment> payments = FXCollections.observableArrayList();
    private final ReservationService reservationService;

    public PaymentService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public Payment createPayment(Reservation reservation, double amount, PaymentMethod method) {
        String id = UUID.randomUUID().toString();
        Payment payment = new Payment(id, reservation, amount, method);
        payments.add(payment);
        return payment;
    }

    public void processPayment(String paymentId, String transactionId) {
        payments.stream()
            .filter(p -> p.getId().equals(paymentId))
            .findFirst()
            .ifPresent(payment -> {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(transactionId);
                // Complete the associated reservation
                reservationService.completeReservation(payment.getReservation());
            });
    }

    public ObservableList<Payment> getPayments() {
        return payments;
    }
}