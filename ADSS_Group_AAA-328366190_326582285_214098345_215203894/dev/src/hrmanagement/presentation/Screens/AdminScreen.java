package hrmanagement.presentation.Screens;

import Transportation.BusinessLayer.Resources.Response;
import Transportation.ServiceLayer.SiteService;
import hrmanagement.presentation.console.Console;
import hrmanagement.presentation.controller.AdminController;

import java.util.List;

public class AdminScreen extends Screen{
    private  AdminController controller;


    public void setController(SiteService siteService) {
        this.controller = new AdminController(siteService);
    }

    private final List<String> options = List.of(
            "Add site","Remove site","Exit");

    public AdminScreen(Console console) {
        super(console);
    }


    @Override
    public void show() {
        console.println("\nSelect your action:");
        for (int i = 0; i < options.size(); i++) {
            console.println((i + 1) + ". " + options.get(i));
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

        if (choice < 1 || choice > options.size()) {
            console.println("Choice out of range. Try again.");
            return this;
        }

        String selected = options.get(choice - 1);
        switch (selected) {
            case "Add site":
                //add a new site to database
                addSite();
                return this;
            case "Remove site":
                //remove an existing site from database
                removeSite();
                return this;
            case "Exit":
                console.println("Exiting to role selection...");
                return new RoleSelectionScreen(console);
            default:
                // Should never happen
                return this;
        }
    }
    private void addSite(){
        console.println("Enter site name:");
        String name = console.readLine().trim();
        console.println("Enter site address:");
        String address = console.readLine().trim();
        console.println("Enter contact's name:");
        String contactName = console.readLine().trim();
        console.println("Enter contact's phone number:");
        String phone = console.readLine().trim();
        if (address.isEmpty() || contactName.isEmpty() || phone.isEmpty()) {
            console.println("All fields are required.");
            return;
        }

        //enter zone
        console.println("Enter transportation zone (EAST" +
                "    WEST," +
                "    NORTH," +
                "    SOUTH," +
                "    CENTER):");
        String zoneInput = console.readLine().trim().toUpperCase();
        Transportation.BusinessLayer.Resources.TransportationZone zone;
        try {
            zone = Transportation.BusinessLayer.Resources.TransportationZone.valueOf(zoneInput);
        } catch (IllegalArgumentException e) {
            console.println("Invalid transportation zone. Please try again.");
            return;
        }


        Response response = controller.addSite(name,address,contactName,phone,zone);
        printResponse(response);
    }
    private void removeSite(){
        console.println("Existing sites:\n" + controller.getAllSiteAddresses() );
        console.println("Enter site address:");
        String address = console.readLine().trim();
        Response response = controller.removeSite(address);
        if (response.getReturnValue() == null || !(response.getReturnValue() instanceof Boolean) || !(Boolean) response.getReturnValue() ) {
            console.println("Site not found or could not be removed.");
            return;
        }
        console.println("Site removed successfully.");
    }

    private void printResponse(Response response){
        if (response.getErrorMessage() != null)
        {
            console.println(response.getErrorMessage());
            return;
        }
        if (response.getReturnValue() != null)
        {
            console.println(response.getReturnValue().toString());
        }
    }
}
