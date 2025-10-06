package com.hostel.database;

import com.hostel.models.InventoryItem;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO implements Observable<List<InventoryItem>> {

    private List<Observer<List<InventoryItem>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<InventoryItem>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<InventoryItem>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<InventoryItem> data) {
        for (Observer<List<InventoryItem>> observer : observers) {
            observer.update(data);
        }
    }

    public void createInventoryItem(InventoryItem item) throws SQLException {
        String sql = "INSERT INTO inventory (name, description, quantity, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setString(4, item.getStatus());
            pstmt.executeUpdate();
            notifyObservers(getAllInventoryItems());
        }
    }

    public List<InventoryItem> getAllInventoryItems() throws SQLException {
        String sql = "SELECT * FROM inventory";
        List<InventoryItem> items = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                ));
            }
        }
        return items;
    }

    public void updateInventoryItem(InventoryItem item) throws SQLException {
        String sql = "UPDATE inventory SET name = ?, description = ?, quantity = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setString(4, item.getStatus());
            pstmt.setInt(5, item.getId());
            pstmt.executeUpdate();
            notifyObservers(getAllInventoryItems());
        }
    }

    public void deleteInventoryItem(int id) throws SQLException {
        String sql = "DELETE FROM inventory WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllInventoryItems());
        }
    }

    public void populateMockData() {
        try {
            List<InventoryItem> items = getAllInventoryItems();
            if (items.isEmpty()) {
                System.out.println("Populating inventory with mock data...");
                createInventoryItem(new InventoryItem(0, "Beds", "Beds for students", 100, "Available"));
                createInventoryItem(new InventoryItem(0, "Mattresses", "Mattresses for students", 100, "Available"));
                createInventoryItem(new InventoryItem(0, "Chairs", "Chairs for students", 200, "Available"));
                createInventoryItem(new InventoryItem(0, "Tables", "Tables for students", 100, "Available"));
                createInventoryItem(new InventoryItem(0, "Fans", "Fans for students", 100, "Available"));
                System.out.println("Mock data for inventory populated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
