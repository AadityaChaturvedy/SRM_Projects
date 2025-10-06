package com.hostel.utils;

import com.hostel.database.RoomDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Room;
import com.hostel.models.Student;

import java.sql.SQLException;

public class CheckInOutService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    public boolean checkInStudent(int studentId, int roomId) throws SQLException {
        Student student = studentDAO.getStudentById(studentId);
        Room room = roomDAO.getRoomById(roomId);

        if (student != null && room != null) {
            // Basic check: if room has capacity and student is not already in a room
            // More advanced logic can be added here (e.g., check room status, gender, etc.)
            if (student.getRoomId() == 0 && room.getCapacity() > getOccupiedCount(roomId)) {
                student.setRoomId(roomId);
                studentDAO.updateStudent(student);
                // Optionally update room status if it becomes full
                return true;
            }
        }
        return false;
    }

    public boolean checkOutStudent(int studentId) throws SQLException {
        Student student = studentDAO.getStudentById(studentId);

        if (student != null && student.getRoomId() != 0) {
            student.setRoomId(0); // Set room_id to 0 to indicate no room
            studentDAO.updateStudent(student);
            // Optionally update room status if it becomes available
            return true;
        }
        return false;
    }

    private int getOccupiedCount(int roomId) throws SQLException {
        // This would ideally be a more complex query in StudentDAO
        int count = 0;
        for (Student student : studentDAO.getAllStudents()) {
            if (student.getRoomId() == roomId) {
                count++;
            }
        }
        return count;
    }
}
