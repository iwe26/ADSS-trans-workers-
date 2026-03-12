package hrmanagement;

import Transportation.BusinessLayer.Resources.TransportationZone;
import Transportation.ServiceLayer.SiteService;
import Utils.AppDatabaseManager;
import hrmanagement.domain.entities.HRManager;
import hrmanagement.presentation.Screens.*;
import hrmanagement.presentation.console.Console;
import hrmanagement.presentation.console.SystemConsole;
import hrmanagement.service.EmployeeService;
import hrmanagement.service.HRService;
import hrmanagement.service.impl.EmployeeServiceImpl;
import hrmanagement.service.impl.HRServiceImpl;
import hrmanagement.dal.dao.*;
import hrmanagement.domain.facade.*;
import hrmanagement.domain.repository.*;
import hrmanagement.domain.repository.impl.*;

import java.sql.*;
import java.util.HashMap;

public class Main {

    private final Console cli;
    private  HRService hrService;
    private  SiteService siteService;
    private  EmployeeService employeeService;

/*    public Main(SiteService siteService) {
        // Initialize the console for user interaction
        this.cli = new SystemConsole();

        // 1. Instantiate DAOs
        EmployeeDAO employeeDAO                   = new EmployeeDAO();
        EmployeeAvailabilityDAO availabilityDAO   = new EmployeeAvailabilityDAO();
        EmployeeRoleDAO employeeRoleDAO           = new EmployeeRoleDAO();
        RoleDAO roleDAO                           = new RoleDAO();
        ShiftDAO shiftDAO                         = new ShiftDAO();
        ShiftAssignmentDAO shiftAssignmentDAO     = new ShiftAssignmentDAO();
        ShiftRequiredRoleDAO shiftRequiredRoleDAO = new ShiftRequiredRoleDAO();
        SiteDAO siteDAO                           = new SiteDAO();

        // 2. Wrap DAOs in Repositories
        EmployeeRepository            employeeRepo;
        EmployeeAvailabilityRepository availabilityRepo;
        EmployeeRoleRepository        employeeRoleRepo;
        RoleRepository                roleRepo;
        ShiftRepository               shiftRepo;
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
            cli.println("Error initializing repositories: " + e.getMessage());
            return;
        }
        // 3. Build Facades
        EmployeeFacade            employeeFacade            = new EmployeeFacade(employeeRepo);
        EmployeeAvailabilityFacade availabilityFacade        = new EmployeeAvailabilityFacade(availabilityRepo);
        EmployeeRoleFacade        employeeRoleFacade         = new EmployeeRoleFacade(employeeRoleRepo);
        RoleFacade                roleFacade                 = new RoleFacade(roleRepo);
        ShiftFacade               shiftFacade                = new ShiftFacade(shiftRepo);
        ShiftAssignmentFacade     shiftAssignmentFacade      = new ShiftAssignmentFacade(shiftAssignmentRepo);
        ShiftRequiredRoleFacade   shiftRequiredRoleFacade    = new ShiftRequiredRoleFacade(shiftRequiredRoleRepo);
        SiteFacade                siteFacade                 = new SiteFacade(siteRepo);







        // 4. Construct business domain HRManager
        HRManager hrManager = new HRManager(
                employeeFacade,
                roleFacade,
                employeeRoleFacade,
                shiftFacade,
                shiftAssignmentFacade,
                shiftRequiredRoleFacade,
                availabilityFacade
        );
        this.employeeService = new EmployeeServiceImpl(employeeFacade, availabilityFacade);
        this.hrService = new HRServiceImpl(hrManager);
        this.siteService = siteService;
        this.siteService.loadSitesFromDatabase();
    }*/

    public Main(SiteService sharedSiteService, EmployeeFacade employeeFacade, RoleFacade roleFacade, EmployeeRoleFacade employeeRoleFacade, ShiftFacade shiftFacade, ShiftAssignmentFacade shiftAssignmentFacade, ShiftRequiredRoleFacade shiftRequiredRoleFacade, EmployeeAvailabilityFacade availabilityFacade) {
        // Initialize the console for user interaction
        this.cli = new SystemConsole();
        // 4. Construct business domain HRManager
        HRManager hrManager = new HRManager(
                employeeFacade,
                roleFacade,
                employeeRoleFacade,
                shiftFacade,
                shiftAssignmentFacade,
                shiftRequiredRoleFacade,
                availabilityFacade
        );
        this.employeeService = new EmployeeServiceImpl(employeeFacade, availabilityFacade);
        this.hrService = new HRServiceImpl(hrManager);
        this.siteService = sharedSiteService;
        this.siteService.loadSitesFromDatabase();
    }

    public void launch() {
        Connection conn = AppDatabaseManager.getConnection();

        Screen current = new RoleSelectionScreen(cli);
        while (current != null) {
            if (current instanceof HRManagerScreen) {
                ((HRManagerScreen) current).setController(hrService);
                ((HRManagerScreen) current).setAllSites(siteService.getAllSiteAddresses());
            }
            if (current instanceof AdminScreen) {
                ((AdminScreen) current).setController(siteService);
            }
            if (current instanceof EmployeeScreen) {
                ((EmployeeScreen) current).setController(employeeService);
            }

            current.show();
            current = current.handleInput();
        }

        cli.println("Goodbye!");
    }
}
