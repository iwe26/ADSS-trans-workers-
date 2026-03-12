package hrmanagement.domain.entities;

import hrmanagement.dal.dao.*;
import hrmanagement.dal.dto.*;
import hrmanagement.domain.enums.ShiftType;
import Transportation.BusinessLayer.Resources.TransportationZone;
import Utils.AppDatabaseManager;
import hrmanagement.domain.facade.*;
import hrmanagement.domain.repository.*;
import hrmanagement.domain.repository.impl.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HRManagerTest {

    private HRManager hrManager;
    private final int EMPLOYEE_ID_1 = 100;
    private final int EMPLOYEE_ID_2 = 200;
    private final String ROLE_CASHIER = "Cashier";
    private final String ROLE_CLEANER = "Cleaner";
    private final String ROLE_MANAGER = "Manager";
    private final String SITE_ADDRESS = "test-branch-address";
    private LocalDate today;

    private EmployeeFacade             employeeFacade;
    private EmployeeAvailabilityFacade availabilityFacade;
    private EmployeeRoleFacade         employeeRoleFacade;
    private RoleFacade                 roleFacade;
    private ShiftFacade                shiftFacade;
    private ShiftAssignmentFacade      shiftAssignmentFacade;
    private ShiftRequiredRoleFacade    shiftRequiredRoleFacade;
    private SiteFacade                 siteFacade;

    /**
     * Before each test:
     * 1. Temporarily disable foreign-key enforcement.
     * 2. Delete all rows from every table.
     * 3. Re-enable foreign-key enforcement.
     * 4. Insert one SiteDTO so that employees can reference SITE_ADDRESS.
     * 5. Re-create HRManager.
     */
    @BeforeEach
    void clearAndPrepareDatabase() throws Exception {
        Connection conn = AppDatabaseManager.getConnection();
        Statement stmt = null;
        SiteDAO siteDAO = new SiteDAO();
        try {
            stmt = conn.createStatement();

            // 1. Turn off foreign-key enforcement
            stmt.execute("PRAGMA foreign_keys = OFF;");

            // 2. Delete every table's rows
            stmt.executeUpdate("DELETE FROM shift_assignment;");
            stmt.executeUpdate("DELETE FROM shift_required_role;");
            stmt.executeUpdate("DELETE FROM employee_availability;");
            stmt.executeUpdate("DELETE FROM employee_role;");
            stmt.executeUpdate("DELETE FROM Driver_License;");
            stmt.executeUpdate("DELETE FROM Transportation_Stops;");
            stmt.executeUpdate("DELETE FROM Item_At_Stop;");
            stmt.executeUpdate("DELETE FROM Truck_TimeIntervals;");
            stmt.executeUpdate("DELETE FROM Transportations;");
            stmt.executeUpdate("DELETE FROM Trucks;");
            stmt.executeUpdate("DELETE FROM shift;");
            stmt.executeUpdate("DELETE FROM role;");
            stmt.executeUpdate("DELETE FROM employee;");
            stmt.executeUpdate("DELETE FROM site;");

            // 3. Turn foreign-key enforcement back on
            stmt.execute("PRAGMA foreign_keys = ON;");

            // 4. Insert a single test site
            SiteDTO testSite = new SiteDTO(
                    "Test Branch",             // ← name
                    SITE_ADDRESS,              // ← address (PK column, which shift.site_address and employee.site_address will reference)
                    "Test Contact",
                    "050‑9999999",
                    TransportationZone.CENTER
            );
            siteDAO.insert(testSite);

        } finally {
            if (stmt != null) stmt.close();
        }


        // 1. Instantiate DAOs
        EmployeeDAO employeeDAO                   = new EmployeeDAO();
        EmployeeAvailabilityDAO availabilityDAO   = new EmployeeAvailabilityDAO();
        EmployeeRoleDAO employeeRoleDAO           = new EmployeeRoleDAO();
        RoleDAO roleDAO                           = new RoleDAO();
        ShiftDAO shiftDAO                         = new ShiftDAO();
        ShiftAssignmentDAO shiftAssignmentDAO     = new ShiftAssignmentDAO();
        ShiftRequiredRoleDAO shiftRequiredRoleDAO = new ShiftRequiredRoleDAO();

        // 2. Wrap DAOs in Repositories
        EmployeeRepository employeeRepo;
        EmployeeAvailabilityRepository availabilityRepo;
        EmployeeRoleRepository employeeRoleRepo;
        RoleRepository roleRepo;
        ShiftRepository shiftRepo;
        ShiftAssignmentRepository     shiftAssignmentRepo;
        ShiftRequiredRoleRepository   shiftRequiredRoleRepo;
        SiteRepository                siteRepo;
        try {
            employeeRepo = new InMemoryEmployeeRepository(employeeDAO);
            availabilityRepo = new InMemoryEmployeeAvailabilityRepository(availabilityDAO);
            employeeRoleRepo = new InMemoryEmployeeRoleRepository(employeeRoleDAO);
            roleRepo = new InMemoryRoleRepository(roleDAO);
            shiftRepo = new InMemoryShiftRepository(shiftDAO);
            shiftAssignmentRepo = new InMemoryShiftAssignmentRepository(shiftAssignmentDAO);
            shiftRequiredRoleRepo = new InMemoryShiftRequiredRoleRepository(shiftRequiredRoleDAO);
            siteRepo = new InMemorySiteRepository(siteDAO);
        }
        catch (SQLException e) {
            return;
        }
        // 3. Build Facades
        employeeFacade            = new EmployeeFacade(employeeRepo);
        availabilityFacade        = new EmployeeAvailabilityFacade(availabilityRepo);
        employeeRoleFacade         = new EmployeeRoleFacade(employeeRoleRepo);
        roleFacade                 = new RoleFacade(roleRepo);
        shiftFacade                = new ShiftFacade(shiftRepo);
        shiftAssignmentFacade      = new ShiftAssignmentFacade(shiftAssignmentRepo);
        shiftRequiredRoleFacade    = new ShiftRequiredRoleFacade(shiftRequiredRoleRepo);
        siteFacade                 = new SiteFacade(siteRepo);







        // 4. Construct business domain HRManager
         hrManager = new HRManager(
                employeeFacade,
                roleFacade,
                employeeRoleFacade,
                shiftFacade,
                shiftAssignmentFacade,
                shiftRequiredRoleFacade,
                availabilityFacade
        );        today = LocalDate.now();
    }

    // --- Employee creation & existence ----------------------------------

    @Test
    void testCreateEmployeeAndExists() throws SQLException {
        // Initially, no employee with EMPLOYEE_ID_1
        System.out.println(hrManager.employeeExists(EMPLOYEE_ID_1));
        assertFalse(hrManager.employeeExists(EMPLOYEE_ID_1));

        // Call createEmployee(name, id, bankAccount, salary, terms, startDate, siteAddress)
        hrManager.createEmployee(
                "Alice",
                EMPLOYEE_ID_1,
                "IL-11122233",
                7500.0,
                "Full-Time",
                LocalDate.of(2023, 1, 1),
                SITE_ADDRESS
        );

        // Now it should exist
        assertTrue(hrManager.employeeExists(EMPLOYEE_ID_1));

        // Verify via EmployeeDAO
        EmployeeDTO fetched = new EmployeeDAO().findById(EMPLOYEE_ID_1);
        assertNotNull(fetched);
        assertEquals("Alice", fetched.getName());
        assertEquals("IL-11122233", fetched.getBankAccount());
        assertEquals(7500.0, fetched.getSalary());
        assertEquals("Full-Time", fetched.getEmploymentTerms());
        assertEquals(LocalDate.of(2023, 1, 1), fetched.getStartDate());
        assertTrue(fetched.isActive());
        assertEquals(SITE_ADDRESS, fetched.getBranchAddress());
    }

    // --- Role assignment & checking -------------------------------------

    @Test
    void testAssignRoleToEmployeeAndHasRole() throws SQLException {
        // First, create a new employee
        hrManager.createEmployee(
                "Bob",
                EMPLOYEE_ID_1,
                "IL-44455566",
                6500.0,
                "Part-Time",
                LocalDate.of(2022, 6, 15),
                SITE_ADDRESS
        );

        // Initially, Bob has no roles
        assertFalse(hrManager.employeeHasRole(EMPLOYEE_ID_1, ROLE_CASHIER));

        // Assign ROLE_CASHIER
        hrManager.assignRoleToEmployee(EMPLOYEE_ID_1, ROLE_CASHIER);
        assertTrue(hrManager.employeeHasRole(EMPLOYEE_ID_1, ROLE_CASHIER));

        // Assign ROLE_CLEANER
        hrManager.assignRoleToEmployee(EMPLOYEE_ID_1, ROLE_CLEANER);
        assertTrue(hrManager.employeeHasRole(EMPLOYEE_ID_1, ROLE_CLEANER));

        // An unassigned role should return false
        assertFalse(hrManager.employeeHasRole(EMPLOYEE_ID_1, ROLE_MANAGER));
    }

    // --- Availability queries -------------------------------------------

    @Test
    void testIsEmployeeAvailableWithoutConstraints() {
        // Create employee without any availability rows
        hrManager.createEmployee(
                "Carol",
                EMPLOYEE_ID_1,
                "IL-77788899",
                7000.0,
                "Full-Time",
                LocalDate.of(2021, 12, 1),
                SITE_ADDRESS
        );
        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.MORNING,
                List.of(ROLE_CASHIER, ROLE_CLEANER),
                new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        Optional<Integer> shiftIdOpt2 = hrManager.createShift(
                today,
                ShiftType.EVENING,
                List.of(ROLE_CASHIER, ROLE_CLEANER),
                new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        // By default, no availability constraints means employee is available for all shifts
        assertTrue(hrManager.isEmployeeAvailable(today, ShiftType.MORNING, EMPLOYEE_ID_1));
        assertTrue(hrManager.isEmployeeAvailable(today, ShiftType.EVENING, EMPLOYEE_ID_1));
    }

    @Test
    void testIsEmployeeAvailableWithConstraints() throws SQLException {
        // Create employee
        hrManager.createEmployee(
                "Dave",
                EMPLOYEE_ID_1,
                "IL-10101010",
                8000.0,
                "Full-Time",
                LocalDate.of(2020, 5, 10),
                SITE_ADDRESS
        );

        // Insert an unavailability row via DAO: Dave cannot work MORNING of 'today'
        EmployeeAvailabilityDTO unavail = new EmployeeAvailabilityDTO(
                EMPLOYEE_ID_1,
                today,
                ShiftType.MORNING
        );
        //new EmployeeAvailabilityDAO().insert(unavail);
        availabilityFacade.createAvailability(unavail);



        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.MORNING,
                List.of(ROLE_CASHIER, ROLE_CLEANER),
                new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        Optional<Integer> shiftIdOpt2 = hrManager.createShift(
                today,
                ShiftType.EVENING,
                List.of(ROLE_CASHIER, ROLE_CLEANER),
                new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        // Now: not available for MORNING, but available for EVENING
        assertFalse(hrManager.isEmployeeAvailable(today, ShiftType.MORNING, EMPLOYEE_ID_1));
        assertTrue(hrManager.isEmployeeAvailable(today, ShiftType.EVENING, EMPLOYEE_ID_1));
    }

    // --- Shift creation, existence, and initial assignments --------------

    @Test
    void testCreateShiftAndGetAssignedEmployees() throws SQLException {
        // Create two employees
        hrManager.createEmployee(
                "Eve",
                EMPLOYEE_ID_1,
                "IL-22233344",
                7200.0,
                "Full-Time",
                LocalDate.of(2022, 2, 2),
                SITE_ADDRESS
        );
        hrManager.createEmployee(
                "Frank",
                EMPLOYEE_ID_2,
                "IL-33344455",
                7300.0,
                "Part-Time",
                LocalDate.of(2023, 3, 3),
                SITE_ADDRESS
        );

        // Assign distinct roles for coverage
        hrManager.assignRoleToEmployee(EMPLOYEE_ID_1, ROLE_CASHIER);
        hrManager.assignRoleToEmployee(EMPLOYEE_ID_2, ROLE_CLEANER);

        // Create a shift on 'today' morning requiring both roles, assigning both employees,
        // managerId = EMPLOYEE_ID_1, and passing siteAddress
        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.MORNING,
                List.of(ROLE_CASHIER, ROLE_CLEANER),
                new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // shiftExists must be true
        assertTrue(hrManager.shiftExists(shiftId));

        // getAssignedEmployees should contain both
        List<Integer> assigned = hrManager.getAssignedEmployees(shiftId);
        assertEquals(2, assigned.size());
        assertTrue(assigned.contains(EMPLOYEE_ID_1));
        assertTrue(assigned.contains(EMPLOYEE_ID_2));

        // getShiftRoles should list both required roles
        List<String> roles = hrManager.getShiftRoles(shiftId);
        assertEquals(2, roles.size());
        assertTrue(roles.contains(ROLE_CASHIER));
        assertTrue(roles.contains(ROLE_CLEANER));
    }

    // --- Assign / Remove employees from an existing shift ----------------

    @Test
    void testAssignAndRemoveEmployeeFromShift() throws SQLException {
        // Create one employee
        hrManager.createEmployee(
                "Greg",
                EMPLOYEE_ID_1,
                "IL-44455566",
                7100.0,
                "Full-Time",
                LocalDate.of(2022, 4, 4),
                SITE_ADDRESS
        );

        // Create a shift with no initial assignments, manager = EMPLOYEE_ID_1, siteAddress = SITE_ADDRESS
        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.EVENING,
                List.of(),          // no required roles
                new HashSet<>(),    // no initial assignments
                EMPLOYEE_ID_1,      // manager
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // assignEmployeeToShift
        hrManager.assignEmployeeToShift(shiftId, EMPLOYEE_ID_1);
        List<Integer> assignedNow = hrManager.getAssignedEmployees(shiftId);
        assertTrue(assignedNow.contains(EMPLOYEE_ID_1));

        // removeEmployeeFromShift
        hrManager.removeEmployeeFromShift(shiftId, EMPLOYEE_ID_1);
        List<Integer> assignedAfterRemoval = hrManager.getAssignedEmployees(shiftId);
        assertFalse(assignedAfterRemoval.contains(EMPLOYEE_ID_1));
    }

    // --- Shift manager get/set -------------------------------------------

    @Test
    void testGetAndSetShiftManager() throws SQLException {
        // Create two employees
        hrManager.createEmployee(
                "Helen",
                EMPLOYEE_ID_1,
                "IL-55566677",
                7800.0,
                "Full-Time",
                LocalDate.of(2021, 5, 5),
                SITE_ADDRESS
        );
        hrManager.createEmployee(
                "Ian",
                EMPLOYEE_ID_2,
                "IL-66677788",
                7900.0,
                "Part-Time",
                LocalDate.of(2023, 6, 6),
                SITE_ADDRESS
        );

        // Create a shift with manager = EMPLOYEE_ID_1, siteAddress = SITE_ADDRESS
        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.MORNING,
                List.of(),              // no required roles
                new HashSet<>(),        // no initial assignments
                EMPLOYEE_ID_1,          // manager = Helen
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // getShiftManager should return EMPLOYEE_ID_1
        assertEquals(Optional.of(EMPLOYEE_ID_1), hrManager.getShiftManager(shiftId));

        // Change the shift manager to EMPLOYEE_ID_2 (Ian)
        hrManager.setShiftManager(shiftId, EMPLOYEE_ID_2);
        assertEquals(Optional.of(EMPLOYEE_ID_2), hrManager.getShiftManager(shiftId));
    }

    // --- validateRoleCoverage -------------------------------------------

    @Test
    void testValidateRoleCoverageTrue() throws SQLException {
        // Create two employees with distinct roles
        hrManager.createEmployee(
                "Jack",
                EMPLOYEE_ID_1,
                "IL-88899900",
                8000.0,
                "Full-Time",
                LocalDate.of(2021, 7, 7),
                SITE_ADDRESS
        );
        hrManager.createEmployee(
                "Kate",
                EMPLOYEE_ID_2,
                "IL-99900011",
                8100.0,
                "Part-Time",
                LocalDate.of(2022, 8, 8),
                SITE_ADDRESS
        );

        hrManager.assignRoleToEmployee(EMPLOYEE_ID_1, ROLE_CASHIER);
        hrManager.assignRoleToEmployee(EMPLOYEE_ID_2, ROLE_CLEANER);

        // Create shift requiring both roles, assigning both employees, manager = EMPLOYEE_ID_1, siteAddress = SITE_ADDRESS
        List<String> requiredRoles = List.of(ROLE_CASHIER, ROLE_CLEANER);
        HashSet<Integer> emp = new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2));

        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.EVENING,
                requiredRoles,
                emp,
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // validateRoleCoverage should return true
        assertTrue(hrManager.validateRoleCoverage(shiftId));
    }

    @Test
    void testValidateRoleCoverageFalse() throws SQLException {
        // Create two employees, but assign only one role
        hrManager.createEmployee(
                "Liam",
                EMPLOYEE_ID_1,
                "IL-12121212",
                8200.0,
                "Full-Time",
                LocalDate.of(2023, 9, 9),
                SITE_ADDRESS
        );
        hrManager.createEmployee(
                "Mia",
                EMPLOYEE_ID_2,
                "IL-34343434",
                8300.0,
                "Full-Time",
                LocalDate.of(2022, 10, 10),
                SITE_ADDRESS
        );

        hrManager.assignRoleToEmployee(EMPLOYEE_ID_1, ROLE_CASHIER);
        // EMPLOYEE_ID_2 has no roles

        // Create shift requiring both roles, assign only EMPLOYEE_ID_1, siteAddress = SITE_ADDRESS
        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.MORNING,
                List.of(ROLE_CASHIER, ROLE_CLEANER),
                new HashSet<>(Collections.singletonList(EMPLOYEE_ID_1)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // Since no one has ROLE_CLEANER, coverage should fail
        assertFalse(hrManager.validateRoleCoverage(shiftId));
    }
    @Test
    void testEmployeeExistsFalseAfterDelete() throws SQLException {
        // Create and insert an employee
        hrManager.createEmployee(
                "Olivia",
                EMPLOYEE_ID_1,
                "IL-21212121",
                9000.0,
                "Full-Time",
                LocalDate.of(2020, 1, 1),
                SITE_ADDRESS
        );
        assertTrue(hrManager.employeeExists(EMPLOYEE_ID_1));

    }

    @Test
    void testEmployeeHasRoleReturnsFalseWhenNeverAssigned() throws SQLException {
        // Create an employee but do not assign any role
        hrManager.createEmployee(
                "Peter",
                EMPLOYEE_ID_1,
                "IL-32323232",
                5500.0,
                "Part-Time",
                LocalDate.of(2021, 3, 3),
                SITE_ADDRESS
        );
        // Asking for a role that was never created or assigned
        assertFalse(hrManager.employeeHasRole(EMPLOYEE_ID_1, "NonExistentRole"));
    }

    @Test
    void testShiftExistsFalseForInvalidId() {
        // Ensure no shift has been created with this ID (e.g., 9999)
        assertFalse(hrManager.shiftExists(9999));
    }

    @Test
    void testGetShiftRolesWhenNoRoles() throws SQLException {
        // Create two employees and a shift with no required roles
        hrManager.createEmployee(
                "Quinn",
                EMPLOYEE_ID_1,
                "IL-43434343",
                6200.0,
                "Full-Time",
                LocalDate.of(2022, 4, 4),
                SITE_ADDRESS
        );
        hrManager.createEmployee(
                "Riley",
                EMPLOYEE_ID_2,
                "IL-54545454",
                6300.0,
                "Full-Time",
                LocalDate.of(2023, 5, 5),
                SITE_ADDRESS
        );

        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.EVENING,
                List.of(), // no required roles
                new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // getShiftRoles should return an empty list
        List<String> roles = hrManager.getShiftRoles(shiftId);
        assertNotNull(roles);
        assertTrue(roles.isEmpty(), "Expected no roles when none were specified");
    }

    @Test
    void testIsEmployeeAvailableOverloadWithShiftId() throws SQLException {
        // Create and insert an employee
        hrManager.createEmployee(
                "Sam",
                EMPLOYEE_ID_1,
                "IL-65656565",
                7100.0,
                "Part-Time",
                LocalDate.of(2021, 6, 6),
                SITE_ADDRESS
        );
        // Create a shift on 'today' morning requiring no roles and assigning no one
        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.MORNING,
                List.of(), // no roles
                new HashSet<>(),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // Employee is not assigned but availability only checks "unavailable" records,
        // so isEmployeeAvailable(shiftId, EMPLOYEE_ID_1) should return true
        assertTrue(hrManager.isEmployeeAvailable(shiftId, EMPLOYEE_ID_1));

        // Now insert an unavailability for this shift's date and type
        ShiftDTO shift = new ShiftDAO().findById(shiftId);
        EmployeeAvailabilityDTO unavail = new EmployeeAvailabilityDTO(
                EMPLOYEE_ID_1,
                shift.getDate(),
                shift.getShiftType()
        );
        availabilityFacade.createAvailability(unavail);


        // Now isEmployeeAvailable(shiftId, EMPLOYEE_ID_1) should return false
        assertFalse(hrManager.isEmployeeAvailable(shiftId, EMPLOYEE_ID_1));
    }

    @Test
    void testValidateRoleCoverageWithNoRequiredRolesReturnsTrue() throws SQLException {
        // Create two employees without assigning any roles
        hrManager.createEmployee(
                "Taylor",
                EMPLOYEE_ID_1,
                "IL-76767676",
                8000.0,
                "Full-Time",
                LocalDate.of(2022, 7, 7),
                SITE_ADDRESS
        );
        hrManager.createEmployee(
                "Ursula",
                EMPLOYEE_ID_2,
                "IL-87878787",
                8100.0,
                "Full-Time",
                LocalDate.of(2023, 8, 8),
                SITE_ADDRESS
        );

        // Create a shift that requires no roles but assigns both employees
        Optional<Integer> shiftIdOpt = hrManager.createShift(
                today,
                ShiftType.EVENING,
                List.of(), // no required roles
                new HashSet<>(Arrays.asList(EMPLOYEE_ID_1, EMPLOYEE_ID_2)),
                EMPLOYEE_ID_1,
                SITE_ADDRESS
        );
        assertTrue(shiftIdOpt.isPresent());
        int shiftId = shiftIdOpt.get();

        // Since there are no required roles, validateRoleCoverage should return true
        assertTrue(hrManager.validateRoleCoverage(shiftId),
                "When no roles are required, coverage should automatically be true");
    }

}