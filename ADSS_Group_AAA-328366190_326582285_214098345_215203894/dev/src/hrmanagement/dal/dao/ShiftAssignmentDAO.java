package hrmanagement.dal.dao;

import hrmanagement.dal.dto.ShiftAssignmentDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftAssignmentDAO {

    public void insert(ShiftAssignmentDTO dto) throws SQLException {
        String sql = """
            INSERT INTO shift_assignment (shift_id, employee_id)
            VALUES (?, ?);
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getShiftId());
            ps.setInt(2, dto.getEmployeeId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public void delete(ShiftAssignmentDTO dto) throws SQLException {
        String sql = """
            DELETE FROM shift_assignment
            WHERE shift_id = ? AND employee_id = ?;
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getShiftId());
            ps.setInt(2, dto.getEmployeeId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public List<ShiftAssignmentDTO> findByShift(int shiftId) throws SQLException {
        String sql = "SELECT * FROM shift_assignment WHERE shift_id = ?;";
        List<ShiftAssignmentDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, shiftId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ShiftAssignmentDTO(
                        rs.getInt("shift_id"),
                        rs.getInt("employee_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public List<ShiftAssignmentDTO> findByEmployee(int employeeId) throws SQLException {
        String sql = "SELECT * FROM shift_assignment WHERE employee_id = ?;";
        List<ShiftAssignmentDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ShiftAssignmentDTO(
                        rs.getInt("shift_id"),
                        rs.getInt("employee_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public List<ShiftAssignmentDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM shift_assignment;";
        List<ShiftAssignmentDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ShiftAssignmentDTO(
                        rs.getInt("shift_id"),
                        rs.getInt("employee_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }
}
