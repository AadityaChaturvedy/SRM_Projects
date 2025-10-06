package com.hostel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:hostel.db";
    private static Connection connection;
    private static boolean isInitialized = false;

    private DatabaseManager() {
        // private constructor to prevent instantiation
    }

    public static void initializeDatabase() {
        if (!isInitialized) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                if (connection != null) {
                    initDatabase(connection);
                    isInitialized = true;
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static void initDatabase(Connection conn) {
        System.out.println("Initializing database...");
        try (java.io.InputStream in = DatabaseManager.class.getResourceAsStream("/database/schema.sql");
             java.util.Scanner s = new java.util.Scanner(in).useDelimiter(";")) {
            if (in == null) {
                System.err.println("schema.sql not found in classpath!");
                return;
            }
            while (s.hasNext()) {
                String sql = s.next().trim();
                if (!sql.isEmpty()) {
                    System.out.println("Executing SQL: " + sql);
                    conn.createStatement().execute(sql);
                }
            }
            System.out.println("Database initialization complete.");

            // Populate mock data
            RoomDAO roomDAO = new RoomDAO();
            roomDAO.populateMockData();

            StudentDAO studentDAO = new StudentDAO();
            studentDAO.populateMockData();

            AttendanceDAO attendanceDAO = new AttendanceDAO();
            attendanceDAO.populateMockData();

            PaymentDAO paymentDAO = new PaymentDAO();
            paymentDAO.populateMockData();

            ComplaintDAO complaintDAO = new ComplaintDAO();
            complaintDAO.populateMockData();

            InventoryDAO inventoryDAO = new InventoryDAO();
            inventoryDAO.populateMockData();

            LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
            leaveRequestDAO.populateMockData();

            VisitorDAO visitorDAO = new VisitorDAO();
            visitorDAO.populateMockData();

        } catch (java.io.IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}