package com.hostel.controllers;

import com.hostel.database.ReportDAO;
import com.hostel.models.ReportData;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaintenanceReportController {

    @FXML
    private ListView<String> reportListView;

    private ReportDAO reportDAO;

    @FXML
    public void initialize() {
        reportDAO = new ReportDAO();
        generateReport();
    }

    private void generateReport() {
        try {
            ReportData reportData = reportDAO.getMaintenanceTrendReport();
            List<String> reportLines = new ArrayList<>();
            reportLines.add(reportData.getReportName() + ":");

            if (reportData.getData().isEmpty()) {
                reportLines.add("No recurring maintenance issues found.");
            } else {
                for (Map.Entry<String, String> entry : reportData.getData().entrySet()) {
                    reportLines.add(String.format("Issue: '%s' | Occurrences: %s", entry.getKey(), entry.getValue()));
                }
            }

            reportListView.setItems(FXCollections.observableArrayList(reportLines));

        } catch (SQLException e) {
            e.printStackTrace();
            reportListView.setItems(FXCollections.observableArrayList("Error generating report."));
        }
    }

    @FXML
    private void goBack() {
        ((javafx.stage.Stage) reportListView.getScene().getWindow()).close();
    }
}