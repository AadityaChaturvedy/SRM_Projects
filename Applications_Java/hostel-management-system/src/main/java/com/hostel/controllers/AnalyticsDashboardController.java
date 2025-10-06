package com.hostel.controllers;

import com.hostel.database.PaymentDAO;
import com.hostel.database.RoomDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.util.Map;

public class AnalyticsDashboardController {

    @FXML
    private BarChart<String, Number> roomOccupancyChart;
    @FXML
    private PieChart paymentStatusChart;

    private RoomDAO roomDAO;
    private PaymentDAO paymentDAO;

    @FXML
    public void initialize() {
        roomDAO = new RoomDAO();
        paymentDAO = new PaymentDAO();

        loadRoomOccupancyData();
        loadPaymentStatusData();
    }

    private void loadRoomOccupancyData() {
        try {
            Map<String, Integer> occupancyData = roomDAO.getRoomOccupancyStatus();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Rooms");

            for (Map.Entry<String, Integer> entry : occupancyData.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            roomOccupancyChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPaymentStatusData() {
        try {
            Map<String, Integer> paymentData = paymentDAO.getPaymentStatusDistribution();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (Map.Entry<String, Integer> entry : paymentData.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            paymentStatusChart.setData(pieChartData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        ((javafx.stage.Stage) paymentStatusChart.getScene().getWindow()).close();
    }
}
