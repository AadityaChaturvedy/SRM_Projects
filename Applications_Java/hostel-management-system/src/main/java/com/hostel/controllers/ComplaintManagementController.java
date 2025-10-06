package com.hostel.controllers;

import com.hostel.database.ComplaintDAO;
import com.hostel.models.Complaint;
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

public class ComplaintManagementController implements Observer<List<Complaint>> {

    @FXML
    private TableView<Complaint> complaintTable;

    @FXML
    private TableColumn<Complaint, Integer> idColumn;

    @FXML
    private TableColumn<Complaint, Integer> studentIdColumn;

    @FXML
    private TableColumn<Complaint, String> complaintTextColumn;

    @FXML
    private TableColumn<Complaint, String> dateColumn;

    @FXML
    private TableColumn<Complaint, String> statusColumn;

    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        complaintTextColumn.setCellValueFactory(new PropertyValueFactory<>("complaintText"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        complaintTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        complaintDAO.addObserver(this);
        loadComplaints();
    }

    @Override
    public void update(List<Complaint> complaints) {
        complaintTable.setItems(FXCollections.observableArrayList(complaints));
    }

    private void loadComplaints() {
        try {
            ObservableList<Complaint> complaints = FXCollections.observableArrayList(complaintDAO.getAllComplaints());
            complaintTable.setItems(complaints);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void addComplaint() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddComplaint.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Complaint");
            stage.showAndWait();
            loadComplaints();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editComplaint() {
        Complaint selectedComplaint = complaintTable.getSelectionModel().getSelectedItem();
        if (selectedComplaint != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditComplaint.fxml"));
                Parent root = loader.load();

                EditComplaintController controller = loader.getController();
                controller.setComplaint(selectedComplaint);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit Complaint");
                stage.showAndWait();
                loadComplaints();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteComplaint() {
        Complaint selectedComplaint = complaintTable.getSelectionModel().getSelectedItem();
        if (selectedComplaint != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Complaint");
            alert.setHeaderText("Are you sure you want to delete this complaint record?");
            alert.setContentText("Student ID: " + selectedComplaint.getStudentId() + ", Complaint: " + selectedComplaint.getComplaintText());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        complaintDAO.deleteComplaint(selectedComplaint.getId());
                        loadComplaints();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) complaintTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
