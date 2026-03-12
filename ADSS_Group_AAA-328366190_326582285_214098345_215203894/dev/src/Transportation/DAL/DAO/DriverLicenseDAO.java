package Transportation.DAL.DAO;

import Transportation.DAL.DTO.DriverLicenseDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverLicenseDAO {
    private final Connection conn;

    public DriverLicenseDAO() {
        this.conn = AppDatabaseManager.getConnection();
    }

    public void insert(DriverLicenseDTO dto) throws SQLException {
        String sql = "INSERT INTO Driver_License (employee_id, license) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getEmployeeId());
            stmt.setInt(2, dto.getLicense());
            stmt.executeUpdate();
        }
    }

    public void delete(DriverLicenseDTO dto) throws SQLException {
        String sql = "DELETE FROM Driver_License WHERE employee_id = ? AND license = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getEmployeeId());
            stmt.setInt(2, dto.getLicense());
            stmt.executeUpdate();
        }
    }

    public boolean exists(DriverLicenseDTO dto) throws SQLException {
        String sql = "SELECT 1 FROM Driver_License WHERE employee_id = ? AND license = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getEmployeeId());
            stmt.setInt(2, dto.getLicense());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Integer> getLicensesForDriver(int employeeId) throws SQLException {
        List<Integer> licenses = new ArrayList<>();
        String sql = "SELECT license FROM Driver_License WHERE employee_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    licenses.add(rs.getInt("license"));
                }
            }
        }
        return licenses;
    }

    public List<Integer> getDriversForLicense(int license) throws SQLException {
        List<Integer> employeeIds = new ArrayList<>();
        String sql = "SELECT employee_id FROM Driver_License WHERE license = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, license);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employeeIds.add(rs.getInt("employee_id"));
                }
            }
        }
        return employeeIds;
    }

    public List<DriverLicenseDTO> getAllRecords() throws SQLException {
        List<DriverLicenseDTO> result = new ArrayList<>();
        String sql = "SELECT employee_id, license FROM Driver_License";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(new DriverLicenseDTO(rs.getInt("employee_id"), rs.getInt("license")));
            }
        }
        return result;
    }
}
