package hrmanagement.dal.dao;

import hrmanagement.dal.dto.EmployeeAvailabilityDTO;
import Utils.AppDatabaseManager;
import hrmanagement.domain.enums.ShiftType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeAvailabilityDAO {



    public EmployeeAvailabilityDTO getAvailability(int employeeId, LocalDate date, ShiftType shiftType) throws SQLException {
        String sql = "SELECT * FROM employee_availability WHERE employee_id = ? AND date = ? AND shift_type = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            ps.setString(2, date.toString());
            ps.setString(3, shiftType.name());
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public void insert(EmployeeAvailabilityDTO dto) throws SQLException {
        String sql = """
            INSERT INTO employee_availability (employee_id, date, shift_type)
            VALUES (?, ?, ?);
        """;

        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getEmployeeId());
            ps.setString(2, dto.getDate().toString());
            ps.setString(3, dto.getShiftType().name());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public void delete(EmployeeAvailabilityDTO dto) throws SQLException {
        String sql = """
            DELETE FROM employee_availability
            WHERE employee_id = ? AND date = ? AND shift_type = ?;
        """;

        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getEmployeeId());
            ps.setString(2, dto.getDate().toString());
            ps.setString(3, dto.getShiftType().name());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public List<EmployeeAvailabilityDTO> findByEmployee(int employeeId) throws SQLException {
        String sql = "SELECT * FROM employee_availability WHERE employee_id = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EmployeeAvailabilityDTO> list = new ArrayList<>();
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
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

    public List<EmployeeAvailabilityDTO> findBySlot(LocalDate date, ShiftType shiftType) throws SQLException {
        String sql = "SELECT * FROM employee_availability WHERE date = ? AND shift_type = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EmployeeAvailabilityDTO> list = new ArrayList<>();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, date.toString());
            ps.setString(2, shiftType.name());
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

    public List<EmployeeAvailabilityDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM employee_availability;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EmployeeAvailabilityDTO> list = new ArrayList<>();
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

    private EmployeeAvailabilityDTO mapRow(ResultSet rs) throws SQLException {
        return new EmployeeAvailabilityDTO(
                rs.getInt("employee_id"),
                LocalDate.parse(rs.getString("date")),
                ShiftType.valueOf(rs.getString("shift_type"))
        );
    }
}
