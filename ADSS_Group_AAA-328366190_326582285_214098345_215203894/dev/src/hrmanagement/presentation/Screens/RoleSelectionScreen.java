package hrmanagement.presentation.Screens;

import hrmanagement.domain.entities.HRManager;
import hrmanagement.presentation.console.Console;

import java.util.List;

public class RoleSelectionScreen extends Screen{
    private final List<String> roles = List.of("HR Manager", "Employee","Transportation Manager","Admin", "Exit");




    public RoleSelectionScreen(Console console) {
        super(console);
    }

    @Override
    public void show() {
        console.println("\nSelect your role:");
        for (int i = 0; i < roles.size(); i++) {
            console.println((i + 1) + ". " + roles.get(i));
        }
        console.println("Enter your choice: ");

    }


    @Override
    public Screen handleInput() {
        String line =  console.readLine().trim();
        int choice;
        try {
            choice = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            console.println("Invalid input. Please enter a number.");
            return this;
        }

        if (choice < 1 || choice > roles.size()) {
            console.println("Choice out of range. Try again.");
            return this;
        }

        String selected = roles.get(choice - 1);
        switch (selected) {
            case "HR Manager":
                return new HRManagerScreen(console);
            case "Employee":
                return new EmployeeScreen(console);
            case "Transportation Manager":
                //return new TransportationScreen(console);
            case "Admin":
                return new AdminScreen(console);
            case "Exit":
                console.println("Exiting the application.");
                return null; // Exit the application
            default:
                // Should never happen
                return this;
        }
    }
}
