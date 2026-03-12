package Transportation.DAL.DAO;

import Transportation.DAL.DTO.TruckDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TruckDAO {
    private final Connection conn;

    public TruckDAO() {
        this.conn = AppDatabaseManager.getConnection();
    }

    public void insert(TruckDTO truck) throws SQLException {
        String sql = "INSERT INTO Trucks (licensePlate, netWeight, maxWeight, requiredLicense) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, truck.getLicensePlate());
            stmt.setDouble(2, truck.getNetWeight());
            stmt.setDouble(3, truck.getMaxWeight());
            stmt.setInt(4, truck.getRequiredLicense());
            stmt.executeUpdate();
        }
    }

    public void delete(String licensePlate) throws SQLException {
        String sql = "DELETE FROM Trucks WHERE licensePlate = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            stmt.executeUpdate();
        }
    }

    public TruckDTO get(String licensePlate) throws SQLException {
        String sql = "SELECT * FROM Trucks WHERE licensePlate = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TruckDTO(
                            rs.getString("licensePlate"),
                            rs.getDouble("netWeight"),
                            rs.getDouble("maxWeight"),
                            rs.getInt("requiredLicense")
                    );
                } else {
                    throw new NoSuchElementException("Truck not found: " + licensePlate);
                }
            }
        }
    }

    public List<TruckDTO> getAll() throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT * FROM Trucks";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                trucks.add(new TruckDTO(
                        rs.getString("licensePlate"),
                        rs.getDouble("netWeight"),
                        rs.getDouble("maxWeight"),
                        rs.getInt("requiredLicense")
                ));
            }
        }
        return trucks;
    }

    public void update(TruckDTO truck) throws SQLException {
        String sql = "UPDATE Trucks SET netWeight = ?, maxWeight = ?, requiredLicense = ? WHERE licensePlate = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, truck.getNetWeight());
            stmt.setDouble(2, truck.getMaxWeight());
            stmt.setInt(3, truck.getRequiredLicense());
            stmt.setString(4, truck.getLicensePlate());
            stmt.executeUpdate();
        }
    }


    public boolean exists(String licensePlate) throws SQLException {
        String sql = "SELECT 1 FROM Trucks WHERE licensePlate = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
