import Transportation.PresentationLayer.Application;
import Transportation.ServiceLayer.SiteService;
import Utils.AppDatabaseManager;
import hrmanagement.Main;
import hrmanagement.dal.dao.*;
import hrmanagement.domain.facade.*;
import hrmanagement.domain.repository.*;
import hrmanagement.domain.repository.impl.*;

import java.sql.SQLException;
import java.util.Scanner;

public class MainApplication {
    private static final Scanner scanner = new Scanner(System.in);

    // Shared SiteService instance for both modules
    private static final SiteService sharedSiteService = new SiteService();


    private EmployeeDAO employeeDAO                   = new EmployeeDAO();
    private EmployeeAvailabilityDAO availabilityDAO   = new EmployeeAvailabilityDAO();
    private EmployeeRoleDAO employeeRoleDAO           = new EmployeeRoleDAO();
    private RoleDAO roleDAO                           = new RoleDAO();
    private ShiftDAO shiftDAO                         = new ShiftDAO();
    private ShiftAssignmentDAO shiftAssignmentDAO     = new ShiftAssignmentDAO();
    private ShiftRequiredRoleDAO shiftRequiredRoleDAO = new ShiftRequiredRoleDAO();
    private SiteDAO siteDAO                           = new SiteDAO();

    // 2. Wrap DAOs in Repositories
    private static EmployeeRepository employeeRepo;
    private static EmployeeAvailabilityRepository availabilityRepo;
    private static EmployeeRoleRepository employeeRoleRepo;
    private static RoleRepository roleRepo;
    private static ShiftRepository shiftRepo;
    private static ShiftAssignmentRepository     shiftAssignmentRepo;
    private static ShiftRequiredRoleRepository   shiftRequiredRoleRepo;
    private static SiteRepository                siteRepo;

    {
        try {
            employeeRepo = new InMemoryEmployeeRepository(employeeDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            availabilityRepo = new InMemoryEmployeeAvailabilityRepository(availabilityDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            employeeRoleRepo = new InMemoryEmployeeRoleRepository(employeeRoleDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            roleRepo = new InMemoryRoleRepository(roleDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            shiftRepo = new InMemoryShiftRepository(shiftDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            shiftAssignmentRepo = new InMemoryShiftAssignmentRepository(shiftAssignmentDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            shiftRequiredRoleRepo = new InMemoryShiftRequiredRoleRepository(shiftRequiredRoleDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            siteRepo = new InMemorySiteRepository(siteDAO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // 3. Build Facades
    private static final EmployeeFacade            employeeFacade            = new EmployeeFacade(employeeRepo);
    private static final EmployeeAvailabilityFacade availabilityFacade        = new EmployeeAvailabilityFacade(availabilityRepo);
    private static final EmployeeRoleFacade        employeeRoleFacade         = new EmployeeRoleFacade(employeeRoleRepo);
    private static final RoleFacade                roleFacade                 = new RoleFacade(roleRepo);
    private static final ShiftFacade               shiftFacade                = new ShiftFacade(shiftRepo);
    private static final ShiftAssignmentFacade     shiftAssignmentFacade      = new ShiftAssignmentFacade(shiftAssignmentRepo);
    private static final ShiftRequiredRoleFacade   shiftRequiredRoleFacade    = new ShiftRequiredRoleFacade(shiftRequiredRoleRepo);
    private static final SiteFacade                siteFacade                 = new SiteFacade(siteRepo);




    // Pass sharedSiteService into constructors
    private static final Application transportationApp = new Application(sharedSiteService,shiftFacade, shiftAssignmentFacade, roleFacade , employeeRoleFacade, employeeFacade, availabilityFacade);
    private static final Main hrApp = new Main(sharedSiteService, employeeFacade, roleFacade, employeeRoleFacade, shiftFacade, shiftAssignmentFacade, shiftRequiredRoleFacade, availabilityFacade);

    public static void main(String[] args)
    {




        AppDatabaseManager.main(null);
        while (true) {
            System.out.println("\n=== Main Application ===");
            System.out.println("Select Module:");
            System.out.println("1. HR");
            System.out.println("2. Transportation");
            System.out.println("3. Exit");

            int choice = readInt();

            switch (choice) {
                case 1 -> hrApp.launch();
                case 2 -> selectTransportationRole();
                case 3 -> {
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void selectTransportationRole() {
        System.out.println("\n=== Transportation Module ===");
        System.out.println("Choose Role:");
        System.out.println("1. Admin");
        System.out.println("2. Transportation Manager");
        System.out.println("3. Driver");
        System.out.print("Enter choice: ");

        int role = readInt();
        switch (role) {
            case 1 -> transportationApp.launchAdmin();
            case 2 -> transportationApp.launchTransportationManager();
            case 3 -> {
                System.out.print("Enter Driver ID: ");
                int id = readInt();
                if (transportationApp.isValidDriver(id)) {
                    transportationApp.launchDriver(id);
                } else {
                    System.out.println("Invalid driver ID.");
                }
            }
            default -> System.out.println("Invalid role.");
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Invalid input, try again: ");
            }
        }
    }
}
