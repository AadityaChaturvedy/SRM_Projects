package com.hostel.database;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hostel.models.User;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements Observable<List<User>> {

    private List<Observer<List<User>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<User>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<User>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<User> data) {
        for (Observer<List<User>> observer : observers) {
            observer.update(data);
        }
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, permissions) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray()));
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getPermissions());
            pstmt.executeUpdate();
            notifyObservers(getAllUsers());
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("permissions")
                );
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("permissions")
                ));
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, permissions = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getPermissions());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
            notifyObservers(getAllUsers());
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllUsers());
        }
    }

    public void createDefaultAdminUser() throws SQLException {
        if (getUserByUsername("admin") == null) {
            User adminUser = new User(0, "admin", BCrypt.withDefaults().hashToString(12, "admin".toCharArray()), "ADMIN", "ADMIN");
            createUser(adminUser);
        }
    }
}
