package com.hostel.controllers;

import com.hostel.database.RoomDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Room;
import com.hostel.models.Student;
import javafx.fxml.FXML;
import com.hostel.database.AttendanceDAO;
import com.hostel.database.ComplaintDAO;
import com.hostel.database.PaymentDAO;
import com.hostel.models.Attendance;
import com.hostel.models.Complaint;
import com.hostel.models.Payment;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExportDataController {

    private final StudentDAO studentDAO = new StudentDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @FXML
    private Button exportStudentsButton;

    @FXML
    private void exportStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Students");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Phone");
            headerRow.createCell(4).setCellValue("Address");
            headerRow.createCell(5).setCellValue("Room ID");

            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getEmail());
                row.createCell(3).setCellValue(student.getPhone());
                row.createCell(4).setCellValue(student.getAddress());
                row.createCell(5).setCellValue(student.getRoomId());
            }

            try (FileOutputStream fileOut = new FileOutputStream("students.xlsx")) {
                workbook.write(fileOut);
            }
            workbook.close();
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Students data exported to students.xlsx");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export students data.");
        }
    }

    @FXML
    private void exportRooms() {
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Rooms");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Room Number");
            headerRow.createCell(2).setCellValue("Capacity");
            headerRow.createCell(3).setCellValue("Status");

            int rowNum = 1;
            for (Room room : rooms) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(room.getId());
                row.createCell(1).setCellValue(room.getRoomNumber());
                row.createCell(2).setCellValue(room.getCapacity());
                row.createCell(3).setCellValue(room.getStatus());
            }

            try (FileOutputStream fileOut = new FileOutputStream("rooms.xlsx")) {
                workbook.write(fileOut);
            }
            workbook.close();
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Rooms data exported to rooms.xlsx");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export rooms data.");
        }
    }

    @FXML
    private void exportStudentsCSV() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("students.csv"))) {
                writer.write("ID,Name,Email,Phone,Address,Room ID\n");
                for (Student student : students) {
                    writer.write(student.getId() + "," + student.getName() + "," + student.getEmail() + "," + student.getPhone() + "," + student.getAddress() + "," + student.getRoomId() + "\n");
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Students data exported to students.csv");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export students data to CSV.");
        }
    }

    @FXML
    private void exportRoomsCSV() {
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("rooms.csv"))) {
                writer.write("ID,Room Number,Capacity,Status\n");
                for (Room room : rooms) {
                    writer.write(room.getId() + "," + room.getRoomNumber() + "," + room.getCapacity() + "," + room.getStatus() + "\n");
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Rooms data exported to rooms.csv");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export rooms data to CSV.");
        }
    }

    @FXML
    private void exportAttendanceCSV() {
        try {
            List<Attendance> attendances = attendanceDAO.getAllAttendance();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("attendance.csv"))) {
                writer.write("ID,Student ID,Date,Status\n");
                for (Attendance attendance : attendances) {
                    writer.write(attendance.getId() + "," + attendance.getStudentId() + "," + attendance.getDate() + "," + attendance.getStatus() + "\n");
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Attendance data exported to attendance.csv");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export attendance data to CSV.");
        }
    }

    @FXML
    private void exportPaymentsCSV() {
        try {
            List<Payment> payments = paymentDAO.getAllPayments();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.csv"))) {
                writer.write("ID,Student ID,Amount,Date,Status\n");
                for (Payment payment : payments) {
                    writer.write(payment.getId() + "," + payment.getStudentId() + "," + payment.getAmount() + "," + payment.getDate() + "," + payment.getStatus() + "\n");
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Payments data exported to payments.csv");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export payments data to CSV.");
        }
    }

    @FXML
    private void exportComplaintsCSV() {
        try {
            List<Complaint> complaints = complaintDAO.getAllComplaints();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("complaints.csv"))) {
                writer.write("ID,Student ID,Complaint,Date,Status\n");
                for (Complaint complaint : complaints) {
                    writer.write(complaint.getId() + "," + complaint.getStudentId() + "," + complaint.getComplaintText() + "," + complaint.getDate() + "," + complaint.getStatus() + "\n");
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Complaints data exported to complaints.csv");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Failed to export complaints data to CSV.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() throws IOException {
        Stage stage = (Stage) exportStudentsButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
        stage.setScene(new Scene(root));
    }
}
