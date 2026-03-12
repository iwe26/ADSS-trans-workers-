package Transportation.PresentationLayer;

import Transportation.BusinessLayer.BLs.TransportationItemBL;
import Transportation.BusinessLayer.BLs.TruckBL;
import Transportation.BusinessLayer.Resources.*;
import Transportation.ServiceLayer.*;
import hrmanagement.domain.facade.*;
import hrmanagement.service.TransportationHRService;
import hrmanagement.service.impl.TransportationHRServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

public class Application {



    private static final Scanner scanner = new Scanner(System.in);
    private static  TransportationServiceFactory transportationServiceFactory;


/*    public Application(SiteService siteService)
    {
        transportationServiceFactory = new TransportationServiceFactory(siteService);
    }*/

    public Application(SiteService sharedSiteService, ShiftFacade shiftFacade, ShiftAssignmentFacade shiftAssignmentFacade, RoleFacade roleFacade, EmployeeRoleFacade employeeRoleFacade, EmployeeFacade employeeFacade, EmployeeAvailabilityFacade availabilityFacade) {
        TransportationHRService transportationHRService = new TransportationHRServiceImpl(shiftFacade, shiftAssignmentFacade, roleFacade, employeeRoleFacade, employeeFacade, availabilityFacade);
        transportationServiceFactory = new TransportationServiceFactory(sharedSiteService, transportationHRService);
    }

    public static void launch() {
        while (true) {
            System.out.println("\n=== Transportation System ===");
            System.out.println("Select Role:");
            System.out.println("1. Admin");
            System.out.println("2. Transportation Manager");
            System.out.println("3. Driver");
            System.out.println("4. Exit");

            int choice = readInt();

            switch (choice) {
                case 1 -> launchAdmin();
                case 2 -> launchTransportationManager();
                case 3 -> {
                    System.out.print("Enter Driver ID: ");
                    int id = readInt();
                    if (isValidDriver(id)) {
                        launchDriver(id);
                    } else {
                        System.out.println("Invalid driver ID.");
                    }
                }
                case 4 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void launchAdmin() {
        showAdminMenu();
    }

    public static void launchTransportationManager() {
        showManagerMenu();
    }

    public static void launchDriver(int driverId) {
        showDriverMenu(driverId);
    }

    public static boolean isValidDriver(int id) {
        Response res = transportationServiceFactory._driverService.getDriver(id);
        return res.getErrorMessage() == null;
    }

    private static void showDriverMenu(int driverId) {
        while (true) {
            System.out.println("\n=== Driver Menu ===");
            System.out.println("1. Add Comment");
            System.out.println("2. Back to Main Menu");
            int choice = readInt();

            switch (choice) {
                case 1 -> addComment(driverId);
                case 2 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addComment(int driverId) {
        System.out.print("Enter Transportation ID: ");
        int transportationId = readInt();
        System.out.print("Comment: ");
        String comment = scanner.nextLine();
        Response res = transportationServiceFactory._transportationService.addComment(transportationId, String.valueOf(driverId), comment);
        System.out.println(res.getErrorMessage() == null ? "Comment added." : res.getErrorMessage());
    }

    private static void showManagerMenu() {
        while (true) {
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. Order Transportation");
            System.out.println("2. Show Pending Transportations");
            System.out.println("3. Show All Transportations");
            System.out.println("4. Assign Order to Existing Transportation");
            System.out.println("5. Back to Main Menu");
            int choice = readInt();
            switch (choice) {
                case 1 -> orderTransportation();
                case 2 -> showPendingAndReschedule();
                case 3 -> showAll();
                case 4 -> assignOrderToExistingTransportation();
                case 5 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void showAdminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add Truck");
            System.out.println("2. Remove Truck");
            System.out.println("3. Add Driver License");
            System.out.println("4. Order Transportation");
            System.out.println("5. Show All Transportations");
            System.out.println("6. Show Pending Transportations");
            System.out.println("7. Assign Order to Existing Transportation");
            System.out.println("8. Back to Main Menu");
            int choice = readInt();
            switch (choice) {
                case 1 -> addTruck();
                case 2 -> removeTruck();
                case 3 -> addDriver();
                case 4 -> orderTransportation();
                case 5 -> showAll();
                case 6 -> showPendingAndReschedule();
                case 7 -> assignOrderToExistingTransportation();
                case 8 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addDriver() {
        System.out.print("Enter Employee ID of the driver: ");
        int id = readInt();
        System.out.print("Enter License Type (B/C/D/E): ");
        LicenseType type = LicenseType.valueOf(scanner.nextLine().toUpperCase());
        Response res = transportationServiceFactory._driverService.addLicenseToDriver(id, type);
        System.out.println(res.getErrorMessage() == null ? "Driver added." : res.getErrorMessage());
    }

    private static void addTruck() {
        System.out.print("License Plate: ");
        String license = scanner.nextLine();
        System.out.print("Net Weight: ");
        double net = Double.parseDouble(scanner.nextLine());
        System.out.print("Max Load: ");
        double max = Double.parseDouble(scanner.nextLine());
        System.out.print("License Type (B/C/D/E): ");
        LicenseType type = LicenseType.valueOf(scanner.nextLine().toUpperCase());

        Response res = transportationServiceFactory._truckService.addTruck(license, net, max, type);
        System.out.println(res.getErrorMessage() == null ? "Truck added." : res.getErrorMessage());
    }

    private static void removeTruck() {
        System.out.print("License Plate to remove: ");
        String plate = scanner.nextLine();
        Response res = transportationServiceFactory._truckService.removeTruck(plate);
        System.out.println(res.getErrorMessage() == null ? "Truck removed." : res.getErrorMessage());
    }

    private static void orderTransportation() {
        System.out.println("=== Create New Transportation ===");
        String sourceAddress = chooseSiteAddress("Choose Source Site:");
        LocalDateTime sourceTime = readDateTime("Enter Source Arrival DateTime (yyyy-MM-dd HH:mm):");
        String destinationAddress = chooseSiteAddress("Choose Destination Site:");
        LocalDateTime destinationTime = readDateTime("Enter Destination Arrival DateTime (yyyy-MM-dd HH:mm):");

        Response truckResponse = transportationServiceFactory._truckService.getAvailableTrucks(sourceTime, destinationTime);
        List<String> trucks = (List<String>) truckResponse.getReturnValue();
        if (trucks == null || trucks.isEmpty()) {
            System.out.println("No available trucks.");
            storePendingAndReturn(null, sourceAddress, destinationAddress, sourceTime, destinationTime);
            return;
        }

        System.out.println("Available Trucks:");
        for (int i = 0; i < trucks.size(); i++) System.out.println((i + 1) + ". " + trucks.get(i));
        System.out.print("Choose Truck: ");
        String truckLicense = trucks.get(readInt() - 1).split(" ")[1];

        TruckBL selectedTruck = (TruckBL) (transportationServiceFactory._truckService.getTruck(truckLicense).getReturnValue());
        if (selectedTruck == null) {
            System.out.println("Invalid truck selected.");
            return;
        }

        // Check if any available driver has the required license
        LicenseType requiredLicense = selectedTruck.getRequiredLicenseType();
        Response driverResponse = transportationServiceFactory._driverService.getAvailableDriverNamesWithLicenses(sourceTime, destinationTime);
        List<String> allDrivers = (List<String>) driverResponse.getReturnValue();

        List<String> matchingDrivers = new ArrayList<>();
        for (String driverStr : allDrivers) {
            // Example format: "1. ID: 12 - Name: John Doe - Licenses: [B, C, D]"
            int licenseStart = driverStr.indexOf("Licenses: [");
            int licenseEnd = driverStr.indexOf("]", licenseStart);
            if (licenseStart != -1 && licenseEnd != -1) {
                String licensesSection = driverStr.substring(licenseStart + "Licenses: [".length(), licenseEnd);
                List<String> licenses = Arrays.asList(licensesSection.split(",\\s*"));
                if (licenses.contains(requiredLicense.toString())) {
                    matchingDrivers.add(driverStr);
                }
            }
        }

        if (matchingDrivers.isEmpty()) {
            System.out.println("❌ No available driver has the required license for this truck.");
            storePendingAndReturn(null, sourceAddress, destinationAddress, sourceTime, destinationTime);
            return;
        }

        System.out.println("Available Drivers:");
        for (int i = 0; i < matchingDrivers.size(); i++) System.out.println((i + 1) + ". " + matchingDrivers.get(i));
        System.out.print("Choose Driver: ");
        int driverId = Integer.parseInt(matchingDrivers.get(readInt() - 1).split(":")[1].split("-")[0].trim());

        System.out.print("Enter number of items: ");
        int count = readInt();
        List<TransportationItemBL> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Item name: ");
            String name = scanner.nextLine();
            System.out.print("Item weight: ");
            double weight = Double.parseDouble(scanner.nextLine());
            items.add(new TransportationItemBL(name, weight));
        }

        Response res = transportationServiceFactory._transportationService.createTransportation(
                driverId, truckLicense, items, sourceAddress, destinationAddress, sourceTime, destinationTime
        );

        System.out.println(res.getErrorMessage() == null ? "Transportation created." : "Error: " + res.getErrorMessage());
    }


    private static void storePendingAndReturn(List<TransportationItemBL> items, String sourceAddress,
                                              String destinationAddress, LocalDateTime sourceTime,
                                              LocalDateTime destinationTime) {
        if (items == null) {
            items = new ArrayList<>();
            System.out.print("Enter number of items: ");
            int count = readInt();
            for (int i = 0; i < count; i++) {
                System.out.print("Item name: ");
                String name = scanner.nextLine();
                System.out.print("Item weight: ");
                double weight = Double.parseDouble(scanner.nextLine());
                items.add(new TransportationItemBL(name, weight));
            }
        }

        transportationServiceFactory._transportationService.addPendingTransportation(
                items, sourceAddress, destinationAddress, sourceTime, destinationTime
        );
        System.out.println("Transportation stored as pending.");
    }

    private static void showAll() {
        Response res = transportationServiceFactory._transportationService.getAllTransportations();
        printResponse(res);
    }

    private static void showPendingAndReschedule() {
        Response res = transportationServiceFactory._transportationService.getPendingTransportations();
        List<String> list = (List<String>) res.getReturnValue();
        if (list == null || list.isEmpty()) {
            System.out.println("No pending transportations.");
            return;
        }

        System.out.println("Pending Transportations:");
        for (int i = 0; i < list.size(); i++) System.out.println((i + 1) + ". " + list.get(i));
        System.out.print("Choose one to reschedule (0 to cancel): ");
        int index = readInt();
        if (index == 0) return;

        LocalDateTime newSource = readDateTime("New source time:");
        LocalDateTime newDest = readDateTime("New destination time:");

        Response truckRes = transportationServiceFactory._truckService.getAvailableTrucks(newSource, newDest);
        List<String> trucks = (List<String>) truckRes.getReturnValue();
        if (trucks.isEmpty()) {
            System.out.println("No trucks available.");
            return;
        }
        for (int i = 0; i < trucks.size(); i++) System.out.println((i + 1) + ". " + trucks.get(i));
        System.out.print("Choose Truck: ");
        String truck = trucks.get(readInt() - 1).split(" ")[1];

        Response driverRes = transportationServiceFactory._driverService.getAvailableDriverNamesWithLicenses(newSource, newDest);
        List<String> drivers = (List<String>) driverRes.getReturnValue();
        if (drivers.isEmpty()) {
            System.out.println("No drivers available.");
            return;
        }
        for (int i = 0; i < drivers.size(); i++) System.out.println((i + 1) + ". " + drivers.get(i));
        System.out.print("Choose Driver: ");
        int driverId = Integer.parseInt(drivers.get(readInt() - 1).split(":")[1].split("-")[0].trim());

        Response res2 = transportationServiceFactory._transportationService.reschedulePendingWithResources(
                index - 1, newSource, newDest, truck, driverId
        );

        System.out.println(res2.getErrorMessage() == null ? "Rescheduled successfully." : res2.getErrorMessage());
    }

    private static void assignOrderToExistingTransportation() {
        System.out.println("\n=== Assign Order to Existing Transportation ===");

        // Select Source Site
        String sourceAddress = chooseSiteAddress("Choose Order Source Site:");
        LocalDateTime sourceTime = readDateTime("Enter Order Source Arrival Time (yyyy-MM-dd HH:mm):");

        // Select Destination Site
        String destAddress = chooseSiteAddress("Choose Order Destination Site:");
        LocalDateTime destTime = readDateTime("Enter Order Destination Arrival Time (yyyy-MM-dd HH:mm):");

        // Enter items
        System.out.print("Enter number of items to assign: ");
        int count = readInt();
        List<TransportationItemBL> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Item name: ");
            String name = scanner.nextLine();
            System.out.print("Item weight: ");
            double weight = Double.parseDouble(scanner.nextLine());
            items.add(new TransportationItemBL(name, weight));
        }

        // Fetch assignable transportations
        Response response = transportationServiceFactory._transportationService.findAssignableTransportations(sourceAddress, sourceTime, destAddress, destTime,items);
        Map<Integer, String> transportOptions = (Map<Integer, String>) response.getReturnValue();

        if (transportOptions == null || transportOptions.isEmpty()) {
            System.out.println("No suitable transportation found for assignment.");
            return;
        }

        // Display options
        System.out.println("Matching In-Progress Transportations:");
        List<Integer> transportIds = new ArrayList<>(transportOptions.keySet());
        for (int i = 0; i < transportIds.size(); i++) {
            int tid = transportIds.get(i);
            System.out.println((i + 1) + ". ID: " + tid + " - " + transportOptions.get(tid));
        }

        System.out.print("Select Transportation to Assign (or 0 to cancel): ");
        int selected = readInt();
        if (selected == 0) return;

        int chosenId = transportIds.get(selected - 1);

        // Assign using service
        Response assignResult = transportationServiceFactory._transportationService.assignOrderToExistingTransportation(
                chosenId, sourceAddress, sourceTime, destAddress, destTime, items);

        if (assignResult.getErrorMessage() != null) {
            System.out.println("❌ Error: " + assignResult.getErrorMessage());
        } else {
            System.out.println("✅ Order assigned successfully.");
        }
    }


    private static String chooseSiteAddress(String prompt) {
        Response res = transportationServiceFactory._siteService.getAllSitesSummary();
        List<String> sites = (List<String>) res.getReturnValue();
        System.out.println(prompt);
        for (int i = 0; i < sites.size(); i++) System.out.println((i + 1) + ". " + sites.get(i));
        int choice = readInt();
        String selected = sites.get(choice - 1);
        return selected.substring(selected.indexOf("Address: ") + 8, selected.indexOf(", Contact:")).trim();
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input, try again:");
            }
        }
    }

    private static LocalDateTime readDateTime(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " ");
                String input = scanner.nextLine();
                return LocalDateTime.parse(input.replace(" ", "T"));
            } catch (Exception e) {
                System.out.println("Invalid format. Please use yyyy-MM-dd HH:mm");
            }
        }
    }

    private static void printResponse(Response res) {
        if (res.getErrorMessage() != null) {
            System.out.println("Error: " + res.getErrorMessage());
        } else {
            ((List<String>) res.getReturnValue()).forEach(System.out::println);
        }
    }
}
