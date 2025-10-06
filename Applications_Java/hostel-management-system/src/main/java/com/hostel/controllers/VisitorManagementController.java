package com.hostel.controllers;

import com.hostel.database.VisitorDAO;
import com.hostel.models.Visitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class VisitorManagementController {

    @FXML
    private TableView<Visitor> visitorTable;
    @FXML
    private TableColumn<Visitor, String> studentNameColumn;
    @FXML
    private TableColumn<Visitor, String> visitorNameColumn;
    @FXML
    private TableColumn<Visitor, String> relationColumn;
    @FXML
    private TableColumn<Visitor, String> visitDateColumn;
    @FXML
    private TableColumn<Visitor, String> statusColumn;

    private VisitorDAO visitorDAO;
    private ObservableList<Visitor> visitors;

    @FXML
    public void initialize() {
        visitorDAO = new VisitorDAO();
        setupTable();
        loadVisitors();
    }

    private void setupTable() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        visitorNameColumn.setCellValueFactory(new PropertyValueFactory<>("visitorName"));
        relationColumn.setCellValueFactory(new PropertyValueFactory<>("relation"));
        visitDateColumn.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        visitorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void loadVisitors() {
        try {
            visitors = FXCollections.observableArrayList(visitorDAO.getAllVisitors());
            visitorTable.setItems(visitors);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load visitor requests.");
        }
    }

    @FXML
    private void handleAllowEntry() {
        Visitor selectedVisitor = visitorTable.getSelectionModel().getSelectedItem();
        if (selectedVisitor != null) {
            updateVisitorStatus(selectedVisitor, "ALLOWED");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a visitor to allow.");
        }
    }

    @FXML
    private void handleDenyEntry() {
        Visitor selectedVisitor = visitorTable.getSelectionModel().getSelectedItem();
        if (selectedVisitor != null) {
            updateVisitorStatus(selectedVisitor, "DENIED");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a visitor to deny.");
        }
    }

    private void updateVisitorStatus(Visitor visitor, String status) {
        try {
            visitorDAO.updateVisitorStatus(visitor.getId(), status);
            visitor.setStatus(status);
            visitorTable.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Visitor has been " + status.toLowerCase() + ".");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update visitor status.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        ((javafx.stage.Stage) visitorTable.getScene().getWindow()).close();
    }
}
