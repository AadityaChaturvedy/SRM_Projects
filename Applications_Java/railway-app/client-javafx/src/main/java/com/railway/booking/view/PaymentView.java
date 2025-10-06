package com.railway.booking.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import java.util.Optional;

public class PaymentView {

    public static Optional<String> show(String paymentMethod) {
        Dialog<String> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Enter Payment Details");
        dialog.setHeaderText("Enter your " + paymentMethod + " details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        switch (paymentMethod) {
            case "UPI":
                TextField upiIdField = new TextField();
                upiIdField.setPromptText("your-upi-id@okhdfcbank");
                grid.add(new Label("UPI ID:"), 0, 0);
                grid.add(upiIdField, 1, 0);
                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return upiIdField.getText();
                    }
                    return null;
                });
                break;
            case "Card":
                TextField cardNumberField = new TextField();
                cardNumberField.setPromptText("Card Number");
                TextField expiryField = new TextField();
                expiryField.setPromptText("MM/YY");
                PasswordField cvvField = new PasswordField();
                cvvField.setPromptText("CVV");
                grid.add(new Label("Card Number:"), 0, 0);
                grid.add(cardNumberField, 1, 0);
                grid.add(new Label("Expiry Date:"), 0, 1);
                grid.add(expiryField, 1, 1);
                grid.add(new Label("CVV:"), 0, 2);
                grid.add(cvvField, 1, 2);
                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return "Card: " + cardNumberField.getText();
                    }
                    return null;
                });
                break;
            case "Net Banking":
                ComboBox<String> bankComboBox = new ComboBox<>();
                bankComboBox.getItems().addAll("HDFC Bank", "ICICI Bank", "State Bank of India", "Axis Bank");
                bankComboBox.setPromptText("Select Bank");
                grid.add(new Label("Bank:"), 0, 0);
                grid.add(bankComboBox, 1, 0);
                dialog.getDialogPane().setContent(grid);
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return "Bank: " + bankComboBox.getValue();
                    }
                    return null;
                });
                break;
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        return dialog.showAndWait();
    }
}
