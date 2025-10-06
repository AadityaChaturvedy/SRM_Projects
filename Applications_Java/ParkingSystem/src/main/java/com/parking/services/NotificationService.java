package com.parking.services;

import com.parking.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.UUID;

public class NotificationService {
    private final ObservableList<Notification> notifications = FXCollections.observableArrayList();
    private final ObservableList<Notification> unreadNotifications = FXCollections.observableArrayList();

    public void sendNotification(String title, String message, NotificationType type) {
        String id = UUID.randomUUID().toString();
        Notification notification = new Notification(id, title, message, type);
        notifications.add(0, notification); // Add to beginning of list
        unreadNotifications.add(notification);
    }

    public void markAsRead(String notificationId) {
        notifications.stream()
            .filter(n -> n.getId().equals(notificationId))
            .findFirst()
            .ifPresent(notification -> {
                notification.setRead(true);
                unreadNotifications.remove(notification);
            });
    }

    public void markAllAsRead() {
        unreadNotifications.forEach(notification -> notification.setRead(true));
        unreadNotifications.clear();
    }

    public ObservableList<Notification> getNotifications() {
        return notifications;
    }

    public ObservableList<Notification> getUnreadNotifications() {
        return unreadNotifications;
    }

    public void notifyLowSpace(int availableSlots, int totalSlots) {
        if (availableSlots < totalSlots * 0.2) { // Less than 20% slots available
            sendNotification(
                "Low Parking Space",
                String.format("Only %d out of %d slots available!", availableSlots, totalSlots),
                NotificationType.WARNING
            );
        }
    }

    public void notifyPaymentReceived(String vehicleNumber, double amount) {
        sendNotification(
            "Payment Received",
            String.format("Payment of ₹%.2f received for vehicle %s", amount, vehicleNumber),
            NotificationType.SUCCESS
        );
    }

    public void notifyUnauthorizedAccess(String cardUID) {
        sendNotification(
            "Unauthorized Access Attempt",
            String.format("Unauthorized access attempt with card %s", cardUID),
            NotificationType.ERROR
        );
    }
}