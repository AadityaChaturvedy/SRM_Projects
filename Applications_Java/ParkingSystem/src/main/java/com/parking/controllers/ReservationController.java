package com.parking.controllers;

import com.parking.models.*;
import com.parking.services.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    @FXML private TableView<Reservation> reservationTable;

    private final ReservationService reservationService;
    private final NotificationService notificationService;

    public ReservationController(ReservationService reservationService, NotificationService notificationService) {
        this.reservationService = reservationService;
        this.notificationService = notificationService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeReservationTable();
    }

    private void initializeReservationTable() {
        reservationTable.getColumns().clear();
        TableColumn<Reservation, String> cardCol = new TableColumn<>("Card UID");
        cardCol.setCellValueFactory(cellData -> cellData.getValue().cardUIDProperty());

        TableColumn<Reservation, LocalDateTime> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());

        TableColumn<Reservation, LocalDateTime> endCol = new TableColumn<>("End Time");
        endCol.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());

        reservationTable.getColumns().addAll(cardCol, startCol, endCol);
        reservationTable.setItems(reservationService.getActiveReservations());
        reservationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}