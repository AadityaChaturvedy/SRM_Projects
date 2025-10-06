package com.railway.booking.view;

import com.railway.booking.service.ApiClient;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardView {

    private final ApiClient apiClient = new ApiClient();
    private int userId;
    private String currentUsername;

    public void show(Stage stage, int userId, String currentUsername, Runnable onLogout) {
        this.userId = userId;
        this.currentUsername = currentUsername;

        TabPane tabPane = new TabPane();
        
        SearchTrainView searchTrainView = new SearchTrainView(apiClient, userId);
        Tab searchTab = new Tab("🔍 Search Trains", searchTrainView.createSearchPane());
        searchTab.setClosable(false);

        MyBookingsView myBookingsView = new MyBookingsView(apiClient, userId);
        Tab bookingsTab = new Tab("🎫 My Bookings", myBookingsView.createBookingsPane());
        bookingsTab.setClosable(false);

        tabPane.getTabs().addAll(searchTab, bookingsTab);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 20, 10, 20));
        topBar.setSpacing(20);
        Label welcome = new Label("🎉 Welcome, " + currentUsername + "!");
        welcome.getStyleClass().add("welcome-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setOnAction(e -> onLogout.run());
        topBar.getChildren().addAll(welcome, spacer, logoutBtn);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
