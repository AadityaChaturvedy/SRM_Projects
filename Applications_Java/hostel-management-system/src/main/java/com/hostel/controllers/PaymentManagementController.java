package com.hostel.controllers;

import com.hostel.database.PaymentDAO;
import com.hostel.models.Payment;
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

public class PaymentManagementController implements Observer<List<Payment>> {

    @FXML
    private TableView<Payment> paymentTable;

    @FXML
    private TableColumn<Payment, Integer> idColumn;

    @FXML
    private TableColumn<Payment, Integer> studentIdColumn;

    @FXML
    private TableColumn<Payment, Double> amountColumn;

    @FXML
    private TableColumn<Payment, String> dateColumn;

    @FXML
    private TableColumn<Payment, String> statusColumn;

    private final PaymentDAO paymentDAO = new PaymentDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        paymentDAO.addObserver(this);
        loadPayments();
    }

    @Override
    public void update(List<Payment> payments) {
        paymentTable.setItems(FXCollections.observableArrayList(payments));
    }

    

    private void loadPayments() {
        try {
            ObservableList<Payment> payments = FXCollections.observableArrayList(paymentDAO.getAllPayments());
            paymentTable.setItems(payments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddPayment.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Payment");
            stage.showAndWait();
            loadPayments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editPayment() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditPayment.fxml"));
                Parent root = loader.load();

                EditPaymentController controller = loader.getController();
                controller.setPayment(selectedPayment);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit Payment");
                stage.showAndWait();
                loadPayments();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deletePayment() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Payment");
            alert.setHeaderText("Are you sure you want to delete this payment record?");
            alert.setContentText("Student ID: " + selectedPayment.getStudentId() + ", Amount: " + selectedPayment.getAmount());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        paymentDAO.deletePayment(selectedPayment.getId());
                        loadPayments();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) paymentTable.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }


}
