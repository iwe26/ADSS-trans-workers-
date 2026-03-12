package hrmanagement.dal.dao;

import hrmanagement.dal.dto.ShiftDTO;
import Utils.AppDatabaseManager;
import hrmanagement.domain.enums.ShiftType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftDAO {

    /**
     * Inserts a new Shift record.
     * @param shift the ShiftDTO to insert (id is ignored/autoincremented)
     * @return generated shift ID
     * @throws SQLException on failure
     */
    public int insert(ShiftDTO shift) throws SQLException {
        String sql = """
        INSERT INTO shift (date, shift_type, shift_manager, site_address)
        VALUES (?, ?, ?, ?);
    """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Insert the shift
            ps = conn.prepareStatement(sql);
            ps.setString(1, shift.getDate().toString());
            ps.setString(2, shift.getShiftType().name());
            ps.setInt(3, shift.getShiftManagerId());
            ps.setString(4, shift.getSiteAddress());
            ps.executeUpdate();

            // Retrieve the last inserted row ID
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT last_insert_rowid();");
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve inserted shift ID.");
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (ps != null) ps.close();
        }
    }


    /**
     * Finds a shift by its ID.
     * @param id the shift’s ID
     * @return the ShiftDTO or null if not found
     * @throws SQLException on failure
     */
    public ShiftDTO findById(int id) throws SQLException {
        String sql = "SELECT * FROM shift WHERE id = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) return null;
            return mapRow(rs);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * Retrieves all shifts.
     * @return list of ShiftDTO
     * @throws SQLException on failure
     */
    public List<ShiftDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM shift;";
        List<ShiftDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * Updates an existing shift’s manager.
     * @param shiftId the ShiftDTO with updated manager (other fields immutable)
     * @throws SQLException on failure
     */
    public void updateManager(int shiftId, int managerId) throws SQLException {
        String sql = "UPDATE shift SET shift_manager = ? WHERE id = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            ps.setInt(2, shiftId);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Deletes a shift by ID.
     * @param id the shift ID
     * @throws SQLException on failure
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM shift WHERE id = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Maps the current row of ResultSet to a ShiftDTO.
     */
    private ShiftDTO mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        LocalDate date = LocalDate.parse(rs.getString("date"));
        ShiftType type = ShiftType.valueOf(rs.getString("shift_type"));
        int manager = rs.getInt("shift_manager");
        String site_address    = rs.getString("site_address");

        return new ShiftDTO(id, date, type, manager, site_address);
    }
}
