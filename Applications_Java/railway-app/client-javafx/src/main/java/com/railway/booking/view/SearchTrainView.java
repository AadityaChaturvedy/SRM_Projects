package com.railway.booking.view;

import com.google.gson.JsonObject;
import com.railway.booking.service.ApiClient;
import com.railway.booking.service.PdfService;
import com.railway.booking.util.UIUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SearchTrainView {

    private final ApiClient apiClient;
    private final int userId;

    private final ObservableList<String> cities = FXCollections.observableArrayList(
            "Chennai", "Mumbai", "Delhi", "Bangalore", "Kolkata", "Pune", "Hyderabad",
            "Ahmedabad", "Jaipur", "Surat", "Lucknow", "Kanpur", "Nagpur", "Visakhapatnam",
            "Indore", "Thane", "Bhopal", "Pimpri-Chinchwad", "Patna", "Vadodara", "Ghaziabad",
            "Ludhiana", "Coimbatore", "Agra", "Madurai", "Nashik", "Rajkot", "Kochi"
    );

    public SearchTrainView(ApiClient apiClient, int userId) {
        this.apiClient = apiClient;
        this.userId = userId;
    }

    public VBox createSearchPane() {
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setAlignment(Pos.TOP_CENTER);

        VBox searchCard = new VBox(20);
        searchCard.getStyleClass().add("search-card");
    searchCard.setAlignment(Pos.CENTER);
    searchCard.setMaxWidth(900);
    searchCard.setPrefWidth(900);

        Label searchTitle = new Label("🚂 Find Your Perfect Journey");
        searchTitle.getStyleClass().add("title-label");

        GridPane searchGrid = new GridPane();
        searchGrid.setHgap(20);
        searchGrid.setVgap(15);
        searchGrid.setAlignment(Pos.CENTER);

        Label fromLabel = new Label("📍 From:");
        fromLabel.getStyleClass().add("label");
        ComboBox<String> fromCombo = new ComboBox<>(cities);
        fromCombo.setPromptText("Select departure city");
    fromCombo.setPrefWidth(220);
        fromCombo.setEditable(true);

        Label toLabel = new Label("🎯 To:");
        toLabel.getStyleClass().add("label");
        ComboBox<String> toCombo = new ComboBox<>(cities);
        toCombo.setPromptText("Select destination city");
    toCombo.setPrefWidth(220);
        toCombo.setEditable(true);

        Label dateLabel = new Label("📅 Travel Date:");
        DatePicker dateField = new DatePicker();
        dateField.setPromptText("Select travel date");
    dateField.setPrefWidth(220);
        fromCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateDatePickerAvailability(fromCombo, toCombo, dateField));
        toCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateDatePickerAvailability(fromCombo, toCombo, dateField));
        dateLabel.getStyleClass().add("label");

        Label viaLabel = new Label("🛤️ Via:");
        viaLabel.getStyleClass().add("label");
        ComboBox<String> viaCombo = new ComboBox<>(cities);
        viaCombo.setPromptText("Select via station (optional)");
    viaCombo.setPrefWidth(220);
        viaCombo.setEditable(true);

        // Arrange From, To and Date side-by-side
        HBox controlsRow = new HBox(30);
        controlsRow.setAlignment(Pos.CENTER);

        VBox fromBox = new VBox(6, fromLabel, fromCombo);
        fromBox.setPrefWidth(280);
        fromBox.setAlignment(Pos.CENTER_LEFT);

        VBox toBox = new VBox(6, toLabel, toCombo);
        toBox.setPrefWidth(280);
        toBox.setAlignment(Pos.CENTER_LEFT);

        VBox dateBox = new VBox(6, dateLabel, dateField);
        dateBox.setPrefWidth(280);
        dateBox.setAlignment(Pos.CENTER_LEFT);

        VBox viaBox = new VBox(6, viaLabel, viaCombo);
        viaBox.setPrefWidth(280);
        viaBox.setAlignment(Pos.CENTER_LEFT);

        controlsRow.getChildren().addAll(fromBox, toBox, viaBox, dateBox);
        searchGrid.add(controlsRow, 0, 0, 2, 1);

        Button searchBtn = new Button("🔍 Search Trains");
        searchBtn.setPrefWidth(200);

        searchCard.getChildren().addAll(searchTitle, searchGrid, searchBtn);

        TableView<com.railway.booking.model.Train> trainTable = new TableView<>();
        trainTable.getStyleClass().add("table-view");

        TableColumn<com.railway.booking.model.Train, String> colName = new TableColumn<>("Train Name");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colName.setPrefWidth(180);

        TableColumn<com.railway.booking.model.Train, String> colSrc = new TableColumn<>("🚉 From");
        colSrc.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSource()));
        colSrc.setPrefWidth(150);

        TableColumn<com.railway.booking.model.Train, String> colDest = new TableColumn<>("🏁 To");
        colDest.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDestination()));
        colDest.setPrefWidth(150);

        TableColumn<com.railway.booking.model.Train, String> colDate = new TableColumn<>("� Date");
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));
        colDate.setPrefWidth(120);

        TableColumn<com.railway.booking.model.Train, String> colTime = new TableColumn<>("🕓 Time");
        colTime.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDepartureTime()));
        colTime.setPrefWidth(100);

        TableColumn<com.railway.booking.model.Train, Double> colPrice = new TableColumn<>("💰 Price");
        colPrice.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPrice()));
        colPrice.setPrefWidth(100);
        colPrice.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", price));
                }
            }
        });

        TableColumn<com.railway.booking.model.Train, Integer> colSeats = new TableColumn<>("💺 Seats");
        colSeats.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getSeats()).asObject());
        colSeats.setPrefWidth(90);

        TableColumn<com.railway.booking.model.Train, String> colVia = new TableColumn<>("🛤️ Via");
        colVia.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getVia()));
        colVia.setPrefWidth(150);

        TableColumn<com.railway.booking.model.Train, Void> colBook = new TableColumn<>("🎫 Action");
        colBook.setPrefWidth(140);

        trainTable.getColumns().addAll(colName, colSrc, colVia, colDest, colDate, colTime, colPrice, colSeats, colBook);
        trainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        trainTable.setFixedCellSize(44);
        trainTable.setStyle("-fx-font-size: 15px;");

        // Ensure the table can grow and be scrolled on smaller screens
        ScrollPane tableScroll = new ScrollPane(trainTable);
        tableScroll.setFitToWidth(true);
        tableScroll.setFitToHeight(true);
        tableScroll.setPannable(true);
        tableScroll.setStyle("-fx-background-color: transparent;");
        tableScroll.setPrefViewportHeight(300); // reasonable default so it shows on small windows

        colBook.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("🎫 Book Now");

            {
                btn.setOnAction(e -> {
                    com.railway.booking.model.Train train = getTableView().getItems().get(getIndex());

                    showClassAndSeatSelection(train, (trainClass, seatsToBook) -> {
                        if (train.getSeats() < seatsToBook) {
                            UIUtils.showErrorAlert("❌ Not enough seats available for this train!");
                            return;
                        }

                        double priceMultiplier = switch (trainClass) {
                            case "AC" -> 1.8;
                            case "First Class" -> 3.2;
                            default -> 1.0; // Sleeper
                        };
                        double finalPrice = train.getPrice() * seatsToBook * priceMultiplier;

                        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmation.setTitle("Confirm Booking");
                        confirmation.setHeaderText("Total Price: " + String.format("₹%.2f", finalPrice));
                        confirmation.setContentText("Do you want to proceed with this booking?");

                        confirmation.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                showPaymentSelection(paymentMethod -> {
                                    PaymentView.show(paymentMethod).ifPresent(paymentDetails -> {
                                        TravelerInfoView.show(seatsToBook).ifPresent(travelerInfo -> {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("trainId", train.getId());
                                            data.put("userId", userId);
                                            data.put("seats", seatsToBook);
                                            data.put("trainClass", trainClass);
                                            data.put("paymentMethod", paymentMethod);
                                            data.put("paymentDetails", paymentDetails);
                                            try {
                                                JsonObject json = apiClient.post("/book", data);
                                                if (json.get("success").getAsBoolean()) {
                                                    String pnr = json.has("pnr") ? json.get("pnr").getAsString() : "N/A";
                                                    UIUtils.showInfoAlert("✅ Booking successful! Your PNR is " + pnr);
                                                    PdfService.generateTicket(train, seatsToBook, trainClass, finalPrice, pnr, travelerInfo);
                                                    searchBtn.fire();
                                                } else {
                                                    UIUtils.showErrorAlert("❌ Booking failed: " + json.get("msg").getAsString());
                                                }
                                            } catch (Exception ex) {
                                                UIUtils.showErrorAlert("❌ Booking error: " + ex.getMessage());
                                            }
                                        });
                                    });
                                });
                            }
                        });
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    com.railway.booking.model.Train train = getTableView().getItems().get(getIndex());
                    int seats = train.getSeats();
                    btn.setDisable(seats <= 0);
                    btn.setText(seats <= 0 ? "❌ Sold Out" : "🎫 Book Now");
                    setGraphic(btn);
                }
            }
        });

        searchBtn.setOnAction(e -> {
            String src = fromCombo.getValue() != null ? fromCombo.getValue() : "";
            String dest = toCombo.getValue() != null ? toCombo.getValue() : "";
            String via = viaCombo.getValue() != null ? viaCombo.getValue() : "";
            String date = dateField.getValue() != null ? dateField.getValue().toString() : "";
            String url = String.format("/trains?from=%s&to=%s&date=%s&via=%s",
                    src.replace(" ", "%20"), dest.replace(" ", "%20"), date, via.replace(" ", "%20"));
            try {
                String resp = apiClient.get(url);
                com.railway.booking.model.Train[] trains = apiClient.fromJson(resp, com.railway.booking.model.Train[].class);
                trainTable.getItems().setAll(trains);
                if (trains.length == 0) UIUtils.showInfoAlert("🔍 No trains found. Try different cities or dates.");
            } catch (Exception ex) {
                UIUtils.showErrorAlert("❌ Search error: " + ex.getMessage());
            }
        });

    main.getChildren().addAll(searchCard, tableScroll);
    javafx.scene.layout.VBox.setVgrow(tableScroll, javafx.scene.layout.Priority.ALWAYS);
        return main;
    }

    private void updateDatePickerAvailability(ComboBox<String> fromCombo, ComboBox<String> toCombo, DatePicker dateField) {
        String src = fromCombo.getValue(), dest = toCombo.getValue();
        if (src == null || src.isEmpty() || dest == null || dest.isEmpty()) {
            dateField.setDayCellFactory(null);
            return;
        }
        Platform.runLater(() -> {
            Set<String> availableDates = new HashSet<>();
            try {
                String url = String.format("/trains?from=%s&to=%s", src.replace(" ", "%20"), dest.replace(" ", "%20"));
                String resp = apiClient.get(url);
                com.railway.booking.model.Train[] trains = apiClient.fromJson(resp, com.railway.booking.model.Train[].class);
                for (com.railway.booking.model.Train train : trains) {
                    availableDates.add(train.getDate());
                }
            } catch (Exception ex) {
                // Ignore error, date picker will just not be filtered
            }
            dateField.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(java.time.LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setDisable(true);
                    } else {
                        String dateStr = item.toString();
                        setDisable(!availableDates.contains(dateStr));
                        if (availableDates.contains(dateStr))
                            setStyle("-fx-background-color: #B5C99A; -fx-text-fill: #222023;");
                    }
                }
            });
        });
    }

    private void showClassAndSeatSelection(com.railway.booking.model.Train train, java.util.function.BiConsumer<String, Integer> onSelection) {
        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Select Class and Seats");
            dialog.setHeaderText("Choose your class and number of seats for " + train.getName());

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            ComboBox<String> classComboBox = new ComboBox<>(FXCollections.observableArrayList("Sleeper", "AC", "First Class"));
            classComboBox.getSelectionModel().selectFirst();

            Spinner<Integer> seatSpinner = new Spinner<>(1, train.getSeats() > 0 ? train.getSeats() : 1, 1);
            seatSpinner.setPrefWidth(80);

            grid.add(new Label("Class:"), 0, 0);
            grid.add(classComboBox, 1, 0);
            grid.add(new Label("Seats:"), 0, 1);
            grid.add(seatSpinner, 1, 1);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    onSelection.accept(classComboBox.getValue(), seatSpinner.getValue());
                }
            });
        });
    }

    private void showPaymentSelection(java.util.function.Consumer<String> onPayment) {
        Platform.runLater(() -> {
            ChoiceDialog<String> dlg = new ChoiceDialog<>("UPI", java.util.List.of("UPI", "Card", "Net Banking"));
            dlg.setTitle("Payment Method");
            dlg.setHeaderText("Choose how you want to pay:");
            dlg.setContentText("Method:");
            dlg.showAndWait().ifPresent(onPayment);
        });
    }
}
