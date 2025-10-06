package com.hostel.utils;

import com.hostel.database.NotificationDAO;
import com.hostel.database.PaymentDAO;
import com.hostel.models.Notification;
import com.hostel.models.Payment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeeReminderService {

    private final PaymentDAO paymentDAO;
    private final NotificationDAO notificationDAO;

    public FeeReminderService() {
        this.paymentDAO = new PaymentDAO();
        this.notificationDAO = new NotificationDAO();
    }

    public void sendReminders() {
        try {
            // Reminder for payments due in the next 7 days
            sendUpcomingPaymentReminders();

            // Reminder for overdue payments
            sendOverduePaymentReminders();

        } catch (SQLException e) {
            e.printStackTrace();
            // In a real application, you would log this error more robustly
        }
    }

    private void sendUpcomingPaymentReminders() throws SQLException {
        List<Payment> upcomingPayments = paymentDAO.getPaymentsDueSoon(7); // 7 days window
        for (Payment payment : upcomingPayments) {
            if (payment.getStatus().equalsIgnoreCase("PENDING")) {
                createReminderNotification(payment, "Fee Due Soon");
            }
        }
    }

    private void sendOverduePaymentReminders() throws SQLException {
        List<Payment> overduePayments = paymentDAO.getOverduePayments();
        for (Payment payment : overduePayments) {
            if (payment.getStatus().equalsIgnoreCase("PENDING")) {
                createReminderNotification(payment, "Fee Overdue");
            }
        }
    }

    private void createReminderNotification(Payment payment, String title) throws SQLException {
        Notification notification = new Notification(0, payment.getStudentId(), String.format("Reminder: A payment of %.2f is due on %s.", payment.getAmount(), payment.getDate()), LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), false);
        notificationDAO.createNotification(notification);
    }
}
