package com.hostel.database;

import com.hostel.models.ReportData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public ReportData getOccupancyReport() throws SQLException {
        ReportData report = new ReportData("Occupancy Report");
        String sqlTotalRooms = "SELECT COUNT(*) FROM rooms";
        String sqlOccupiedRooms = "SELECT COUNT(DISTINCT room_id) FROM students WHERE room_id IS NOT NULL";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotalRooms);
             PreparedStatement pstmtOccupied = conn.prepareStatement(sqlOccupiedRooms)) {

            ResultSet rsTotal = pstmtTotal.executeQuery();
            if (rsTotal.next()) {
                report.addData("Total Rooms", String.valueOf(rsTotal.getInt(1)));
            }

            ResultSet rsOccupied = pstmtOccupied.executeQuery();
            if (rsOccupied.next()) {
                report.addData("Occupied Rooms", String.valueOf(rsOccupied.getInt(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return report;
    }

    public ReportData getPaymentSummaryReport() throws SQLException {
        ReportData report = new ReportData("Payment Summary Report");
        String sqlTotalPayments = "SELECT SUM(amount) FROM payments";
        String sqlPendingPayments = "SELECT COUNT(*) FROM payments WHERE status = 'Pending'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotalPayments);
             PreparedStatement pstmtPending = conn.prepareStatement(sqlPendingPayments)) {

            ResultSet rsTotal = pstmtTotal.executeQuery();
            if (rsTotal.next()) {
                report.addData("Total Payments", String.valueOf(rsTotal.getInt(1)));
            }

            ResultSet rsPending = pstmtPending.executeQuery();
            if (rsPending.next()) {
                report.addData("Pending Payments", String.valueOf(rsPending.getInt(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return report;
    }

    public ReportData getAttendanceSummaryReport() throws SQLException {
        ReportData report = new ReportData("Attendance Summary Report");
        String sqlTotalStudents = "SELECT COUNT(*) FROM students";
        String sqlPresentStudents = "SELECT COUNT(DISTINCT student_id) FROM attendance WHERE date = CURRENT_DATE AND status = 'Present'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotalStudents);
             PreparedStatement pstmtPresent = conn.prepareStatement(sqlPresentStudents)) {

            ResultSet rsTotal = pstmtTotal.executeQuery();
            if (rsTotal.next()) {
                report.addData("Total Students", String.valueOf(rsTotal.getInt(1)));
            }

            ResultSet rsPresent = pstmtPresent.executeQuery();
            if (rsPresent.next()) {
                report.addData("Present Students Today", String.valueOf(rsPresent.getInt(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return report;
    }

    public ReportData getComplaintSummaryReport() throws SQLException {
        ReportData report = new ReportData("Complaint Summary Report");
        String sqlTotalComplaints = "SELECT COUNT(*) FROM complaints";
        String sqlPendingComplaints = "SELECT COUNT(*) FROM complaints WHERE status = 'Pending'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotalComplaints);
             PreparedStatement pstmtPending = conn.prepareStatement(sqlPendingComplaints)) {

            ResultSet rsTotal = pstmtTotal.executeQuery();
            if (rsTotal.next()) {
                report.addData("Total Complaints", String.valueOf(rsTotal.getInt(1)));
            }

            ResultSet rsPending = pstmtPending.executeQuery();
            if (rsPending.next()) {
                report.addData("Pending Complaints", String.valueOf(rsPending.getInt(1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return report;
    }

    public ReportData getMaintenanceTrendReport() throws SQLException {
        ReportData report = new ReportData("Maintenance Trend Report");
        String sql = "SELECT complaint, COUNT(*) as count FROM complaints GROUP BY complaint HAVING COUNT(*) > 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                report.addData(rs.getString("complaint"), String.valueOf(rs.getInt("count")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return report;
    }
}
