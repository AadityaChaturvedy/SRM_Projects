package com.hostel.controllers;

import com.hostel.database.LeaveRequestDAO;
import com.hostel.models.LeaveRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class LeaveManagementController {

    @FXML
    private TableView<LeaveRequest> leaveRequestsTable;
    @FXML
    private TableColumn<LeaveRequest, String> studentNameColumn;
    @FXML
    private TableColumn<LeaveRequest, String> startDateColumn;
    @FXML
    private TableColumn<LeaveRequest, String> endDateColumn;
    @FXML
    private TableColumn<LeaveRequest, String> reasonColumn;
    @FXML
    private TableColumn<LeaveRequest, String> statusColumn;
    @FXML
    private TableColumn<LeaveRequest, String> requestDateColumn;

    private LeaveRequestDAO leaveRequestDAO;
    private ObservableList<LeaveRequest> leaveRequests;

    @FXML
    public void initialize() {
        leaveRequestDAO = new LeaveRequestDAO();
        setupTable();
        loadLeaveRequests();
    }

    private void setupTable() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        leaveRequestsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void loadLeaveRequests() {
        try {
            leaveRequests = FXCollections.observableArrayList(leaveRequestDAO.getAllLeaveRequests());
            leaveRequestsTable.setItems(leaveRequests);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load leave requests.");
        }
    }

    @FXML
    private void handleApprove() {
        LeaveRequest selectedRequest = leaveRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            updateRequestStatus(selectedRequest, "APPROVED");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a leave request to approve.");
        }
    }

    @FXML
    private void handleReject() {
        LeaveRequest selectedRequest = leaveRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            updateRequestStatus(selectedRequest, "REJECTED");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a leave request to reject.");
        }
    }

    private void updateRequestStatus(LeaveRequest request, String status) {
        try {
            leaveRequestDAO.updateLeaveRequestStatus(request.getId(), status);
            request.setStatus(status);
            leaveRequestsTable.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Leave request has been " + status.toLowerCase() + ".");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update leave request status.");
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
        ((javafx.stage.Stage) leaveRequestsTable.getScene().getWindow()).close();
    }
}
