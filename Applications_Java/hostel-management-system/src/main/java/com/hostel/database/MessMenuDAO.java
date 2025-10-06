package com.hostel.database;

import com.hostel.models.MessMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessMenuDAO {

    public void createMessMenuTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS mess_menu (" +
                "menu_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "day_of_week TEXT NOT NULL UNIQUE," +
                "breakfast TEXT NOT NULL," +
                "lunch TEXT NOT NULL," +
                "snacks TEXT NOT NULL," +
                "dinner TEXT NOT NULL" +
                ");";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void addMessMenu(MessMenu messMenu) throws SQLException {
        String sql = "INSERT INTO mess_menu(day_of_week, breakfast, lunch, snacks, dinner) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, messMenu.getDayOfWeek());
            pstmt.setString(2, messMenu.getBreakfast());
            pstmt.setString(3, messMenu.getLunch());
            pstmt.setString(4, messMenu.getSnacks());
            pstmt.setString(5, messMenu.getDinner());
            pstmt.executeUpdate();
        }
    }

    public List<MessMenu> getAllMessMenus() throws SQLException {
        List<MessMenu> messMenus = new ArrayList<>();
        String sql = "SELECT * FROM mess_menu ORDER BY menu_id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                messMenus.add(new MessMenu(
                        rs.getInt("menu_id"),
                        rs.getString("day_of_week"),
                        rs.getString("breakfast"),
                        rs.getString("lunch"),
                        rs.getString("snacks"),
                        rs.getString("dinner")
                ));
            }
        }
        return messMenus;
    }

    public void updateMessMenu(MessMenu messMenu) throws SQLException {
        String sql = "UPDATE mess_menu SET day_of_week = ?, breakfast = ?, lunch = ?, snacks = ?, dinner = ? WHERE menu_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, messMenu.getDayOfWeek());
            pstmt.setString(2, messMenu.getBreakfast());
            pstmt.setString(3, messMenu.getLunch());
            pstmt.setString(4, messMenu.getSnacks());
            pstmt.setString(5, messMenu.getDinner());
            pstmt.setInt(6, messMenu.getMenuId());
            pstmt.executeUpdate();
        }
    }

    public void deleteMessMenu(int menuId) throws SQLException {
        String sql = "DELETE FROM mess_menu WHERE menu_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuId);
            pstmt.executeUpdate();
        }
    }

    public MessMenu getMessMenuByDay(String dayOfWeek) throws SQLException {
        String sql = "SELECT * FROM mess_menu WHERE day_of_week = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dayOfWeek);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MessMenu(
                            rs.getInt("menu_id"),
                            rs.getString("day_of_week"),
                            rs.getString("breakfast"),
                            rs.getString("lunch"),
                            rs.getString("snacks"),
                            rs.getString("dinner")
                    );
                }
            }
        }
        return null;
    }

    public void populateMockData() throws SQLException {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] breakfasts = {"Poha", "Upma", "Idli", "Dosa", "Paratha", "Bread Omelette", "Corn Flakes"};
        String[] lunches = {"Rajma Chawal", "Kadhi Chawal", "Chole Bhature", "Dal Makhani", "Veg Biryani", "Chicken Curry", "Fish Curry"};
        String[] snacks = {"Samosa", "Kachori", "Vada Pav", "Dhokla", "Bhel Puri", "Pani Puri", "Sev Puri"};
        String[] dinners = {"Aloo Gobi", "Paneer Butter Masala", "Mix Veg", "Egg Curry", "Chicken Korma", "Dal Fry", "Jeera Aloo"};

        for (int i = 0; i < days.length; i++) {
            if (getMessMenuByDay(days[i]) == null) {
                addMessMenu(new MessMenu(0, days[i], breakfasts[i], lunches[i], snacks[i], dinners[i]));
            }
        }
    }
}
