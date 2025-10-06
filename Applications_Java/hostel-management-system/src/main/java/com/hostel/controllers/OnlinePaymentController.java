package com.hostel.controllers;

import com.hostel.database.PaymentDAO;
import com.hostel.models.Payment;
import com.hostel.utils.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OnlinePaymentController {

    @FXML
    private TextField amountField;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expDateField;
    @FXML
    private TextField cvcField;
    @FXML
    private Label statusLabel;

    private StripeService stripeService;
    private PaymentDAO paymentDAO;
    private int studentId; // This would be set from the previous screen

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    @FXML
    public void initialize() {
        stripeService = new StripeService();
        paymentDAO = new PaymentDAO();
        // In a real app, the student ID would be passed to this controller
        // For demonstration, let's assume a studentId of 1 if not set
        if (this.studentId == 0) {
            this.studentId = 1;
        }
    }

    @FXML
    private void handlePayment() {
        try {
            int amount = Integer.parseInt(amountField.getText());
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid amount.");
                return;
            }

            String cardNumber = cardNumberField.getText();
            String expDate = expDateField.getText();
            String cvc = cvcField.getText();

            // Basic validation for card details (for simulation purposes)
            if (cardNumber.isEmpty() || expDate.isEmpty() || cvc.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all card details.");
                return;
            }

            String[] expParts = expDate.split("/");
            if (expParts.length != 2) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Expiration date must be in MM/YY format.");
                return;
            }
            String expMonth = expParts[0];
            String expYear = expParts[1];

            // 1. Create a Payment Intent on the server (your Java backend)
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount);

            // 2. Simulate client-side confirmation using the new confirmPayment method
            // IMPORTANT: In a real application, card details should NEVER be sent directly
            // from the client to your backend. Instead, use Stripe.js or mobile SDKs
            // to tokenize card details on the client-side and send only the token to your server.
            boolean paymentConfirmed = stripeService.confirmPayment(
                    paymentIntent.getId(), cardNumber, expMonth, expYear, cvc);

            if (paymentConfirmed) {
                // 3. Update the payment in your local database
                updateLocalPaymentRecord(amount);

                statusLabel.setText("Payment Successful!");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment of $" + amount + " was successful.");
            } else {
                statusLabel.setText("Payment Failed.");
                showAlert(Alert.AlertType.ERROR, "Payment Failed", "The payment could not be confirmed. Please check your card details.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the amount.");
        } catch (StripeException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Payment Error", "An error occurred while processing the payment. " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update payment status in the database.");
        }
    }

    private void updateLocalPaymentRecord(int amount) throws SQLException {
        Payment newPayment = new Payment(0, this.studentId, amount, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), "PAID");
        paymentDAO.createPayment(newPayment);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        // Get the stage from any UI element and close it
        ((javafx.stage.Stage) statusLabel.getScene().getWindow()).close();
    }
}
