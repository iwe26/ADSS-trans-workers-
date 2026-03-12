package hrmanagement.presentation.Screens;

import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.domain.enums.ShiftType;
import hrmanagement.presentation.console.Console;
import hrmanagement.presentation.controller.EmployeeController;
import hrmanagement.service.EmployeeService;
import hrmanagement.service.impl.EmployeeServiceImpl;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * CLI screen for regular employees.
 */
public class EmployeeScreen extends Screen {
    private final List<String> options = List.of(
            "Update availability constraints",
            "Request Leave",
            "Exit"
    );

    private EmployeeController controller;

    public void setController(EmployeeService employeeService) {
        this.controller = new EmployeeController(employeeService);
    }

    public EmployeeScreen(Console console) {
        super(console);
    }

    @Override
    public void show() {
        console.println("\n=== Employee Portal ===");
    }

    @Override
    public Screen handleInput() {
        EmployeeDTO emp = promptForEmployee();
        if (emp == null) {
            return new RoleSelectionScreen(console);
        }

        showMenu();
        String choice = console.readLine().trim();
        return handleMenuChoice(choice, emp);
    }

    private EmployeeDTO promptForEmployee() {
        console.print("Enter your employee ID: ");
        String raw = console.readLine().trim();
        int id;
        try {
            id = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            console.println("Invalid ID format.");
            return null;
        }

        Optional<EmployeeDTO> opt = controller.lookupEmployee(id);
        if (opt.isEmpty()) {
            console.println("No such employee.");
            return null;
        }
        return opt.get();
    }

    private void showMenu() {
        console.println("\nPlease select an option:");
        for (int i = 0; i < options.size(); i++) {
            console.println((i+1) + ". " + options.get(i));
        }
        console.print("Choice: ");
    }

    private Screen handleMenuChoice(String choice, EmployeeDTO emp) {
        switch (choice) {
            case "1": return updateAvailability(emp);
            case "2": return requestLeave(emp);
            case "3":
                console.println("Exiting to role selection...");
                return new RoleSelectionScreen(console);
            default:
                console.println("Invalid selection.");
                return this;
        }
    }

    private Screen updateAvailability(EmployeeDTO emp) {
        console.println("Updating availability constraints...");
        Map<LocalDate, List<ShiftType>> toAdd = new HashMap<>();

        while (true) {
            console.print("Enter date (YYYY-MM-DD) or 'done': ");
            String s = console.readLine().trim();
            if ("done".equalsIgnoreCase(s)) break;

            LocalDate date;
            try {
                date = LocalDate.parse(s);
                if (!date.isAfter(LocalDate.now())) {
                    console.println("Date must be in the future.");
                    continue;
                }
            } catch (Exception e) {
                console.println("Invalid date format.");
                continue;
            }

            console.print("Enter shift (MORNING/EVENING): ");
            String t = console.readLine().trim().toUpperCase();
            ShiftType shiftType;
            try {
                shiftType = ShiftType.valueOf(t);
            } catch (IllegalArgumentException e) {
                console.println("Invalid shift type.");
                continue;
            }

            toAdd.computeIfAbsent(date, d -> new ArrayList<>()).add(shiftType);
            console.println("Unavailable: " + date + " " + shiftType);
        }

        boolean ok = controller.updateAvailability(emp.getId(), toAdd);
        console.println(ok ? "Availability updated." : "Failed to update availability.");
        return this;
    }

    private Screen requestLeave(EmployeeDTO emp) {
        console.println("Requesting leave...");
        boolean ok = controller.requestLeave(emp.getId());
        console.println(ok ? "Leave requested." : "Failed to request leave.");
        return this;
    }
}
