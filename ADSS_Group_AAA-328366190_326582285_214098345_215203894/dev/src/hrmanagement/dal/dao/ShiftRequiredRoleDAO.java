package hrmanagement.dal.dao;

import hrmanagement.dal.dto.ShiftRequiredRoleDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the shift_required_role join table.
 */
public class ShiftRequiredRoleDAO {

    /**
     * Inserts a required role for a shift.
     * @param dto an instance containing shiftId and roleId
     * @throws SQLException on failure
     */
    public void insert(ShiftRequiredRoleDTO dto) throws SQLException {
        String sql = """
            INSERT INTO shift_required_role (shift_id, role_id)
            VALUES (?, ?);
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getShiftId());
            ps.setInt(2, dto.getRoleId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Deletes a required role for a shift.
     * @param dto identifies the record to delete
     * @throws SQLException on failure
     */
    public void delete(ShiftRequiredRoleDTO dto) throws SQLException {
        String sql = """
            DELETE FROM shift_required_role
             WHERE shift_id = ? AND role_id = ?;
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getShiftId());
            ps.setInt(2, dto.getRoleId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Finds all required roles for a given shift.
     * @param shiftId the shift’s ID
     * @return list of ShiftRequiredRoleDTO
     * @throws SQLException on failure
     */
    public List<ShiftRequiredRoleDTO> findByShift(int shiftId) throws SQLException {
        String sql = "SELECT * FROM shift_required_role WHERE shift_id = ?;";
        List<ShiftRequiredRoleDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, shiftId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ShiftRequiredRoleDTO(
                        rs.getInt("shift_id"),
                        rs.getInt("role_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * Finds all shifts that require a given role.
     * @param roleId the role’s ID
     * @return list of ShiftRequiredRoleDTO
     * @throws SQLException on failure
     */
    public List<ShiftRequiredRoleDTO> findByRole(int roleId) throws SQLException {
        String sql = "SELECT * FROM shift_required_role WHERE role_id = ?;";
        List<ShiftRequiredRoleDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ShiftRequiredRoleDTO(
                        rs.getInt("shift_id"),
                        rs.getInt("role_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * Retrieves all shift-to-role requirements.
     * @return list of all ShiftRequiredRoleDTO
     * @throws SQLException on failure
     */
    public List<ShiftRequiredRoleDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM shift_required_role;";
        List<ShiftRequiredRoleDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ShiftRequiredRoleDTO(
                        rs.getInt("shift_id"),
                        rs.getInt("role_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }
}
