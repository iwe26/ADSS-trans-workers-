package hrmanagement.dal.dao;

import hrmanagement.dal.dto.EmployeeRoleDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRoleDAO {

    public void insert(EmployeeRoleDTO er) throws SQLException {
        String sql = """
            INSERT INTO employee_role (employee_id, role_id)
            VALUES (?, ?);
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, er.getEmployeeId());
            ps.setInt(2, er.getRoleId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public void delete(EmployeeRoleDTO er) throws SQLException {
        String sql = """
            DELETE FROM employee_role
            WHERE employee_id = ? AND role_id = ?;
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, er.getEmployeeId());
            ps.setInt(2, er.getRoleId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public List<EmployeeRoleDTO> findByEmployee(int employeeId) throws SQLException {
        String sql = "SELECT * FROM employee_role WHERE employee_id = ?;";
        List<EmployeeRoleDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employeeId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new EmployeeRoleDTO(
                        rs.getInt("employee_id"),
                        rs.getInt("role_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public List<EmployeeRoleDTO> findByRole(int roleId) throws SQLException {
        String sql = "SELECT * FROM employee_role WHERE role_id = ?;";
        List<EmployeeRoleDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new EmployeeRoleDTO(
                        rs.getInt("employee_id"),
                        rs.getInt("role_id")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public List<EmployeeRoleDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM employee_role;";
        List<EmployeeRoleDTO> list = new ArrayList<>();
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new EmployeeRoleDTO(
                        rs.getInt("employee_id"),
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
