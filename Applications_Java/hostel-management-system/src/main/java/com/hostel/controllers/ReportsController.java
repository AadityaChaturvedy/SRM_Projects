package com.hostel.controllers;

import com.hostel.database.ReportDAO;
import com.hostel.models.ReportData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ReportsController {

    @FXML
    private Label occupancyReportLabel;

    @FXML
    private Label paymentReportLabel;

    @FXML
    private Label attendanceReportLabel;

    @FXML
    private Label complaintReportLabel;

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    private void initialize() {
        loadReports();
    }

    private void loadReports() {
        try {
            ReportData occupancyReport = reportDAO.getOccupancyReport();
            occupancyReportLabel.setText(formatReportData(occupancyReport));

            ReportData paymentReport = reportDAO.getPaymentSummaryReport();
            paymentReportLabel.setText(formatReportData(paymentReport));

            ReportData attendanceReport = reportDAO.getAttendanceSummaryReport();
            attendanceReportLabel.setText(formatReportData(attendanceReport));

            ReportData complaintReport = reportDAO.getComplaintSummaryReport();
            complaintReportLabel.setText(formatReportData(complaintReport));

        } catch (SQLException e) {
            e.printStackTrace();
            occupancyReportLabel.setText("Error loading report.");
            paymentReportLabel.setText("Error loading report.");
            attendanceReportLabel.setText("Error loading report.");
            complaintReportLabel.setText("Error loading report.");
        }
    }

    private String formatReportData(ReportData report) {
        StringBuilder sb = new StringBuilder();
        sb.append(report.getReportName()).append(":\n");
        report.getData().forEach((key, value) -> sb.append("  ").append(key).append(": ").append(value).append("\n"));
        return sb.toString();
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) occupancyReportLabel.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
