package hrmanagement.dal.dao;

import hrmanagement.dal.dto.RoleDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public int insert(RoleDTO role) throws SQLException {
        String sql = "INSERT INTO role (name) VALUES (?);";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        Statement idStmt = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, role.getName());
            ps.executeUpdate();

            idStmt = conn.createStatement();
            rs = idStmt.executeQuery("SELECT last_insert_rowid();");
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating role failed, no ID obtained.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (rs != null) rs.close();
            if (idStmt != null) idStmt.close();
            if (ps != null) ps.close();
        }
    }


    public RoleDTO findById(int id) throws SQLException {
        String sql = "SELECT * FROM role WHERE id = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
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

    public RoleDTO findByName(String name) throws SQLException {
        String sql = "SELECT * FROM role WHERE name = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
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

    public List<RoleDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM role;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            List<RoleDTO> roles = new ArrayList<>();
            while (rs.next()) {
                roles.add(mapRow(rs));
            }
            return roles;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public void update(RoleDTO role) throws SQLException {
        String sql = "UPDATE role SET name = ? WHERE id = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, role.getName());
            ps.setInt(2, role.getId());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM role WHERE id = ?;";
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

    private RoleDTO mapRow(ResultSet rs) throws SQLException {
        return new RoleDTO(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
