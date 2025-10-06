package com.parking.controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.parking.models.AccessLogEntry;
import com.parking.models.Reservation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.parking.services.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import com.google.gson.JsonSyntaxException;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController implements Initializable {
    @FXML private ComboBox<String> portSelector;
    @FXML private Button connectButton;
    @FXML private Button overviewButton;
    @FXML private Button reservationsButton;
    @FXML private Label connectionStatus;
    @FXML private Label systemStatus;
    @FXML private Label totalSlots;
    @FXML private Label availableSlots;
    @FXML private Label occupiedSlots;
    @FXML private FlowPane slotsContainer;
    @FXML private TextArea consoleOutput;
    @FXML private TableView<AccessLogEntry> accessLogTable;
    @FXML private TableColumn<AccessLogEntry, String> timeColumn;
    @FXML private TableColumn<AccessLogEntry, String> cardColumn;
    @FXML private TableColumn<AccessLogEntry, String> statusColumn;
    @FXML private Button clearLogButton;
    @FXML private ComboBox<String> logFilterComboBox;
    @FXML private StackPane contentArea;
    private SerialPort serialPort;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private Thread serialThread;
    private final ObservableList<AccessLogEntry> accessLog = FXCollections.observableArrayList();
    
    private final ReservationService reservationService = new ReservationService();
    private final NotificationService notificationService = new NotificationService();
    
    private Parent reservationsView;
    private Parent overviewContent;

    private void initializeNavigation() {
        overviewButton.setOnAction(e -> showOverview());
        reservationsButton.setOnAction(e -> showReservations());
    }

    private void setActiveButton(Button activeButton) {
        overviewButton.getStyleClass().remove("nav-button-active");
        reservationsButton.getStyleClass().remove("nav-button-active");
        activeButton.getStyleClass().add("nav-button-active");
    }

    @FXML
    public void showOverview() {
        if (overviewContent != null) {
            contentArea.getChildren().setAll(overviewContent);
        }
        setActiveButton(overviewButton);
    }

    @FXML
    public void showReservations() {
        if (reservationsView == null) {
            initializeReservationsView();
        }
        contentArea.getChildren().setAll(reservationsView);
        setActiveButton(reservationsButton);
    }

    private void initializeReservationsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReservationView.fxml"));
            ReservationController controller = new ReservationController(reservationService, notificationService);
            loader.setController(controller);
            reservationsView = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reservations view");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!contentArea.getChildren().isEmpty()) {
            overviewContent = (Parent) contentArea.getChildren().get(0);
        }
        initializePortSelector();
        initializeConnectButton();
        initializeNavigation();
        initializeAccessLog();
        updateStatistics(0, 0);
        
        // Initialize with 2 parking slots
        initializeParkingSlots(2);
    }

    private void initializeAccessLog() {
        // Initialize table columns
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().formattedTimeProperty());
        cardColumn.setCellValueFactory(cellData -> cellData.getValue().cardUIDProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Style status column
        statusColumn.setCellFactory(column -> new TableCell<AccessLogEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().removeAll("status-granted", "status-denied");
                    getStyleClass().add(item.toLowerCase().equals("granted") ? "status-granted" : "status-denied");
                }
            }
        });

        // Set table items
        accessLogTable.setItems(accessLog);
        accessLogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Initialize filter combo box
        logFilterComboBox.getItems().addAll("All", "Granted", "Denied");
        logFilterComboBox.setValue("All");
        logFilterComboBox.setOnAction(e -> filterAccessLog());

        // Initialize clear button
        clearLogButton.setOnAction(e -> accessLog.clear());
    }

    private void filterAccessLog() {
        String filter = logFilterComboBox.getValue();
        if (filter == null || filter.equals("All")) {
            accessLogTable.setItems(accessLog);
        } else {
            FilteredList<AccessLogEntry> filteredData = new FilteredList<>(accessLog);
            filteredData.setPredicate(entry -> entry.getStatus().equalsIgnoreCase(filter));
            accessLogTable.setItems(filteredData);
        }
    }

    private void initializePortSelector() {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            portSelector.getItems().add(port.getSystemPortName());
        }
        if (ports.length == 0) {
            portSelector.setPromptText("No ports available");
        }
    }

    private void initializeConnectButton() {
        connectButton.setOnAction(e -> {
            if (!isConnected.get()) {
                connect();
            } else {
                disconnect();
            }
        });
    }

    private void connect() {
        String selectedPort = portSelector.getValue();
        if (selectedPort == null) {
            showAlert("Error", "Please select a port");
            return;
        }

        serialPort = SerialPort.getCommPort(selectedPort);
        serialPort.setBaudRate(115200);
        
        if (serialPort.openPort()) {
            isConnected.set(true);
            updateConnectionStatus(true);
            startSerialListener();
        } else {
            showAlert("Error", "Failed to open port");
        }
    }

    private void disconnect() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
        if (serialThread != null) {
            serialThread.interrupt();
        }
        isConnected.set(false);
        updateConnectionStatus(false);
    }

    private void startSerialListener() {
        serialThread = new Thread(() -> {
            StringBuilder messageBuilder = new StringBuilder();
            while (!Thread.interrupted() && serialPort.isOpen()) {
                if (serialPort.bytesAvailable() > 0) {
                    byte[] readBuffer = new byte[serialPort.bytesAvailable()];
                    serialPort.readBytes(readBuffer, readBuffer.length);
                    String data = new String(readBuffer);
                    
                    messageBuilder.append(data);
                    
                    if (data.contains("\n")) {
                        String completeMessage = messageBuilder.toString().trim();
                        handleSerialMessage(completeMessage);
                        messageBuilder = new StringBuilder();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        serialThread.start();
    }

    private final Gson gson = new Gson();

    private void handleSerialMessage(String message) {
        Platform.runLater(() -> {
            appendToConsole(message);
            try {
                String cleanedJson = message.trim();
                if (!cleanedJson.startsWith("{")) {
                    return; // Skip invalid JSON
                }
                JsonReader reader = new JsonReader(new StringReader(cleanedJson));
                reader.setLenient(true);
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                String event = json.get("event").getAsString();
                
                switch (event) {
                    case "slot_update":
                        int slotId = json.get("slot_id").getAsInt();
                        boolean isOccupied = json.get("status").getAsString().equals("occupied");
                        updateSlotStatus(slotId - 1, isOccupied);
                        break;
                    case "card_scan":
                        String uid = json.get("uid").getAsString();
                        String status = json.get("status").getAsString();
                        handleCardScan(uid, status);
                        break;
                    case "system_ready":
                        systemStatus.setText("System Online");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
        });
    }

    private void updateConnectionStatus(boolean connected) {
        Platform.runLater(() -> {
            connectionStatus.getStyleClass().clear();
            connectionStatus.getStyleClass().add(connected ? "status-connected" : "status-disconnected");
            connectButton.setText(connected ? "Disconnect" : "Connect");
            connectButton.getStyleClass().clear();
            connectButton.getStyleClass().add(connected ? "btn-danger-outline" : "btn-connect");
            portSelector.setDisable(connected);
        });
    }

    private void initializeParkingSlots(int numSlots) {
        slotsContainer.getChildren().clear();
        for (int i = 0; i < numSlots; i++) {
            VBox slotCard = createSlotCard(i + 1);
            slotsContainer.getChildren().add(slotCard);
        }
        updateStatistics(numSlots, 0);
    }

    private VBox createSlotCard(int slotNumber) {
        VBox slotCard = new VBox();
        slotCard.getStyleClass().add("slot-card");
        
        Label numberLabel = new Label("Parking Slot " + slotNumber);
        numberLabel.getStyleClass().add("slot-number");
        
        Label statusLabel = new Label("Available");
        statusLabel.getStyleClass().addAll("slot-status", "status-available");
        statusLabel.setId("status-" + slotNumber);
        
        slotCard.getChildren().addAll(numberLabel, statusLabel);
        return slotCard;
    }

    private void updateSlotStatus(int slotIndex, boolean isOccupied) {
        Label statusLabel = (Label) slotsContainer.lookup("#status-" + (slotIndex + 1));
        if (statusLabel != null) {
            statusLabel.setText(isOccupied ? "Occupied" : "Available");
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().addAll("slot-status", 
                isOccupied ? "status-occupied" : "status-available");
        }
        updateStatistics();
    }

    private void updateStatistics() {
        int occupied = (int) slotsContainer.getChildren().stream()
            .filter(node -> ((VBox) node).lookup(".status-occupied") != null)
            .count();
        int total = slotsContainer.getChildren().size();
        updateStatistics(total, occupied);
    }

    private void updateStatistics(int total, int occupied) {
        totalSlots.setText(String.valueOf(total));
        occupiedSlots.setText(String.valueOf(occupied));
        availableSlots.setText(String.valueOf(total - occupied));
    }

    private void handleCardScan(String uid, String status) {
        String message = String.format("Card Scan: %s - Access %s", uid, status);
        appendToConsole(message);
        
        if ("granted".equalsIgnoreCase(status)) {
            Optional<Reservation> completedReservation = reservationService.handleCardScan(uid);
            completedReservation.ifPresent(this::showPaymentDialog);
        }
        
        // Add to access log
        Platform.runLater(() -> {
            accessLog.add(0, new AccessLogEntry(uid, status));
            filterAccessLog(); // Reapply current filter
        });
    }

    private void showPaymentDialog(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentDialog.fxml"));
            Parent root = loader.load();

            PaymentDialogController controller = loader.getController();
            controller.setAmount(reservation.getCost());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Payment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contentArea.getScene().getWindow());
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isPaid()) {
                // Handle successful payment
                showAlert("Payment Successful", "Payment for reservation has been completed.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load payment dialog");
        }
    }

    private void appendToConsole(String message) {
        Platform.runLater(() -> {
            consoleOutput.appendText(message + "\n");
            consoleOutput.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void shutdown() {
        disconnect();
    }
}