package com.hostel.database;

import com.hostel.models.Room;
import com.hostel.utils.Observable;
import com.hostel.utils.Observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements Observable<List<Room>> {

    private List<Observer<List<Room>>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<List<Room>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<List<Room>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<Room> data) {
        for (Observer<List<Room>> observer : observers) {
            observer.update(data);
        }
    }

    public void createRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, capacity, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setString(3, room.getStatus());
            pstmt.executeUpdate();
            notifyObservers(getAllRooms());
        }
    }

    public Room getRoomById(int id) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                );
            }
        }
        return null;
    }

    public List<Room> getAllRooms() throws SQLException {
        String sql = "SELECT * FROM rooms";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                ));
            }
        }
        return rooms;
    }

    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, capacity = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setString(3, room.getStatus());
            pstmt.setInt(4, room.getId());
            pstmt.executeUpdate();
            notifyObservers(getAllRooms());
        }
    }

    public void deleteRoom(int id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            notifyObservers(getAllRooms());
        }
    }

    public java.util.Map<String, Integer> getRoomOccupancyStatus() throws SQLException {
        String sql = "SELECT status, COUNT(*) as count FROM rooms GROUP BY status";
        java.util.Map<String, Integer> occupancyData = new java.util.HashMap<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                occupancyData.put(rs.getString("status"), rs.getInt("count"));
            }
        }
        return occupancyData;
    }

    public void populateMockData() throws SQLException {
        if (getAllRooms().isEmpty()) {
            for (int i = 1; i <= 20; i++) {
                String roomNumber = String.format("1%02d", i);
                int capacity = (i % 3) + 1; // 1, 2, or 3
                String status = (i % 2 == 0) ? "Available" : "Occupied";
                createRoom(new Room(0, roomNumber, capacity, status));
            }
        }
    }
}
