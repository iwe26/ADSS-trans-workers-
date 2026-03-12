package Transportation.DAL.DAO;

import Transportation.DAL.DTO.TransportationStopDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransportationStopsDAO {
    private final Connection conn;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public TransportationStopsDAO() {
        this.conn = AppDatabaseManager.getConnection();
    }

    public void insertStop(int transportationId, String address, LocalDateTime arrivalTime) throws SQLException {
        String sql = "INSERT INTO Transportation_Stops (transportation_id, address, arrival_time) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            stmt.setString(2, address);
            stmt.setString(3, arrivalTime.format(formatter));
            stmt.executeUpdate();
        }
    }

    public boolean stopExists(int transportationId, String address, LocalDateTime arrivalTime) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Transportation_Stops WHERE transportation_id = ? AND address = ? AND arrival_time = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            stmt.setString(2, address);
            stmt.setString(3, arrivalTime.format(formatter));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    public void updateArrivalTime(int transportationId, String address, LocalDateTime arrivalTime) throws SQLException {
        String sql = "UPDATE Transportation_Stops SET arrival_time = ? WHERE transportation_id = ? AND address = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, arrivalTime.format(formatter));
            stmt.setInt(2, transportationId);
            stmt.setString(3, address);
            stmt.executeUpdate();
        }
    }

    public List<TransportationStopDTO> getStopsForTransportationWithArrivalTime(int transportationId) throws SQLException {
        List<TransportationStopDTO> stops = new ArrayList<>();
        String sql = "SELECT address, arrival_time FROM Transportation_Stops WHERE transportation_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String address = rs.getString("address");
                    LocalDateTime arrivalTime = LocalDateTime.parse(rs.getString("arrival_time"), formatter);
                    stops.add(new TransportationStopDTO(transportationId, address, arrivalTime));
                }
            }
        }
        return stops;
    }

    public void deleteStopsByTransportationId(int transportationId) throws SQLException {
        String sql = "DELETE FROM Transportation_Stops WHERE transportation_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            stmt.executeUpdate();
        }
    }


    public void deleteStop(int transportationId, String address) throws SQLException {
        String sql = "DELETE FROM Transportation_Stops WHERE transportation_id = ? AND address = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            stmt.setString(2, address);
            stmt.executeUpdate();
        }
    }

    public List<StopRecord> getStopsForTransportation(int transportationId) throws SQLException {
        List<StopRecord> stops = new ArrayList<>();
        String sql = "SELECT address, arrival_time FROM Transportation_Stops WHERE transportation_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String address = rs.getString("address");
                    LocalDateTime arrivalTime = LocalDateTime.parse(rs.getString("arrival_time"), formatter);
                    stops.add(new StopRecord(address, arrivalTime));
                }
            }
        }
        return stops;
    }

    public Map<Integer, List<StopRecord>> getAllStops() throws SQLException {
        Map<Integer, List<StopRecord>> result = new HashMap<>();
        String sql = "SELECT transportation_id, address, arrival_time FROM Transportation_Stops";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int tid = rs.getInt("transportation_id");
                String address = rs.getString("address");
                LocalDateTime arrivalTime = LocalDateTime.parse(rs.getString("arrival_time"), formatter);
                result.computeIfAbsent(tid, k -> new ArrayList<>()).add(new StopRecord(address, arrivalTime));
            }
        }
        return result;
    }

    // Helper record to return both address and time
    public static class StopRecord {
        public final String address;
        public final LocalDateTime arrivalTime;

        public StopRecord(String address, LocalDateTime arrivalTime) {
            this.address = address;
            this.arrivalTime = arrivalTime;
        }
    }
}
