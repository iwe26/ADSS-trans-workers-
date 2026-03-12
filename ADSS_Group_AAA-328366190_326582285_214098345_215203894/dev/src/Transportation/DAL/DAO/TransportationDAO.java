package Transportation.DAL.DAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import Transportation.DAL.DTO.TransportationDTO;
import Utils.AppDatabaseManager;

public class TransportationDAO {
    private final Connection conn;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public TransportationDAO() {
        this.conn = AppDatabaseManager.getConnection();
    }

    public void insert(TransportationDTO dto) throws SQLException {
        String sql = "INSERT INTO Transportations (id, driver_id, truck_licensePlate, source_address, destination_address, source_time, destination_time, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getId());

            if (dto.getDriverId() != null)
                stmt.setInt(2, dto.getDriverId());
            else
                stmt.setNull(2, Types.INTEGER);

            if (dto.getTruckLicensePlate() != null)
                stmt.setString(3, dto.getTruckLicensePlate());
            else
                stmt.setNull(3, Types.VARCHAR);

            stmt.setString(4, dto.getSourceAddress());
            stmt.setString(5, dto.getDestinationAddress());
            stmt.setString(6, dto.getSourceTime().format(formatter));
            stmt.setString(7, dto.getDestinationTime().format(formatter));
            stmt.setString(8, dto.getComment());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Transportations WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT 1 FROM Transportations WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updateComment(int id, String newComment) throws SQLException {
        String sql = "UPDATE Transportations SET comment = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newComment);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }



    public TransportationDTO get(int id) throws SQLException {
        String sql = "SELECT * FROM Transportations WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TransportationDTO(
                            rs.getInt("id"),
                            rs.getInt("driver_id"),
                            rs.getString("truck_licensePlate"),
                            rs.getString("source_address"),
                            rs.getString("destination_address"),
                            LocalDateTime.parse(rs.getString("source_time"), formatter),
                            LocalDateTime.parse(rs.getString("destination_time"), formatter),
                            rs.getString("comment")
                    );
                } else {
                    throw new NoSuchElementException("Transportation not found: " + id);
                }
            }
        }
    }

    public void insertOrUpdate(TransportationDTO dto) throws SQLException {
        String sql = """
        INSERT INTO Transportations (id, driver_id, truck_licensePlate, source_address, destination_address,
                                     source_time, destination_time, comment)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(id) DO UPDATE SET
            driver_id = excluded.driver_id,
            truck_licensePlate = excluded.truck_licensePlate,
            source_address = excluded.source_address,
            destination_address = excluded.destination_address,
            source_time = excluded.source_time,
            destination_time = excluded.destination_time,
            comment = excluded.comment
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getId());

            if (dto.getDriverId() != null)
                stmt.setInt(2, dto.getDriverId());
            else
                stmt.setNull(2, Types.INTEGER);

            if (dto.getTruckLicensePlate() != null)
                stmt.setString(3, dto.getTruckLicensePlate());
            else
                stmt.setNull(3, Types.VARCHAR);

            stmt.setString(4, dto.getSourceAddress());
            stmt.setString(5, dto.getDestinationAddress());
            stmt.setString(6, dto.getSourceTime().format(formatter));
            stmt.setString(7, dto.getDestinationTime().format(formatter));
            stmt.setString(8, dto.getComment());
            stmt.executeUpdate();
        }
    }


    public List<TransportationDTO> getAll() throws SQLException {
        List<TransportationDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Transportations";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new TransportationDTO(
                        rs.getInt("id"),
                        rs.getInt("driver_id"),
                        rs.getString("truck_licensePlate"),
                        rs.getString("source_address"),
                        rs.getString("destination_address"),
                        LocalDateTime.parse(rs.getString("source_time"), formatter),
                        LocalDateTime.parse(rs.getString("destination_time"), formatter),
                        rs.getString("comment")
                ));
            }
        }
        return list;
    }
}
