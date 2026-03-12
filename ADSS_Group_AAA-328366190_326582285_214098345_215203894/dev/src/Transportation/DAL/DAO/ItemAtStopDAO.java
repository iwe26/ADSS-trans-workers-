package Transportation.DAL.DAO;

import Transportation.DAL.DTO.ItemAtStopDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.*;

public class ItemAtStopDAO {
    private final Connection conn;

    public ItemAtStopDAO() {
        this.conn = AppDatabaseManager.getConnection();
    }

    public void insert(ItemAtStopDTO item) throws SQLException {
        String sql = "INSERT INTO Item_At_Stop (transportation_id, stop_address, item_name, pickup, item_weight) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getTransportationId());
            stmt.setString(2, item.getStopAddress());
            stmt.setString(3, item.getItemName());
            stmt.setBoolean(4, item.isPickup());
            stmt.setDouble(5, item.getItemWeight());
            stmt.executeUpdate();
        }
    }

    public void delete(int transportationId, String stopAddress, String itemName, boolean pickup) throws SQLException {
        String sql = "DELETE FROM Item_At_Stop WHERE transportation_id = ? AND stop_address = ? AND item_name = ? AND pickup = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            stmt.setString(2, stopAddress);
            stmt.setString(3, itemName);
            stmt.setBoolean(4, pickup);
            stmt.executeUpdate();
        }
    }

    public List<ItemAtStopDTO> getItems(int transportationId, String stopAddress) throws SQLException {
        List<ItemAtStopDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM Item_At_Stop WHERE transportation_id = ? AND stop_address = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            stmt.setString(2, stopAddress);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new ItemAtStopDTO(
                            rs.getInt("transportation_id"),
                            rs.getString("stop_address"),
                            rs.getString("item_name"),
                            rs.getDouble("item_weight"),
                            rs.getBoolean("pickup")
                    ));
                }
            }
        }
        return items;
    }

    public List<ItemAtStopDTO> getItemsForTransportation(int transportationId) throws SQLException {
        List<ItemAtStopDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM Item_At_Stop WHERE transportation_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new ItemAtStopDTO(
                            rs.getInt("transportation_id"),
                            rs.getString("stop_address"),
                            rs.getString("item_name"),
                            rs.getDouble("item_weight"),
                            rs.getBoolean("pickup")
                    ));
                }
            }
        }
        return items;
    }

    public Map<Integer, Map<String, List<ItemAtStopDTO>>> getAllItems() throws SQLException {
        Map<Integer, Map<String, List<ItemAtStopDTO>>> result = new HashMap<>();
        String sql = "SELECT * FROM Item_At_Stop";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int tId = rs.getInt("transportation_id");
                String stop = rs.getString("stop_address");

                ItemAtStopDTO item = new ItemAtStopDTO(
                        tId,
                        stop,
                        rs.getString("item_name"),
                        rs.getDouble("item_weight"),
                        rs.getBoolean("pickup")
                );

                result.computeIfAbsent(tId, k -> new HashMap<>())
                        .computeIfAbsent(stop, s -> new ArrayList<>())
                        .add(item);
            }
        }
        return result;
    }
}
