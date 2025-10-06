package com.hostel.controllers;

import com.hostel.database.InventoryDAO;
import com.hostel.models.InventoryItem;
import com.hostel.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class InventoryManagementController implements Observer<List<InventoryItem>> {

    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, Integer> idColumn;

    @FXML
    private TableColumn<InventoryItem, String> nameColumn;

    @FXML
    private TableColumn<InventoryItem, String> descriptionColumn;

    @FXML
    private TableColumn<InventoryItem, Integer> quantityColumn;

    @FXML
    private TableColumn<InventoryItem, String> statusColumn;

    private final InventoryDAO inventoryDAO = new InventoryDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        inventoryDAO.addObserver(this);
        loadInventoryItems();
    }

    @Override
    public void update(List<InventoryItem> items) {
        inventoryTable.setItems(FXCollections.observableArrayList(items));
    }

    private void loadInventoryItems() {
        try {
            ObservableList<InventoryItem> items = FXCollections.observableArrayList(inventoryDAO.getAllInventoryItems());
            inventoryTable.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addInventoryItem() {
        // TODO: Implement add inventory item functionality
    }

    @FXML
    private void editInventoryItem() {
        // TODO: Implement edit inventory item functionality
    }

    @FXML
    private void deleteInventoryItem() {
        // TODO: Implement delete inventory item functionality
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) inventoryTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
