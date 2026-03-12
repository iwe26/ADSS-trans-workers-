package hrmanagement.domain.entities;

import hrmanagement.dal.dao.SiteDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import hrmanagement.dal.dto.SiteDTO;
import Transportation.BusinessLayer.Resources.TransportationZone;
import Utils.AppDatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SiteDAOTest {

    private SiteDAO siteDAO;

    /**
     * Before each test, clear the 'site' table completely.
     */
    @BeforeEach
    void clearSiteTable() throws SQLException {
        Connection conn = AppDatabaseManager.getConnection();
        try (Statement stmt = conn.createStatement()) {
            // 1) Disable FK checks so we can delete without cascade issues
            stmt.execute("PRAGMA foreign_keys = OFF;");
            // 2) Delete everything from 'site'
            stmt.executeUpdate("DELETE FROM site;");
            // 3) Re-enable FK checks
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        siteDAO = new SiteDAO();
    }

    @Test
    void testInsertAndFindByAddress() throws SQLException {
        // Create a new SiteDTO: address="A1", name="Warehouse A1", contact="Alice", phone="050-0001", zone=NORTH
        SiteDTO s = new SiteDTO(
                "A1",                          // name
                "Warehouse A1",                // address (PK)
                "Alice",                       // contact_name
                "050-0001",                    // phone
                TransportationZone.NORTH       // zone
        );

        // Insert into DB
        siteDAO.insert(s);

        // Now find it
        SiteDTO fetched = siteDAO.findByAddress("Warehouse A1");
        assertNotNull(fetched, "SiteDAO.findByAddress should return a SiteDTO after insert");
        assertEquals("Warehouse A1", fetched.getAddress(),      "Name must match");
        assertEquals("A1",            fetched.getName(),   "Address must match");
        assertEquals("Alice",         fetched.getContactName(), "Contact must match");
        assertEquals("050-0001",      fetched.getPhone(),      "Phone must match");
        assertEquals(TransportationZone.NORTH, fetched.getZone(), "Zone must match");

        // Finding a nonexistent address returns null
        assertNull(siteDAO.findByAddress("NON_EXISTENT"),
                "findByAddress of a missing key should return null");
    }

    @Test
    void testFindAllReturnsMultipleSites() throws SQLException {
        // Insert three distinct sites
        SiteDTO s1 = new SiteDTO( "Name1", "S1","Contact1", "050-1001", TransportationZone.CENTER);
        SiteDTO s2 = new SiteDTO("Name2", "S2", "Contact2", "050-1002", TransportationZone.SOUTH);
        SiteDTO s3 = new SiteDTO("Name3", "S3", "Contact3", "050-1003", TransportationZone.EAST);

        siteDAO.insert(s1);
        siteDAO.insert(s2);
        siteDAO.insert(s3);

        // Now findAll() should return at least these three (in any order)
        List<SiteDTO> allSites = siteDAO.findAll();
        assertNotNull(allSites);
        assertEquals(3, allSites.size(), "Exactly 3 sites should exist after insertion");

        // Verify that each inserted address appears
        boolean foundS1 = allSites.stream().anyMatch(site -> "S1".equals(site.getAddress())
                && "Name1".equals(site.getName()));
        boolean foundS2 = allSites.stream().anyMatch(site -> "S2".equals(site.getAddress())
                && "Name2".equals(site.getName()));
        boolean foundS3 = allSites.stream().anyMatch(site -> "S3".equals(site.getAddress())
                && "Name3".equals(site.getName()));
        assertTrue(foundS1 && foundS2 && foundS3, "All three inserted sites should be present in findAll()");
    }

    @Test
    void testUpdateModifiesExistingSite() throws SQLException {
        // Insert one site
        SiteDTO original = new SiteDTO("Branch One","B1", "Bob", "050-2001", TransportationZone.WEST);
        siteDAO.insert(original);

        // Verify initial values
        SiteDTO before = siteDAO.findByAddress("B1");
        assertNotNull(before);
        assertEquals("Branch One", before.getName());
        assertEquals("Bob", before.getContactName());
        assertEquals("050-2001", before.getPhone());
        assertEquals(TransportationZone.WEST, before.getZone());

        // Create a new DTO to update the same address “B1”:
        // Change name, contact, phone, zone; address (PK) remains "B1"
        SiteDTO updated = new SiteDTO("Branch 1 Updated", "B1", "Bobby", "050-2002", TransportationZone.SOUTH);
        siteDAO.update(updated);

        // Fetch again and verify changes
        SiteDTO after = siteDAO.findByAddress("B1");
        assertNotNull(after, "Site should still exist after update");
        assertEquals("Branch 1 Updated", after.getName(), "Name should have been updated");
        assertEquals("Bobby", after.getContactName(),       "ContactName should have been updated");
        assertEquals("050-2002", after.getPhone(),           "Phone should have been updated");
        assertEquals(TransportationZone.SOUTH, after.getZone(), "Zone should have been updated");
    }

    @Test
    void testDeleteRemovesSite() throws SQLException {
        // Insert one site
        SiteDTO s = new SiteDTO("Center Branch", "C1", "Carol", "050-3001", TransportationZone.CENTER);
        siteDAO.insert(s);

        // Confirm it exists
        assertNotNull(siteDAO.findByAddress("C1"));

        // Delete it
        siteDAO.delete("C1");

        // Now findByAddress must return null
        assertNull(siteDAO.findByAddress("C1"), "After delete, findByAddress should return null");
        // And findAll should be empty
        assertTrue(siteDAO.findAll().isEmpty(), "After delete, no sites should remain in the table");
    }

    @Test
    void testInsertDuplicateAddressThrowsException() {
        // Insert one site
        SiteDTO s = new SiteDTO("Duplicate Branch", "D1", "Dan", "050-4001", TransportationZone.NORTH);
        try {
            siteDAO.insert(s);
        } catch (SQLException e) {
            fail("First insert() should not throw");
        }

        // Inserting again with the same address "D1" should violate PRIMARY KEY
        SiteDTO duplicate = new SiteDTO("Another", "D1", "Dave", "050-4002", TransportationZone.EAST);
        assertThrows(SQLException.class, () -> siteDAO.insert(duplicate),
                "Inserting a SiteDTO with the same address should throw SQLException (PRIMARY KEY violation)");
    }

}