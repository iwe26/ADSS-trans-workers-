package Transportation.ServiceLayer;

import Transportation.BusinessLayer.BLs.*;
import Transportation.BusinessLayer.Resources.Response;
import Transportation.BusinessLayer.Resources.TransportationStatus;
import Transportation.DAL.DAO.TransportationDAO;
import Transportation.DAL.DTO.TransportationDTO;
import hrmanagement.dal.dto.SiteDTO;
import hrmanagement.service.TransportationHRService;
import hrmanagement.service.impl.TransportationHRServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

public class TransportationService {

    private final Map<Integer, TransportationBL> transports = new HashMap<>();
    private int nextId = 1;

    private final Map<String, TruckBL> trucks;
    private final Map<Integer, DriverBL> drivers;
    private final SiteService siteService;
    private final TransportationHRService hrService;

    public TransportationService(Map<Integer, DriverBL> drivers,
                                 Map<String, TruckBL> trucks,
                                 SiteService siteService, TransportationHRService hrService) {
        this.drivers = drivers;
        this.trucks = trucks;
        this.siteService = siteService;
        this.hrService = hrService;
        try {
            this.loadFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response createTransportation(int driverId,
                                         String truckLicense,
                                         List<TransportationItemBL> items,
                                         String orderSourceAddress,
                                         String orderDestAddress,
                                         LocalDateTime sourceTime,
                                         LocalDateTime destinationTime) {
        try {
            Scanner scanner = new Scanner(System.in);

            Response srcResp = siteService.getSite(orderSourceAddress);
            Response destResp = siteService.getSite(orderDestAddress);
            if (srcResp.getErrorMessage() != null || destResp.getErrorMessage() != null) {
                return new Response(null, "Invalid source or destination.");
            }

            SiteDTO orderSource = (SiteDTO) srcResp.getReturnValue();
            SiteDTO orderDest = (SiteDTO) destResp.getReturnValue();

            while (true) {
                TruckBL truck = trucks.get(truckLicense);
                DriverBL driver = drivers.get(driverId);

                if (driver == null || truck == null) {
                    return new Response(null, "Invalid driver or truck.");
                }

                double totalWeight = items.stream().mapToDouble(TransportationItemBL::getTotalWeight).sum();
                if (totalWeight > truck.getAvailableLoadWeight()) {
                    System.out.println("❗ Items exceed truck's capacity. Choose an option:");
                    System.out.println("1. Change truck\n2. Remove items\n3. Abort");
                    int choice = Integer.parseInt(scanner.nextLine().trim());

                    if (choice == 1) {
                        truckLicense = null;
                        List<String> available = new ArrayList<>();
                        for (TruckBL t : trucks.values()) {
                            if (t.isAvailableFor(sourceTime, destinationTime) &&
                                    t.getAvailableLoadWeight() >= totalWeight) {
                                available.add(t.getLicensePlate());
                            }
                        }
                        if (available.isEmpty()) {
                            return new Response(null, "No suitable trucks found.");
                        }
                        for (int i = 0; i < available.size(); i++) {
                            System.out.println((i + 1) + ". " + available.get(i));
                        }
                        System.out.print("Choose truck: ");
                        int tIndex = Integer.parseInt(scanner.nextLine().trim());
                        truckLicense = available.get(tIndex - 1);
                        continue;
                    } else if (choice == 2) {
                        while (true) {
                            for (int i = 0; i < items.size(); i++) {
                                System.out.println((i + 1) + ". " + items.get(i).getName() + " - " + items.get(i).getTotalWeight() + "kg");
                            }
                            System.out.print("Enter item index to remove (or 0 to stop): ");
                            int removeIndex = Integer.parseInt(scanner.nextLine().trim());
                            if (removeIndex == 0) break;
                            if (removeIndex > 0 && removeIndex <= items.size()) {
                                items.remove(removeIndex - 1);
                            }
                            totalWeight = items.stream().mapToDouble(TransportationItemBL::getTotalWeight).sum();
                            if (totalWeight <= truck.getAvailableLoadWeight()) break;
                        }
                        continue;
                    } else {
                        return new Response(null, "Process aborted.");
                    }
                }

                if (!driver.hasLicense(truck.getRequiredLicenseType())) {
                    return new Response(null, "Driver lacks required license.");
                }

                if (!orderSource.getZone().equals(orderDest.getZone())) {
                    System.out.print("Zones differ. Continue? (yes/no): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (!confirm.equals("yes")) {
                        return new Response(null, "Cancelled due to zone mismatch.");
                    }
                }

                if (!hrService.isWarehouseEmployeeAssignToTransportation(destinationTime,((SiteDTO) destResp.getReturnValue()).getAddress())) {
                    System.out.print("⚠️ No warehouse worker at destination time. Continue anyway? (yes/no): ");
                    if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        return new Response(null, "Cancelled due to missing warehouse worker.");
                    }
                }
                if (!hrService.assignDriverToShift(driverId, sourceTime,((SiteDTO) srcResp.getReturnValue()).getAddress())) {
                    return new Response(null,"Warning: Failed to mark driver as unavailable.");
                }
                if (!hrService.assignDriverToShift(driverId, destinationTime,((SiteDTO) destResp.getReturnValue()).getAddress())) {
                    return new Response(null,"Warning: Failed to mark driver as unavailable.");
                }
                int id = nextId++;
                TransportationBL tx = new TransportationBL(id, driver, truck, items, orderSource, orderDest, sourceTime, destinationTime);
                transports.put(id, tx);

                if (tx.getStatus() == TransportationStatus.PENDING && truck != null) {
                    truck.removeBusyInterval(sourceTime, destinationTime);
                }

                return new Response(tx.toString(), null);
            }
        } catch (Exception e) {
            return new Response(null, "Error: " + e.getMessage());
        }
    }

    public void addPendingTransportation(List<TransportationItemBL> items, String source, String destination,
                                         LocalDateTime sourceTime, LocalDateTime destinationTime) {
        Response src = siteService.getSite(source);
        Response dst = siteService.getSite(destination);
        if (src.getErrorMessage() != null || dst.getErrorMessage() != null) return;

        SiteDTO s = (SiteDTO) src.getReturnValue();
        SiteDTO d = (SiteDTO) dst.getReturnValue();
        int id = nextId++;
        TransportationBL pending = new TransportationBL(id, null, null, items, s, d, sourceTime, destinationTime);
        transports.put(id, pending);
    }

    public Response reschedulePendingWithResources(int index, LocalDateTime newSourceTime, LocalDateTime newDestTime,
                                                   String truckLicense, int driverId) {
        List<TransportationBL> pending = new ArrayList<>();
        for (TransportationBL t : transports.values()) {
            if (t.getStatus() == TransportationStatus.PENDING) pending.add(t);
        }
        if (index < 0 || index >= pending.size()) return new Response(null, "Invalid index.");

        TransportationBL tx = pending.get(index);
        SiteDTO src = tx.getStops().getFirst().getSite();
        SiteDTO dst = tx.getStops().getLast().getSite();

        if (!src.getZone().equals(dst.getZone())) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Zone mismatch. Continue? (yes/no): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                return new Response(null, "User cancelled due to zone mismatch.");
            }
        }

        if (!hrService.isWarehouseEmployeeAssignToTransportation(newDestTime,tx.getDestPending().getAddress())) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("⚠️ No warehouse worker at destination time. Continue anyway? (yes/no): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                return new Response(null, "Cancelled due to missing warehouse worker.");
            }
        }

        TruckBL truck = trucks.get(truckLicense);
        DriverBL driver = drivers.get(driverId);
        if (truck == null || driver == null) return new Response(null, "Invalid driver or truck.");
        if (!truck.isAvailableFor(newSourceTime, newDestTime)) return new Response(null, "Truck unavailable.");

        if (!hrService.assignDriverToShift(driverId, newSourceTime,tx.getSourcePending().getAddress())) {
            return new Response(null,"Failed to mark driver unavailable");
        }

        // Remove previous truck busy interval if any
        if (tx.getTruck() != null) {
            tx.getTruck().removeBusyInterval(tx.getDto().getSourceTime(), tx.getDto().getDestinationTime());
        }

        truck.addBusyInterval(newSourceTime, newDestTime);

        // Update fields
        tx.setTruck(truck);
        tx.setDriver(driver);
        tx.setSourceTime(newSourceTime);
        tx.setDestinationTime(newDestTime);
        tx.setStatus(TransportationStatus.REGISTERED);

        // Persist to database
        tx.getDto().setTruckLicensePlate(truck.getLicensePlate());
        tx.getDto().setDriverId(driver.getEmployeeId());
        tx.getDto().setSourceTime(newSourceTime);
        tx.getDto().setDestinationTime(newDestTime);
        try
        {
            tx.getTransportationDAO().insertOrUpdate(tx.getDto());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new Response("Transportation rescheduled and saved successfully.", null);
    }


    public void loadFromDatabase() {
        try {
            TransportationDAO dao = new TransportationDAO();
            List<TransportationDTO> allList = dao.getAll();
            Map<Integer, TransportationDTO> all = new HashMap<>();
            for (TransportationDTO dto : allList) {
                all.put(dto.getId(), dto);
            }

            for (TransportationDTO dto : all.values()) {
                int id = dto.getId();
                DriverBL driver = dto.getDriverId() != null ? drivers.get(dto.getDriverId()) : null;
                TruckBL truck = trucks.get(dto.getTruckLicensePlate());
                Map<String, SiteDTO> siteMap = new HashMap<>();
                for (SiteDTO site : siteService.getAllSitesRaw()) {
                    siteMap.put(site.getAddress(), site);
                }

                TransportationBL tx = new TransportationBL(dto, driver, truck, siteMap);
                transports.put(id, tx);
                nextId = Math.max(nextId, id + 1);
            }
        } catch (Exception e) {
            System.err.println("Failed to load transportations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Response getAllTransportations() {
        List<String> result = new ArrayList<>();
        for (TransportationBL tx : transports.values()) {
            result.add(tx.toString());
        }
        return new Response(result, null);
    }

    public Response addComment(int id, String user, String comment) {
        try {
            TransportationBL tx = transports.get(id);
            if (tx == null) return new Response(null, "Not found.");
            tx.updateComment(comment);
            return new Response("Comment updated and saved.", null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Response getPendingTransportations() {
        List<String> results = new ArrayList<>();
        for (TransportationBL tx : transports.values()) {
            if (tx.getStatus() == TransportationStatus.PENDING) {
                List<StopBL> stops = tx.getStops();
                if (stops.size() < 2) continue;
                results.add(String.format("From %s to %s [%s → %s]",
                        stops.get(0).getSite().getAddress(),
                        stops.getLast().getSite().getAddress(),
                        stops.get(0).getArrivalTime(),
                        stops.getLast().getArrivalTime()));
            }
        }
        return new Response(results, null);
    }

    public Response assignOrderToExistingTransportation(int transportationId,
                                                        String sourceAddress,
                                                        LocalDateTime sourceTime,
                                                        String destAddress,
                                                        LocalDateTime destTime,
                                                        List<TransportationItemBL> items) {
        try {
            // Convert source/destination address strings to SiteDTOs
            Response srcResp = siteService.getSite(sourceAddress);
            Response dstResp = siteService.getSite(destAddress);

            if (srcResp.getErrorMessage() != null || dstResp.getErrorMessage() != null) {
                return new Response(null, "Invalid source or destination address.");
            }

            SiteDTO src = (SiteDTO) srcResp.getReturnValue();
            SiteDTO dst = (SiteDTO) dstResp.getReturnValue();

            // Retrieve the transportation
            TransportationBL tx = transports.get(transportationId);
            if (tx == null) {
                return new Response(null, "Transportation not found.");
            }

            // Validate if this transportation can accept the order
            if (!tx.canAssignBetween(src, sourceTime, dst, destTime, items)) {
                return new Response(null, "Transportation cannot accept this order.");
            }

            // If zone mismatch, confirm
            if (!src.getZone().equals(dst.getZone())) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Zone mismatch between source and destination. Continue? (yes/no): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                    return new Response(null, "Assignment cancelled due to zone mismatch.");
                }
            }

            // Assign the order and persist
            tx.assignOrder(src, sourceTime, dst, destTime, items);
            tx.persistStopsAndItems();  // persists items + stops to DB

            return new Response("Order assigned to transportation successfully.", null);
        } catch (Exception e) {
            return new Response(null, "Error assigning order: " + e.getMessage());
        }
    }


    public Response findAssignableTransportations(String sourceAddress, LocalDateTime sourceTime,
                                                  String destAddress, LocalDateTime destTime,
                                                  List<TransportationItemBL> items) {
        try {
            Response srcResp = siteService.getSite(sourceAddress);
            Response destResp = siteService.getSite(destAddress);
            if (srcResp.getErrorMessage() != null || destResp.getErrorMessage() != null)
                return new Response(null, "Invalid site.");

            SiteDTO source = (SiteDTO) srcResp.getReturnValue();
            SiteDTO dest = (SiteDTO) destResp.getReturnValue();

            Map<Integer, String> result = new LinkedHashMap<>();
            for (TransportationBL tx : transports.values()) {
                if (tx.getStatus() == TransportationStatus.REGISTERED &&
                        tx.canAssignBetween(source, sourceTime, dest, destTime, items)) {
                    result.put(tx.getId(), tx.toString().split("\n")[0]); // Use first line of toString for display
                }
            }

            return new Response(result, null);
        } catch (Exception e) {
            return new Response(null, "Error: " + e.getMessage());
        }
    }
}
