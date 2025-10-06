package com.railway.booking;

import com.railway.booking.view.DashboardView;
import com.railway.booking.view.LoginView;
import com.railway.booking.view.RegisterView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class App extends Application {
    private int userId = -1;
    private String currentUsername = "";
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Railway Booking System");
        showLogin();
    }

    private void showLogin() {
        LoginView loginView = new LoginView();
        loginView.show(stage, (userInfo) -> {
            String[] parts = userInfo.split(":");
            this.userId = Integer.parseInt(parts[0]);
            this.currentUsername = parts[1];
            showDashboard();
        }, this::showRegister);
    }

    private void showRegister() {
        RegisterView registerView = new RegisterView();
        registerView.show(stage, this::showLogin);
    }

    private void showDashboard() {
        DashboardView dashboardView = new DashboardView();
        dashboardView.show(stage, userId, currentUsername, () -> {
            this.userId = -1;
            this.currentUsername = "";
            showLogin();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void showPaymentSelection(java.util.function.Consumer<String> onPayment) {
        Platform.runLater(() -> {
            ChoiceDialog<String> dlg = new ChoiceDialog<>("UPI", java.util.List.of("UPI","Card","Net Banking"));
            dlg.setTitle("Payment Method");
            dlg.setHeaderText("Choose how you want to pay:");
            dlg.setContentText("Method:");
            dlg.showAndWait().ifPresent(onPayment);
        });
    }

}