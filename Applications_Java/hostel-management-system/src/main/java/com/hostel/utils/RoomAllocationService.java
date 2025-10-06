package com.hostel.utils;

import com.hostel.database.RoomDAO;
import com.hostel.database.StudentDAO;
import com.hostel.models.Room;
import com.hostel.models.Student;

import java.sql.SQLException;

public class RoomAllocationService {

    private final RoomDAO roomDAO = new RoomDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public boolean allocateRoom(Student student, Room room) throws SQLException {
        // Check if the student is already allocated to this room
        if (student.getRoomId() == room.getId()) {
            return false; // Student already in this room
        }

        // Check if the room is available and has capacity
        if (!room.getStatus().equalsIgnoreCase("Available") && !room.getStatus().equalsIgnoreCase("Partially Occupied")) {
            return false; // Room not available for allocation
        }

        int occupiedCount = studentDAO.getOccupiedCountForRoom(room.getId());
        if (occupiedCount < room.getCapacity()) {
            // Deallocate from previous room if any
            if (student.getRoomId() != 0) {
                Room oldRoom = roomDAO.getRoomById(student.getRoomId());
                if (oldRoom != null) {
                    updateRoomStatus(oldRoom);
                }
            }

            student.setRoomId(room.getId());
            studentDAO.updateStudent(student);
            updateRoomStatus(room);
            return true;
        }
        return false;
    }

    public boolean deallocateRoom(Student student) throws SQLException {
        if (student.getRoomId() != 0) {
            int oldRoomId = student.getRoomId();
            student.setRoomId(0); // Assuming 0 means no room
            studentDAO.updateStudent(student);

            Room oldRoom = roomDAO.getRoomById(oldRoomId);
            if (oldRoom != null) {
                updateRoomStatus(oldRoom);
            }
            return true;
        }
        return false;
    }

    private void updateRoomStatus(Room room) throws SQLException {
        int occupiedCount = studentDAO.getOccupiedCountForRoom(room.getId());
        String newStatus;
        if (occupiedCount == 0) {
            newStatus = "Available";
        } else if (occupiedCount < room.getCapacity()) {
            newStatus = "Partially Occupied";
        } else {
            newStatus = "Occupied";
        }
        room.setStatus(newStatus);
        roomDAO.updateRoom(room);
    }
}
