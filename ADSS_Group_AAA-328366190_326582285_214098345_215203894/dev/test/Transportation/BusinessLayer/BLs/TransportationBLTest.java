package Transportation.BusinessLayer.BLs;

import Transportation.BusinessLayer.Resources.LicenseType;
import Transportation.BusinessLayer.Resources.TransportationStatus;
import Transportation.BusinessLayer.Resources.TransportationZone;
import Transportation.DAL.DAO.TruckDAO;
import Transportation.DAL.DAO.TruckTimeIntervalsDAO;
import Transportation.DAL.DTO.TransportationDTO;
import Transportation.DAL.DTO.TruckDTO;
import Utils.AppDatabaseManager;
import hrmanagement.dal.dto.SiteDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransportationBLTest {

    private SiteDTO siteA;
    private SiteDTO siteB;
    private LocalDateTime now;
    private LocalDateTime later;
    private TransportationItemBL itemLight;
    private TransportationItemBL itemHeavy;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = AppDatabaseManager.getConnection();
        try (Statement stmt = conn.createStatement()) {
            // Disable foreign-key enforcement
            stmt.execute("PRAGMA foreign_keys = OFF;");

            // Clear out any existing intervals and trucks
            stmt.executeUpdate("DELETE FROM Truck_TimeIntervals;");
            stmt.executeUpdate("DELETE FROM Trucks;");

            // Clear out any existing transportations and their stops/items
            stmt.executeUpdate("DELETE FROM Transportation_Stops;");
            stmt.executeUpdate("DELETE FROM Item_At_Stop;");
            stmt.executeUpdate("DELETE FROM Transportations;");
        }

        // Two sites in the same zone
        siteA = new SiteDTO("SiteA", "ADDR_A", "ContactA", "050-0000", TransportationZone.CENTER);
        siteB = new SiteDTO("SiteB", "ADDR_B", "ContactB", "050-0001", TransportationZone.CENTER);

        now = LocalDateTime.of(2025, 6, 10, 8, 0);
        later = now.plusHours(4);

        itemLight = new TransportationItemBL("LightBox", 100.0);
        itemHeavy = new TransportationItemBL("HeavyCrate", 2000.0);
    }

    @Test
    void testConstructorThrowsIfDriverLacksLicense() {
        // Driver with no licenses
        DriverBL driver = new DriverBL(1, Collections.emptyList());

        // Truck requiring LicenseType.B, but driver has none.
        TruckBL truck = new TruckBL(
                "PLATE1",
                5000.0,
                10000.0,
                LicenseType.B,
                new TruckDAO(),
                new TruckTimeIntervalsDAO()
        );

        List<TransportationItemBL> items = List.of(itemLight);
        assertThrows(IllegalArgumentException.class, () ->
                        new TransportationBL(100, driver, truck, items, siteA, siteB, now, later),
                "Expected exception when driver lacks required license"
        );
    }

    @Test
    void testConstructorThrowsIfOverweight() {
        // Driver with correct license (LicenseType.C)
        DriverBL driver = new DriverBL(2, List.of(LicenseType.C));

        // Truck capacity only 1500, but itemHeavy weighs 2000 → overweight
        TruckBL truck = new TruckBL(
                "PLATE2",
                1000.0,
                1500.0,
                LicenseType.C,
                new TruckDAO(),
                new TruckTimeIntervalsDAO()
        );

        List<TransportationItemBL> items = List.of(itemHeavy);
        assertThrows(IllegalStateException.class, () ->
                        new TransportationBL(101, driver, truck, items, siteA, siteB, now, later),
                "Expected exception when total weight exceeds truck capacity"
        );
    }

    @Test
    void testValidConstructionCreatesStopsAndDTO() {
        // Driver and Truck properly matched (LicenseType.D)
        DriverBL driver = new DriverBL(3, List.of(LicenseType.D));
        TruckBL truck = new TruckBL(
                "PLATE3",
                2000.0,
                10000.0,
                LicenseType.D,
                new TruckDAO(),
                new TruckTimeIntervalsDAO()
        );

        List<TransportationItemBL> items = List.of(itemLight, new TransportationItemBL("Box2", 200.0));
        TransportationBL trans = new TransportationBL(102, driver, truck, items, siteA, siteB, now, later);

        // Two stops should have been created: source and destination
        List<StopBL> stops = trans.getStops();
        assertEquals(2, stops.size(), "Exactly two stops should exist");

        // Check DTO fields
        TransportationDTO tDto = trans.getDto();
        assertEquals(102, tDto.getId());
        assertEquals(Integer.valueOf(3), tDto.getDriverId());
        assertEquals("PLATE3", tDto.getTruckLicensePlate());
        assertEquals("ADDR_A", tDto.getSourceAddress());
        assertEquals("ADDR_B", tDto.getDestinationAddress());
        assertEquals(now, tDto.getSourceTime());
        assertEquals(later, tDto.getDestinationTime());

        // Status must be REGISTERED
        assertEquals(TransportationStatus.REGISTERED, trans.getStatus());
    }

    @Test
    void testCanAssignBetweenRespectsScheduleAndWeight() {
        // Driver and Truck properly matched (LicenseType.E)
        DriverBL driver = new DriverBL(4, List.of(LicenseType.E));
        TruckBL truck = new TruckBL(
                "PLATE4",
                1000.0,
                3000.0,
                LicenseType.E,
                new TruckDAO(),
                new TruckTimeIntervalsDAO()
        );

        // Create an initial transportation to book one interval
        TransportationBL trans = new TransportationBL(103, driver, truck, List.of(itemLight), siteA, siteB, now, later);

        // Try to assign another order overlapping the same interval → should be false
        LocalDateTime overlapStart = now.plusHours(2);
        LocalDateTime overlapEnd = later.plusHours(2);
        boolean canAssignOverlap = trans.canAssignBetween(siteA, overlapStart, siteB, overlapEnd, List.of(itemLight));
        assertFalse(canAssignOverlap, "Truck should not be available during overlapping interval");

        // Try a non‑overlapping interval but overweight
        LocalDateTime nonOverlapStart = later.plusHours(1);
        LocalDateTime nonOverlapEnd = later.plusHours(5);
        TransportationItemBL heavyItem = new TransportationItemBL("HugeLoad", 5000.0);
        boolean canAssignOverweight = trans.canAssignBetween(siteA, nonOverlapStart, siteB, nonOverlapEnd, List.of(heavyItem));
        assertFalse(canAssignOverweight, "Cannot assign if added weight exceeds maxWeight");

        // Try a non‑overlapping, light load → should be true
        boolean canAssignValid = trans.canAssignBetween(siteA, nonOverlapStart, siteB, nonOverlapEnd, List.of(itemLight));
        assertTrue(canAssignValid, "Truck is free in that interval and weight is within limits");
    }

    @Test
    void testPendingStatusWhenNoDriverOrTruck() {
        List<TransportationItemBL> items = List.of(itemLight);

        TransportationBL trans = new TransportationBL(118, null, null, items, siteA, siteB, now, later);

        assertEquals(TransportationStatus.PENDING, trans.getStatus());
        assertEquals("ADDR_A", trans.getDto().getSourceAddress());
        assertEquals("ADDR_B", trans.getDto().getDestinationAddress());
    }



    @Test
    void testSetCommentUpdatesDTO() throws Exception {
        DriverBL driver = new DriverBL(7, List.of(LicenseType.C));
        TruckBL truck = new TruckBL("PLATE7", 1000, 4000, LicenseType.C, new TruckDAO(), new TruckTimeIntervalsDAO());
        TransportationBL trans = new TransportationBL(106, driver, truck, List.of(itemLight), siteA, siteB, now, later);

        trans.updateComment("Test comment set.");
        assertEquals("Test comment set.", trans.getDto().getComment());
    }

    @Test
    void testConstructorAssignsPendingStatusIfTruckMissing() {
        DriverBL driver = new DriverBL(16, List.of(LicenseType.B));
        List<TransportationItemBL> items = List.of(itemLight);

        TransportationBL trans = new TransportationBL(114, driver, null, items, siteA, siteB, now, later);
        assertEquals(TransportationStatus.PENDING, trans.getStatus());
    }


    @Test
    void testGetSourceAndDestinationPending() {
        DriverBL driver = new DriverBL(17, List.of(LicenseType.D));
        TruckBL truck = new TruckBL("PLATE15", 500, 4500, LicenseType.D, new TruckDAO(), new TruckTimeIntervalsDAO());

        TransportationBL trans = new TransportationBL(115, driver, truck, List.of(itemLight), siteA, siteB, now, later);

        assertEquals("ADDR_A", trans.getSourcePending().getAddress());
        assertEquals("ADDR_B", trans.getDestPending().getAddress());
    }

    @Test
    void testSetTruckUpdatesDtoLicensePlate() {
        DriverBL driver = new DriverBL(13, List.of(LicenseType.B));
        TruckBL truck1 = new TruckBL("PLATE13A", 800, 4000, LicenseType.B, new TruckDAO(), new TruckTimeIntervalsDAO());
        TruckBL truck2 = new TruckBL("PLATE13B", 900, 5000, LicenseType.B, new TruckDAO(), new TruckTimeIntervalsDAO());

        TransportationBL trans = new TransportationBL(112, driver, truck1, List.of(itemLight), siteA, siteB, now, later);
        trans.setTruck(truck2);

        assertEquals("PLATE13B", trans.getDto().getTruckLicensePlate());
    }


    @Test
    void testSetDriverUpdatesDtoDriverId() {
        DriverBL driver1 = new DriverBL(14, List.of(LicenseType.C));
        DriverBL driver2 = new DriverBL(15, List.of(LicenseType.C));
        TruckBL truck = new TruckBL("PLATE14", 700, 3500, LicenseType.C, new TruckDAO(), new TruckTimeIntervalsDAO());

        TransportationBL trans = new TransportationBL(113, driver1, truck, List.of(itemLight), siteA, siteB, now, later);
        trans.setDriver(driver2);

        assertEquals(Integer.valueOf(15), trans.getDto().getDriverId());
    }


    @Test
    void testCanAcceptOrderRejectsTimeConflict() {
        DriverBL driver = new DriverBL(12, List.of(LicenseType.C));
        TruckBL truck = new TruckBL("PLATE12", 500, 3000, LicenseType.C, new TruckDAO(), new TruckTimeIntervalsDAO());
        TransportationBL trans = new TransportationBL(111, driver, truck, List.of(itemLight), siteA, siteB, now, later);

        boolean canAssign = trans.canAcceptOrder(List.of(itemLight), siteA, siteB, now.plusHours(1), later.minusHours(1));
        assertFalse(canAssign, "Order overlapping existing stop should be rejected");
    }

}