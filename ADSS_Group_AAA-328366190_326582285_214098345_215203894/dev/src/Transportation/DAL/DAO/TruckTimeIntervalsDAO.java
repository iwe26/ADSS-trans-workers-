package Transportation.DAL.DAO;

import Transportation.DAL.DTO.TruckTimeIntervalDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TruckTimeIntervalsDAO {
    private final Connection conn;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public TruckTimeIntervalsDAO() {
        this.conn = AppDatabaseManager.getConnection();
    }

    public void insert(String licensePlate, LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "INSERT INTO Truck_TimeIntervals (truck_license, datetime1, datetime2) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            stmt.setString(2, start.format(formatter));
            stmt.setString(3, end.format(formatter));
            stmt.executeUpdate();
        }
    }

    public void delete(String licensePlate, LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "DELETE FROM Truck_TimeIntervals WHERE truck_license = ? AND datetime1 = ? AND datetime2 = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            stmt.setString(2, start.format(formatter));
            stmt.setString(3, end.format(formatter));
            stmt.executeUpdate();
        }
    }

    public List<LocalDateTime[]> getIntervals(String licensePlate) throws SQLException {
        List<LocalDateTime[]> intervals = new ArrayList<>();
        String sql = "SELECT datetime1, datetime2 FROM Truck_TimeIntervals WHERE truck_license = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime start = LocalDateTime.parse(rs.getString("datetime1"), formatter);
                    LocalDateTime end = LocalDateTime.parse(rs.getString("datetime2"), formatter);
                    intervals.add(new LocalDateTime[]{start, end});
                }
            }
        }
        return intervals;
    }

    public List<TruckTimeIntervalDTO> getAll() throws SQLException {
        List<TruckTimeIntervalDTO> intervals = new ArrayList<>();
        String sql = "SELECT truck_license, datetime1, datetime2 FROM Truck_TimeIntervals";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                intervals.add(new TruckTimeIntervalDTO(
                        rs.getString("truck_license"),
                        LocalDateTime.parse(rs.getString("datetime1"), formatter),
                        LocalDateTime.parse(rs.getString("datetime2"), formatter)
                ));
            }
        }
        return intervals;
    }
}
