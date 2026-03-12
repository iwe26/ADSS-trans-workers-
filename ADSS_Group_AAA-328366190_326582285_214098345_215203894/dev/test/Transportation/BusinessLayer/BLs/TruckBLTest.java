package Transportation.BusinessLayer.BLs;

import Transportation.BusinessLayer.Resources.TransportationZone;
import Transportation.DAL.DAO.TruckDAO;
import Transportation.DAL.DAO.TruckTimeIntervalsDAO;
import Transportation.DAL.DTO.TruckDTO;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import Transportation.BusinessLayer.Resources.LicenseType;
import Transportation.DAL.DTO.TruckTimeIntervalDTO;
import hrmanagement.dal.dto.SiteDTO;
import java.time.LocalDateTime;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TruckBLTest {

    private static TruckDAO truckDAO;
    private static TruckTimeIntervalsDAO intervalDAO;
    private static final String TEST_LICENSE = "TRUCK-BL-TEST";

    @BeforeAll
    public static void setupClass() {
        truckDAO = new TruckDAO();
        intervalDAO = new TruckTimeIntervalsDAO();
    }

    @BeforeEach
    public void reset() throws Exception {
        // Remove time intervals first
        for (TruckTimeIntervalDTO dto : intervalDAO.getAll()) {
            if (dto.getTruckLicense().equals(TEST_LICENSE)) {
                intervalDAO.delete(dto.getTruckLicense(), dto.getDatetime1(), dto.getDatetime2());
            }
        }

        // Make sure no Transportations reference this truck (if needed)
        // transportationDAO.deleteByTruck(TEST_LICENSE); ← only if you have this

        // Remove truck after dependents are gone
        if (truckDAO.exists(TEST_LICENSE)) {
            truckDAO.delete(TEST_LICENSE);
        }
    }


    @Test
    @Order(1)
    public void testBusyIntervalAndAvailability() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 3000, 10000, LicenseType.C, truckDAO, intervalDAO);
        LocalDateTime from = LocalDateTime.of(2025, 6, 10, 10, 0);
        LocalDateTime to = from.plusHours(2);

        truck.addBusyInterval(from, to);
        assertFalse(truck.isAvailableFor(from.plusMinutes(30), to.minusMinutes(30)));
        assertTrue(truck.isAvailableFor(to.plusHours(1), to.plusHours(2)));
    }

    @Test
    @Order(2)
    public void testLicenseChange() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 2000, 8000, LicenseType.B, truckDAO, intervalDAO);
        assertEquals(LicenseType.B, truck.getRequiredLicenseType());

        truck.setRequiredLicenseType(LicenseType.D);
        assertEquals(LicenseType.D, truck.getRequiredLicenseType());
    }

    @Test
    @Order(3)
    public void testLoadCapacityCalculation() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 2500, 10000, LicenseType.C, truckDAO, intervalDAO);
        assertEquals(7500, truck.getAvailableLoadWeight());
    }

    @Test
    @Order(4)
    public void testCanCarryWeightBetweenStops() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 3000, 10000, LicenseType.C, truckDAO, intervalDAO);
        SiteDTO site = new SiteDTO("Depot", "City", "Code", "Phone", TransportationZone.CENTER);

        StopBL s1 = new StopBL(site, null);
        StopBL s2 = new StopBL(site, null);
        StopBL s3 = new StopBL(site, null);
        List<StopBL> stops = List.of(s1, s2, s3);

        assertTrue(truck.canCarryWeightBetween(stops, s1, s3, 5000));
        assertFalse(truck.canCarryWeightBetween(stops, s1, s3, 8000));
    }

    @Test
    public void testTruckCreationAndWeights() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 2500, 8000, LicenseType.B, truckDAO, intervalDAO);
        assertEquals(2500, truck.getNetWeight());
        assertEquals(8000, truck.getMaxWeight());
        assertEquals(5500, truck.getAvailableLoadWeight());
    }

    @Test
    public void testAddAndRemoveBusyInterval() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 2500, 8000, LicenseType.B, truckDAO, intervalDAO);
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime end = start.plusHours(2);

        truck.addBusyInterval(start, end);
        assertFalse(truck.isAvailableFor(start.plusMinutes(15), end.minusMinutes(15)));

        truck.removeBusyInterval(start, end);
        assertTrue(truck.isAvailableFor(start.plusMinutes(15), end.minusMinutes(15)));
    }

    @Test
    public void testAvailabilityAcrossMultipleIntervals() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 2500, 8000, LicenseType.C, truckDAO, intervalDAO);
        LocalDateTime firstStart = LocalDateTime.of(2025, 6, 5, 9, 0);
        LocalDateTime firstEnd = firstStart.plusHours(2);
        LocalDateTime secondStart = LocalDateTime.of(2025, 6, 5, 13, 0);
        LocalDateTime secondEnd = secondStart.plusHours(2);

        truck.addBusyInterval(firstStart, firstEnd);
        truck.addBusyInterval(secondStart, secondEnd);

        assertFalse(truck.isAvailableFor(firstStart.plusMinutes(30), firstEnd.minusMinutes(30)));
        assertFalse(truck.isAvailableFor(secondStart.plusMinutes(30), secondEnd.minusMinutes(30)));
        assertTrue(truck.isAvailableFor(firstEnd.plusMinutes(1), secondStart.minusMinutes(1)));
    }

    @Test
    public void testSetAndGetRequiredLicense() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 3000, 10000, LicenseType.B, truckDAO, intervalDAO);
        assertEquals(LicenseType.B, truck.getRequiredLicenseType());

        truck.setRequiredLicenseType(LicenseType.C);
        assertEquals(LicenseType.C, truck.getRequiredLicenseType());
    }

    @Test
    public void testToString() {
        TruckBL truck = new TruckBL(TEST_LICENSE, 2000, 10000, LicenseType.D, truckDAO, intervalDAO);
        String result = truck.toString();
        assertTrue(result.contains("Truck"));
        assertTrue(result.contains(TEST_LICENSE));
        assertTrue(result.contains("Required License: D"));
    }
}




