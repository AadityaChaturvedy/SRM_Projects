package com.hostel.utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeService {

    static {
        // Replace with your actual secret key
        Stripe.apiKey = "sk_test_51...REPLACE_WITH_YOUR_KEY"; // IMPORTANT: Use a placeholder
    }

    public PaymentIntent createPaymentIntent(int amount) throws StripeException {
        long amountInCents = amount * 100L;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd") // or your preferred currency
                .addPaymentMethodType("card")
                .build();

        return PaymentIntent.create(params);
    }

    // This method simulates the client-side confirmation of a PaymentIntent.
    // In a real application, card details would be tokenized on the client-side
    // using Stripe.js or mobile SDKs, and only the token would be sent to the backend.
    // For this JavaFX example, we're simulating the process.
    public boolean confirmPayment(String paymentIntentId, String cardNumber, String expMonth, String expYear, String cvc) {
        // In a real scenario, you would use Stripe's client-side SDKs to confirm the payment.
        // This is a simplified simulation.
        System.out.println("Simulating payment confirmation for PaymentIntent: " + paymentIntentId);
        System.out.println("Card Number: " + cardNumber + ", Exp: " + expMonth + "/" + expYear + ", CVC: " + cvc);

        // Simulate success for valid-looking card details, failure otherwise
        if (cardNumber != null && cardNumber.startsWith("4") && cardNumber.length() == 16 &&
            expMonth != null && expYear != null && cvc != null && cvc.length() == 3) {
            return true; // Simulated success
        } else {
            return false; // Simulated failure
        }
    }
}
