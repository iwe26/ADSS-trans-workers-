package hrmanagement.presentation.Screens;

import hrmanagement.domain.entities.HRManager;
import hrmanagement.domain.enums.ShiftType;
import hrmanagement.presentation.console.Console;
import hrmanagement.presentation.controller.HRManagerController;
import hrmanagement.service.HRService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class HRManagerScreen extends Screen {



    private HRManagerController controller;
    public void setController(HRService manager) {
        this.controller = new HRManagerController(manager);
    }

    private HashSet<String> sitesNames;
    private final List<String> options = List.of(
            "Edit Shift",
            "Add Shift",
            "Add Employee",
            "Assign Roles to Employee",
            "Exit"
    );

    public HRManagerScreen(Console console) {
        super(console);
    }

    @Override
    public void show() {
        console.println("\n=== HR Manager Portal ===");
        console.println("Please select an option:");
        for (int i = 0; i < options.size(); i++) {
            console.println((i + 1) + ". " + options.get(i));
        }
        console.print("Choice: ");
    }



    /**
     * Dispatches the user’s menu choice to the appropriate handler.
     *
     * @return the next Screen to display (may be this to re-show, or RoleSelectionScreen to exit)
     */
    @Override
    public Screen handleInput() {
        String choice = console.readLine().trim();
        switch (choice) {
            case "1": return handleEditShift();
            case "2": return handleAddShift();
            case "3": return handleAddEmployee();
            case "4": return handleAssignRolesToEmployee();
            case "5":
                console.println("Returning to role selection...");
                return new RoleSelectionScreen(console);
            default:
                console.println("Invalid choice. Please try again.");
                return this;
        }
    }

    /**
     * Handles the “Edit Shift” workflow: remove/add employees, reassign manager, coverage check.
     *
     * @return this Screen to re-display the HR menu
     */
    private Screen handleEditShift() {
        console.print("Enter shift ID: ");
        int editId;
        try {
            editId = Integer.parseInt(console.readLine().trim());
        } catch (NumberFormatException e) {
            console.println("Invalid shift ID. Returning to menu.");
            return this;
        }
        if (!controller.shiftExists(editId)) {
            console.println("Shift not found.");
            return this;
        }

        List<Integer> currentEmps = controller.getAssignedEmployees(editId);
        int currentMgr = controller.getShiftManager(editId);
        console.println("Current employees on shift: " + currentEmps);

        // Remove employees
        while (true) {
            console.print("Remove employee by ID (enter -1 to stop): ");
            String rem = console.readLine().trim();
            if (rem.equals("-1")) break;
            int removeId;
            try {
                removeId = Integer.parseInt(rem);
            } catch (NumberFormatException ex) {
                console.println("Invalid employee ID format.");
                continue;
            }
            if (currentEmps.remove(Integer.valueOf(removeId))) {
                controller.removeEmployeeFromShift(editId, removeId);
                console.println("Removed " + removeId);
                if (removeId == currentMgr) {
                    console.println("Manager removed—must reassign.");
                    currentMgr = promptForManager(currentEmps);
                    controller.setShiftManager(editId, currentMgr);
                }
            } else {
                console.println("Employee not on that shift.");
            }
        }

        // Add employees
        while (true) {
            console.print("Add employee by ID (enter done to stop): ");
            String add = console.readLine().trim();
            if (add.equalsIgnoreCase("done")) break;
            int addId;
            try {
                addId = Integer.parseInt(add);
            } catch (NumberFormatException ex) {
                console.println("Invalid employee ID format.");
                continue;
            }
            if (currentEmps.contains(addId)) {
                console.println("Already on shift."); continue;
            }
            if (controller.isEmployeeAvailable(editId, addId)) {
                currentEmps.add(addId);
                controller.assignEmployeeToShift(editId, addId);
                console.println("Added " + addId);
            } else {
                console.println("Employee not available for this shift.");
            }
        }

        // Reassign manager if needed
        if (!currentEmps.contains(currentMgr)) {
            console.println("Current manager not on shift—must reassign.");
            currentMgr = promptForManager(currentEmps);
        } else {
            console.print("Reassign manager? (y/n): ");
            if (console.readLine().trim().equalsIgnoreCase("y")) {
                currentMgr = promptForManager(currentEmps);
            }
        }
        controller.setShiftManager(editId, currentMgr);

        if (!validateCoverage(controller.getShiftRoles(editId), currentEmps)) {
            console.println("Warning: not all required roles have at least one assigned employee.");
        }
        console.println("Shift updated.");
        return this;
    }

    /**
     * Handles the “Add Shift” workflow: create new shift with roles, employees, and manager.
     *
     * @return this Screen to re-display the HR menu
     */
    private Screen handleAddShift() {
        //ask for shift's branch
        String branchAddress = promptForSiteFromMenu();


        console.print("Enter shift date (YYYY-MM-DD): ");
        LocalDate date;
        try {
            date = LocalDate.parse(console.readLine().trim());
        } catch (Exception e) {
            console.println("Invalid date format. Please use YYYY-MM-DD.");
            return this;
        }

        console.print("Shift type (MORNING/EVENING): ");
        ShiftType type;
        try {
            type = ShiftType.valueOf(console.readLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            console.println("Invalid shift type. Please enter MORNING or EVENING.");
            return this;
        }

        List<String> newRoles = new ArrayList<>();
        while (true) {
            console.print("Enter required role (or 'done'): ");
            String r = console.readLine().trim();
            if (r.equalsIgnoreCase("done")) break;
            newRoles.add(r);
        }

        HashSet<Integer> newEmps = new HashSet<>();
        while (true) {
            console.print("Assign employee by ID (or 'done'): ");
            String e = console.readLine().trim();
            if (e.equalsIgnoreCase("done")) break;
            int id;
            try {
                id = Integer.parseInt(e);
            } catch (NumberFormatException ex) {
                console.println("Invalid employee ID format.");
                continue;
            }
            if (controller.isEmployeeAvailableCreateShift(date, type, id)) {
                newEmps.add(id);
            } else {
                console.println("Employee not available for this shift.");
            }
        }

        console.println("Assign shift manager (must be one of: " + newEmps + "):");
        int newMgr = promptForManager(new ArrayList<>(newEmps));



        controller.createShift(date, type, newRoles, newEmps, newMgr, branchAddress);

        if (!validateCoverage(newRoles, new ArrayList<>(newEmps))) {
            console.println("Warning: not all required roles have at least one assigned employee.");
        }
        console.println("New shift created.");
        return this;
    }

    /**
     * Handles the “Add Employee” workflow: collect details and create a new employee.
     *
     * @return this Screen to re-display the HR menu
     */
    private Screen handleAddEmployee() {
        console.print("Enter name: ");
        String name = console.readLine().trim();

        console.print("Enter ID: ");
        int empId;
        try {
            empId = Integer.parseInt(console.readLine().trim());
        } catch (NumberFormatException e) {
            console.println("Invalid employee ID. Returning to menu.");
            return this;
        }

        console.print("Enter bank account: ");
        String acct = console.readLine().trim();

        console.print("Enter salary: ");
        double sal;
        try {
            sal = Double.parseDouble(console.readLine().trim());
        } catch (NumberFormatException e) {
            console.println("Invalid salary format. Returning to menu.");
            return this;
        }

        console.print("Enter employment terms: ");
        String terms = console.readLine().trim();

        console.print("Enter start date (YYYY-MM-DD): ");
        LocalDate start;
        try {
            start = LocalDate.parse(console.readLine().trim());
        } catch (Exception e) {
            console.println("Invalid date format. Returning to menu.");
            return this;
        }


        String branchAddress = promptForSiteFromMenu();



        controller.createEmployee(name, empId, acct, sal, terms, start,branchAddress);
        console.println("Employee added.");
        return this;
    }

    /**
     * Handles assigning a new role to an existing employee.
     *
     * @return this Screen to re-display the HR menu
     */
    private Screen handleAssignRolesToEmployee() {
        console.print("Enter employee ID: ");
        String target = console.readLine().trim();
        int targetId;
        try {
            targetId = Integer.parseInt(target);
        } catch (NumberFormatException e) {
            console.println("Invalid employee ID. Returning to menu.");
            return this;
        }
        if (!controller.employeeExists(targetId)) {
            console.println("Employee not found.");
            return this;
        }

        console.print("Enter new role: ");
        String newRole = console.readLine().trim();
        controller.assignRoleToEmployee(targetId, newRole);
        console.println("Role assigned.");
        return this;
    }

    // Helper to prompt for a manager from a given list
    private int promptForManager(List<Integer> candidates) {
        while (true) {
            console.print("Enter manager ID: ");
            int id = Integer.parseInt(console.readLine().trim());
            if (candidates.contains(id)) {
                return id;
            }
            console.println("Invalid manager—must be one of " + candidates);
        }
    }

    // Helper to validate that every required role has at least one assigned employee
    private boolean validateCoverage(List<String> neededRoles, List<Integer> assignedEmpIds) {
        for (String role : neededRoles) {
            boolean covered = assignedEmpIds.stream()
                    .anyMatch(empId -> controller.employeeHasRole(empId, role));
            if (!covered) return false;
        }
        return true;
    }

    public void setAllSites(HashSet<String> allSitesNames) {
        sitesNames = allSitesNames;
    }

    private String promptForSiteFromMenu() {
        List<String> siteList = new ArrayList<>(sitesNames);
        while (true) {
            console.println("Choose a branch:");
            for (int i = 0; i < siteList.size(); i++) {
                console.println((i + 1) + ". " + siteList.get(i));
            }
            console.print("Enter number: ");
            try {
                int choice = Integer.parseInt(console.readLine().trim());
                if (choice >= 1 && choice <= siteList.size()) {
                    return siteList.get(choice - 1);
                }
            } catch (NumberFormatException ignored) {}
            console.println("Invalid choice. Try again.");
        }
    }
}