package com.example.edubase.services;

import org.mindrot.jbcrypt.BCrypt;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:edubase.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() throws SQLException {
    File dbFile = new File("edubase.db");
        boolean isNewDB = !dbFile.exists();

        if (isNewDB) {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                System.out.println("Creating new database and tables...");

                // Users Table with hashed password
                String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password_hash TEXT NOT NULL," +
                        "role TEXT NOT NULL);";
                stmt.execute(createUserTable);

                // Students Table
                String createStudentTable = "CREATE TABLE IF NOT EXISTS students (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "age INTEGER NOT NULL," +
                        "address TEXT NOT NULL," +
                        "city TEXT NOT NULL," +
                        "state TEXT NOT NULL," +
                        "phone TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE);";
                stmt.execute(createStudentTable);

                // Subjects Table - stores attendance, cycle1-4, and total marks
                String createSubjectsTable = "CREATE TABLE IF NOT EXISTS subjects (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "student_id INTEGER NOT NULL," +
                        "subject_name TEXT NOT NULL," +
                        "attendance INTEGER NOT NULL," +
                        "cycle1 INTEGER NOT NULL," +
                        "cycle2 INTEGER NOT NULL," +
                        "cycle3 INTEGER NOT NULL," +
                        "cycle4 INTEGER NOT NULL," +
                        "total INTEGER NOT NULL," +
                        "FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE);";
                stmt.execute(createSubjectsTable);

                // Insert Sample Data with HASHED password
                String adminPassword = "admin";
                String hashedPassword = BCrypt.hashpw(adminPassword, BCrypt.gensalt());
                stmt.execute("INSERT INTO users (username, password_hash, role) VALUES ('admin', '" + hashedPassword + "', 'ADMIN');");

                stmt.execute("INSERT INTO students (name, age, address, city, state, phone, email) VALUES " +
                        "('Alice Johnson', 22, '123 Maple St', 'Springfield', 'Illinois', '5551234567', 'alice@example.com');");
                stmt.execute("INSERT INTO students (name, age, address, city, state, phone, email) VALUES " +
                        "('Bob Smith', 21, '456 Oak Ave', 'Shelbyville', 'Illinois', '5557654321', 'bob@example.com');");

                String[] subjects = {"APP", "COA", "DSA", "Maths", "OS"};
                for (String subject : subjects) {
                    int a1 = 80 + (int)(Math.random() * 20);
                    int c1 = 15 + (int)(Math.random() * 10);
                    int c2 = 15 + (int)(Math.random() * 10);
                    int c3 = 15 + (int)(Math.random() * 10);
                    int c4 = 15 + (int)(Math.random() * 10);
                    int total = c1 + c2 + c3 + c4;
                    stmt.execute("INSERT INTO subjects (student_id, subject_name, attendance, cycle1, cycle2, cycle3, cycle4, total) VALUES (1, '" + subject + "', " + a1 + ", " + c1 + ", " + c2 + ", " + c3 + ", " + c4 + ", " + total + ");");

                    int a2 = 85 + (int)(Math.random() * 15);
                    int rc1 = 15 + (int)(Math.random() * 10);
                    int rc2 = 15 + (int)(Math.random() * 10);
                    int rc3 = 15 + (int)(Math.random() * 10);
                    int rc4 = 15 + (int)(Math.random() * 10);
                    int rTotal = rc1 + rc2 + rc3 + rc4;
                    stmt.execute("INSERT INTO subjects (student_id, subject_name, attendance, cycle1, cycle2, cycle3, cycle4, total) VALUES (2, '" + subject + "', " + a2 + ", " + rc1 + ", " + rc2 + ", " + rc3 + ", " + rc4 + ", " + rTotal + ");");
                }

                System.out.println("Sample data inserted.");
            }
        }
    }
}