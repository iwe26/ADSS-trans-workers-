package hrmanagement.dal.dao;

import Transportation.BusinessLayer.Resources.TransportationZone;
import hrmanagement.dal.dto.SiteDTO;
import Utils.AppDatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for branch/transport site records, now with zone.
 */
public class SiteDAO {

    /**
     * Inserts a new site record.
     */
    public void insert(SiteDTO site) throws SQLException {
        String sql = """
            INSERT INTO site (address,name, contact_name, phone, zone)
            VALUES (?, ?, ?, ?, ?);
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, site.getAddress());
            ps.setString(2, site.getName());
            ps.setString(3, site.getContactName());
            ps.setString(4, site.getPhone());
            ps.setString(5, site.getZone().name());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Retrieves a site by its name.
     */
    public SiteDTO findByAddress(String address) throws SQLException {
        String sql = "SELECT * FROM site WHERE address = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, address);
            rs = ps.executeQuery();
            if (!rs.next()) return null;
            return mapRow(rs);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * Retrieves all sites.
     */
    public List<SiteDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM site;";
        List<SiteDTO> list = new ArrayList<>();
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
     * Updates an existing site’s details.
     */
    public void update(SiteDTO site) throws SQLException {
        String sql = """
            UPDATE site
               SET name      = ?,
                   contact_name = ?,
                   phone        = ?,
                   zone         = ?
             WHERE address = ?;
        """;
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, site.getName());
            ps.setString(2, site.getContactName());
            ps.setString(3, site.getPhone());
            ps.setString(4, site.getZone().name());
            ps.setString(5, site.getAddress());
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Deletes a site by name.
     */
    public void delete(String address) throws SQLException {
        String sql = "DELETE FROM site WHERE address = ?;";
        Connection conn = AppDatabaseManager.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, address);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
        }
    }

    /** Maps a ResultSet row to a SiteDTO, including zone. */
    private SiteDTO mapRow(ResultSet rs) throws SQLException {
        return new SiteDTO(
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("contact_name"),
                rs.getString("phone"),
                TransportationZone.valueOf(rs.getString("zone"))
        );
    }
}
