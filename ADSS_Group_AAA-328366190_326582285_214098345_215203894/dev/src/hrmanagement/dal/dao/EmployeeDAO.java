package hrmanagement.dal.dao;

import hrmanagement.dal.dto.EmployeeDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public void insert(EmployeeDTO emp) throws SQLException {
        String sql = """
            INSERT INTO employee
              (id, name, bank_account, salary, employment_terms, start_date, is_active, site_address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """;

        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, emp.getId());
            ps.setString(2, emp.getName());
            ps.setString(3, emp.getBankAccount());
            ps.setDouble(4, emp.getSalary());
            ps.setString(5, emp.getEmploymentTerms());
            ps.setString(6, emp.getStartDate().toString());
            ps.setBoolean(7, emp.isActive());
            ps.setString(8, emp.getBranchAddress());
            ps.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (ps != null) ps.close();
        }
    }

    public EmployeeDTO findById(int id) throws SQLException {
        String sql = "SELECT * FROM employee WHERE id = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) return null;
            return mapRow(rs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public List<EmployeeDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM employee;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<EmployeeDTO> list = new ArrayList<>();

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

    public void update(EmployeeDTO emp) throws SQLException {
        String sql = """
            UPDATE employee SET
              name = ?,
              bank_account = ?,
              salary = ?,
              employment_terms = ?,
              start_date = ?,
              is_active = ?,
              site_address = ?
            WHERE id = ?;
        """;

        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getBankAccount());
            ps.setDouble(3, emp.getSalary());
            ps.setString(4, emp.getEmploymentTerms());
            ps.setString(5, emp.getStartDate().toString());
            ps.setBoolean(6, emp.isActive());
            ps.setString(7, emp.getBranchAddress());
            ps.setInt(8, emp.getId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM employee WHERE id = ?;";
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

    private EmployeeDTO mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String bankAccount = rs.getString("bank_account");
        double salary = rs.getDouble("salary");
        String employmentTerms = rs.getString("employment_terms");
        String startDateStr = rs.getString("start_date");
        LocalDate startDate = LocalDate.parse(startDateStr);
        boolean isActive = rs.getBoolean("is_active");
        String siteAddress = rs.getString("site_address");

        return new EmployeeDTO(id, name, bankAccount, salary, employmentTerms, startDate, isActive, siteAddress);
    }

}
