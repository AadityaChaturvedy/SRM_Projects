package com.hostel.controllers;

import com.hostel.utils.FeeReminderService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        // Initialize and start the background task for sending fee reminders
        FeeReminderService feeReminderService = new FeeReminderService();

        Timeline timeline = new Timeline(new KeyFrame(Duration.hours(24), event -> {
            System.out.println("Running daily fee reminder check...");
            feeReminderService.sendReminders();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void logout() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        stage.setScene(new Scene(root));
    }

    @FXML
    private void openRoomManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoomManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Room Management");
        stage.show();
    }

    @FXML
    private void openStudentManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Student Management");
        stage.show();
    }

    @FXML
    private void openAttendanceManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AttendanceManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Attendance Management");
        stage.show();
    }

    @FXML
    private void openPaymentManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Payment Management");
        stage.show();
    }

    @FXML
    private void openComplaintManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComplaintManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Complaint Management");
        stage.show();
    }

    @FXML
    private void openMessMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MessMenu.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Mess Menu");
        stage.show();
    }

    @FXML
    private void openExportData() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExportData.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Export Data");
        stage.show();
    }

    @FXML
    private void openRoleManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoleManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Role Management");
        stage.show();
    }

    @FXML
    private void openReports() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Reports.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Reports and Analytics");
        stage.show();
    }



    @FXML
    private void openCheckInOut() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CheckInOut.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Check-in/Check-out");
        stage.show();
    }

    @FXML
    private void openInventoryManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InventoryManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Inventory Management");
        stage.show();
    }

    @FXML
    private void openLeaveManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LeaveManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Leave Management");
        stage.show();
    }

    @FXML
    private void openAnalyticsDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AnalyticsDashboard.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Analytics Dashboard");
        stage.show();
    }

    @FXML
    private void openVisitorManagement() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VisitorManagement.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Visitor Management");
        stage.show();
    }





    @FXML
    private void openMaintenanceReport() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MaintenanceReport.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Maintenance Report");
        stage.show();
    }
}
