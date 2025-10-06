package com.parking.controllers;

import com.parking.models.PaymentMethod;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PaymentDialogController {
    @FXML private Label amountLabel;
    @FXML private ComboBox<PaymentMethod> paymentMethodComboBox;
    @FXML private Button payButton;

    private Stage dialogStage;
    private boolean paid = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAmount(double amount) {
        amountLabel.setText(String.format("₹%.2f", amount));
    }

    public boolean isPaid() {
        return paid;
    }

    @FXML
    private void initialize() {
        paymentMethodComboBox.getItems().setAll(PaymentMethod.values());
        payButton.setOnAction(event -> {
            paid = true;
            dialogStage.close();
        });
    }
}
