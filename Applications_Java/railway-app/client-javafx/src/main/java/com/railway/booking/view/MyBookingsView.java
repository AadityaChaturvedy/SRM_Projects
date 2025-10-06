package com.railway.booking.view;

import com.google.gson.JsonObject;
import com.railway.booking.service.ApiClient;
import com.railway.booking.util.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class MyBookingsView {

    private final ApiClient apiClient;
    private final int userId;

    public MyBookingsView(ApiClient apiClient, int userId) {
        this.apiClient = apiClient;
        this.userId = userId;
    }

    public ScrollPane createBookingsPane() {
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        Label title = new Label("🎫 My Train Bookings");
        title.getStyleClass().add("title-label");
        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER);
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.getStyleClass().add("secondary-button");
        Button exportBtn = new Button("📊 Export to CSV");
        exportBtn.getStyleClass().add("secondary-button");
        actionBox.getChildren().addAll(refreshBtn, exportBtn);

        TableView<Map<String, Object>> histTable = new TableView<>();
        histTable.getStyleClass().add("table-view");
        TableColumn<Map<String, Object>, String> hSrc = new TableColumn<>("🚉 From");
        hSrc.setPrefWidth(170);
        TableColumn<Map<String, Object>, String> hDest = new TableColumn<>("🏁 To");
        hDest.setPrefWidth(170);
        TableColumn<Map<String, Object>, String> hDate = new TableColumn<>("📅 Travel Date");
        hDate.setPrefWidth(130);
        TableColumn<Map<String, Object>, String> hStatus = new TableColumn<>("📋 Status");
        hStatus.setPrefWidth(125);
        TableColumn<Map<String, Object>, Void> hCancel = new TableColumn<>("🗑️ Action");
        hCancel.setPrefWidth(120);
        histTable.getColumns().addAll(hSrc, hDest, hDate, hStatus, hCancel);
        histTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        histTable.setFixedCellSize(44);
        histTable.setStyle("-fx-font-size: 17px;");
        hSrc.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("source")));
        hDest.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("destination")));
        hDate.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("date")));
        hStatus.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("status")));
        hStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("CANCELLED".equals(status)) {
                        setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #B71C1C;");
                    } else if ("CONFIRMED".equals(status)) {
                        setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #256029;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        hCancel.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("🗑️ Cancel");

            {
                btn.getStyleClass().add("secondary-button");
                btn.setOnAction(e -> {
                    Map<String, Object> booking = getTableView().getItems().get(getIndex());
                    String status = (String) booking.get("status");
                    if (!"CONFIRMED".equals(status)) {
                        UIUtils.showErrorAlert("❌ Only confirmed bookings can be cancelled.");
                        return;
                    }
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Cancel Booking");
                    confirm.setHeaderText("Are you sure you want to cancel this booking?");
                    confirm.setContentText(booking.get("source") + " → " + booking.get("destination") + " on " + booking.get("date"));
                    if (confirm.showAndWait().get() == ButtonType.OK) {
                        Map<String, Object> req = Map.of(
                                "booking_id", ((Double) booking.get("id")).intValue(),
                                "train_id", ((Double) booking.get("train_id")).intValue()
                        );
                        try {
                            JsonObject json = apiClient.post("/cancel", req);
                            if (json.get("success").getAsBoolean()) {
                                UIUtils.showInfoAlert("✅ Booking cancelled successfully!");
                                refreshBtn.fire();
                            } else {
                                UIUtils.showErrorAlert("❌ Cancellation failed: " + json.get("msg").getAsString());
                            }
                        } catch (Exception ex) {
                            UIUtils.showErrorAlert("❌ Cancellation error: " + ex.getMessage());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Map<String, Object> booking = getTableView().getItems().get(getIndex());
                    String status = (String) booking.get("status");
                    btn.setDisable(!"CONFIRMED".equals(status));
                    btn.setText("CONFIRMED".equals(status) ? "🗑️ Cancel" : "❌ " + status);
                    setGraphic(btn);
                }
            }
        });

        refreshBtn.setOnAction(e -> loadBookings(histTable));
        exportBtn.setOnAction(e -> {
            try (FileWriter csv = new FileWriter("booking_history.csv")) {
                String resp = apiClient.get("/export/" + userId);
                csv.write(resp);
                UIUtils.showInfoAlert("✅ Booking history exported to booking_history.csv");
            } catch (Exception ex) {
                UIUtils.showErrorAlert("❌ Export failed: " + ex.getMessage());
            }
        });
        loadBookings(histTable);

        main.getChildren().addAll(title, actionBox, histTable);
        ScrollPane scrollPane = new ScrollPane(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private void loadBookings(TableView<Map<String, Object>> table) {
        try {
            String resp = apiClient.get("/history/" + userId);
            Map[] arr = apiClient.fromJson(resp, Map[].class);
            table.getItems().setAll(arr);
            if (arr.length == 0) UIUtils.showInfoAlert("📭 No bookings found. Start by searching and booking trains!");
        } catch (Exception ex) {
            UIUtils.showErrorAlert("❌ Failed to load bookings: " + ex.getMessage());
        }
    }
}
