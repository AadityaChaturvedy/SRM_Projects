package com.hostel.utils;

import com.hostel.database.*;
import com.hostel.models.Attendance;
import com.hostel.models.Complaint;
import com.hostel.models.InventoryItem;
import com.hostel.models.LeaveRequest;
import com.hostel.models.Notification;
import com.hostel.models.Payment;
import com.hostel.models.Room;
import com.hostel.models.Student;
import com.hostel.models.User;
import com.hostel.models.Visitor;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class MockDataGenerator {

    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[] COMPLAINT_KEYWORDS = {
            "water", "leak", "faucet", "toilet", "shower", "drain", "plumbing", "pipe",
            "electricity", "light", "bulb", "fan", "socket", "power", "wiring", "outlet",
            "door", "window", "lock", "hinge", "furniture", "bed", "chair", "table", "cupboard",
            "internet", "wifi", "network", "router", "connectivity",
            "cleaning", "dirty", "mess", "dust", "hygiene",
            "pest", "insect", "rodent", "bug",
            "heating", "cooling", "AC", "heater", "temperature",
            "wall", "paint", "crack", "damage",
            "noise", "loud", "disturbance"
    };

    public static void main(String[] args) {
        try {
            DatabaseManager.getConnection(); // Establish connection
            generateMockData(50);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                DatabaseManager.closeConnection(); // Close connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateMockData(int count) {
        System.out.println("Generating mock data...");

        UserDAO userDAO = new UserDAO();
        RoomDAO roomDAO = new RoomDAO();
        StudentDAO studentDAO = new StudentDAO();
        AttendanceDAO attendanceDAO = new AttendanceDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        ComplaintDAO complaintDAO = new ComplaintDAO();
        NotificationDAO notificationDAO = new NotificationDAO();
        InventoryDAO inventoryDAO = new InventoryDAO();
        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();

        VisitorDAO visitorDAO = new VisitorDAO();

        try {
            // Generate Users
            for (int i = 0; i < count; i++) {
                User user = new User(
                        0, // id will be auto-generated
                        "user" + i,
                        "password" + i,
                        (i % 2 == 0) ? "admin" : "staff",
                        "permission" + i
                );
                userDAO.createUser(user);
            }
            System.out.println("Generated " + count + " users.");

            // Generate Rooms
            for (int i = 0; i < count; i++) {
                Room room = new Room(
                        0, // id will be auto-generated
                        "R" + (100 + i),
                        RANDOM.nextInt(4) + 1, // capacity 1-4
                        (i % 3 == 0) ? "Occupied" : "Available"
                );
                roomDAO.createRoom(room);
            }
            System.out.println("Generated " + count + " rooms.");

            // Generate Students
            for (int i = 0; i < count; i++) {
                Student student = new Student(
                        0, // id will be auto-generated
                        "Student Name " + i,
                        "student" + i + "@example.com",
                        "123-456-78" + String.format("%02d", i),
                        "Address " + i,
                        RANDOM.nextInt(count) + 1 // room_id (assuming rooms are 1-indexed)
                );
                studentDAO.createStudent(student);
            }
            System.out.println("Generated " + count + " students.");



            // Generate Visitors
            for (int i = 0; i < count; i++) {
                Visitor visitor = new Visitor();
                visitor.setStudentId(RANDOM.nextInt(count) + 1);
                visitor.setVisitorName("Visitor " + i);
                visitor.setRelation((RANDOM.nextBoolean()) ? "Parent" : "Friend");
                visitor.setVisitDate(LocalDate.now().minusDays(RANDOM.nextInt(30)).format(DATE_FORMATTER));
                visitor.setStatus((RANDOM.nextBoolean()) ? "APPROVED" : "PENDING");
                visitorDAO.createVisitor(visitor);
            }
            System.out.println("Generated " + count + " visitors.");

            // Generate Attendance
            for (int i = 0; i < count; i++) {
                Attendance attendance = new Attendance(
                        0, // id will be auto-generated
                        RANDOM.nextInt(count) + 1, // student_id
                        LocalDate.now().minusDays(RANDOM.nextInt(30)).format(DATE_FORMATTER),
                        (RANDOM.nextBoolean()) ? "Present" : "Absent"
                );
                attendanceDAO.createAttendance(attendance);
            }
            System.out.println("Generated " + count + " attendance records.");

            // Generate Payments
            for (int i = 0; i < count; i++) {
                Payment payment = new Payment(
                        0, // id will be auto-generated
                        RANDOM.nextInt(count) + 1, // student_id
                        RANDOM.nextInt(1000), // amount
                        LocalDate.now().minusDays(RANDOM.nextInt(60)).format(DATE_FORMATTER),
                        (RANDOM.nextBoolean()) ? "Paid" : "Pending"
                );
                paymentDAO.createPayment(payment);
            }
            System.out.println("Generated " + count + " payments.");

            // Generate Complaints
            for (int i = 0; i < count; i++) {
                Complaint complaint = new Complaint(
                        0, // id will be auto-generated
                        RANDOM.nextInt(count) + 1, // student_id
                        COMPLAINT_KEYWORDS[RANDOM.nextInt(COMPLAINT_KEYWORDS.length)] + " issue in room " + (RANDOM.nextInt(count) + 1),
                        LocalDate.now().minusDays(RANDOM.nextInt(90)).format(DATE_FORMATTER),
                        (i % 3 == 0) ? "Resolved" : "Pending"
                );
                complaintDAO.createComplaint(complaint);
            }
            System.out.println("Generated " + count + " complaints.");

            // Generate Notifications
            for (int i = 0; i < count; i++) {
                Notification notification = new Notification(
                        0, // id will be auto-generated
                        RANDOM.nextInt(count) + 1, // user_id
                        "Notification message " + i + ": " + UUID.randomUUID().toString().substring(0, 8),
                        LocalDateTime.now().minusMinutes(RANDOM.nextInt(120)).format(DATETIME_FORMATTER),
                        RANDOM.nextBoolean()
                );
                notificationDAO.createNotification(notification);
            }
            System.out.println("Generated " + count + " notifications.");

            // Generate Inventory
            for (int i = 0; i < count; i++) {
                InventoryItem item = new InventoryItem(
                        0, // id will be auto-generated
                        "Item " + i,
                        "Description for item " + i,
                        RANDOM.nextInt(100) + 1, // quantity
                        (i % 2 == 0) ? "In Stock" : "Low Stock"
                );
                inventoryDAO.createInventoryItem(item);
            }
            System.out.println("Generated " + count + " inventory items.");

            // Generate Leave Requests
            for (int i = 0; i < count; i++) {
                LocalDate startDate = LocalDate.now().plusDays(RANDOM.nextInt(30));
                LocalDate endDate = startDate.plusDays(RANDOM.nextInt(7) + 1);
                LeaveRequest leaveRequest = new LeaveRequest(
                        0, // id will be auto-generated
                        RANDOM.nextInt(count) + 1, // student_id
                        startDate.format(DATE_FORMATTER),
                        endDate.format(DATE_FORMATTER),
                        "Reason for leave " + i,
                        (i % 2 == 0) ? "APPROVED" : "PENDING",
                        LocalDate.now().minusDays(RANDOM.nextInt(10)).format(DATE_FORMATTER)
                );
                leaveRequestDAO.createLeaveRequest(leaveRequest);
            }
            System.out.println("Generated " + count + " leave requests.");

            System.out.println("Mock data generation complete.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}