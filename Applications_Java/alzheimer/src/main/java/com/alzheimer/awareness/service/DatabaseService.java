package com.alzheimer.awareness.service;

import com.alzheimer.awareness.model.QuizResult;
import com.alzheimer.awareness.model.User;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:alzheimer_app.db";

    public static void executeSqlScript(Connection conn, String resourcePath) {
        System.out.println("Executing SQL script: " + resourcePath);
        try (Statement stmt = conn.createStatement();
             InputStream inputStream = DatabaseService.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            StringBuilder statementBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                statementBuilder.append(line);
                if (line.endsWith(";")) {
                    String sqlStatement = statementBuilder.toString();
                    System.out.println("Executing: " + sqlStatement);
                    try {
                        stmt.execute(sqlStatement);
                    } catch (SQLException e) {
                        System.err.println("Error executing statement: " + sqlStatement);
                        System.err.println(e.getMessage());
                    }
                    statementBuilder.setLength(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to execute script " + resourcePath + ": " + e.getMessage());
        }
    }

    public static User registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In a real app, hash the password!
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new User(generatedKeys.getInt(1), username, password);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("User registration failed: " + e.getMessage());
        }
        return null;
    }

    public static User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In a real app, compare hashed passwords!
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.err.println("User login failed: " + e.getMessage());
        }
        return null;
    }

    public static void saveQuizResult(int userId, QuizResult result) {
        String sql = "INSERT INTO quiz_history (user_id, score, total_questions, risk_indicator, recommendation) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, result.getScore());
            pstmt.setInt(3, result.getTotalQuestions());
            pstmt.setString(4, result.getRiskIndicator());
            pstmt.setString(5, result.getRecommendation());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save quiz result: " + e.getMessage());
        }
    }

    public static List<QuizResult> getQuizHistory(int userId) {
        List<QuizResult> history = new ArrayList<>();
        String sql = "SELECT * FROM quiz_history WHERE user_id = ? ORDER BY completed_at DESC LIMIT 10";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    QuizResult result = new QuizResult(
                            rs.getInt("score"),
                            rs.getInt("total_questions"),
                            rs.getString("risk_indicator"),
                            rs.getString("recommendation")
                    );
                    history.add(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve quiz history: " + e.getMessage());
        }
        return history;
    }
}