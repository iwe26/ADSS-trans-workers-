package Transportation.ServiceLayer;

import Transportation.BusinessLayer.BLs.TruckBL;
import Transportation.BusinessLayer.Resources.LicenseType;
import Transportation.BusinessLayer.Resources.Response;
import Transportation.DAL.DAO.TruckDAO;
import Transportation.DAL.DAO.TruckTimeIntervalsDAO;
import Transportation.DAL.DTO.TruckDTO;
import Transportation.DAL.DTO.TruckTimeIntervalDTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TruckService {
    private final Map<String, TruckBL> truckMap;
    private final TruckDAO truckDAO;
    private final TruckTimeIntervalsDAO truckTimeIntervalsDAO;

    public TruckService(Map<String, TruckBL> truckMap) {
        this.truckMap = truckMap;
        this.truckDAO = new TruckDAO();
        this.truckTimeIntervalsDAO = new TruckTimeIntervalsDAO();
        loadData();
    }

    public Response addTruck(String licensePlate, double netWeight, double maxWeight, LicenseType licenseType) {
        try {
            if (truckMap.containsKey(licensePlate)) {
                throw new IllegalArgumentException("Truck already exists.");
            }
            TruckBL truck = new TruckBL(licensePlate, netWeight, maxWeight, licenseType, truckDAO, truckTimeIntervalsDAO);
            truckMap.put(licensePlate, truck);
            return new Response("Truck added successfully.", null);
        } catch (Exception e) {
            return new Response(null, "Failed to add truck: " + e.getMessage());
        }
    }

    public Response getTruck(String licensePlate) {
        try {
            TruckBL truck = truckMap.get(licensePlate);
            if (truck == null) {
                return new Response(null, "Truck not found.");
            }
            return new Response(truck, null);
        } catch (Exception e) {
            return new Response(null, "Error retrieving truck: " + e.getMessage());
        }
    }

    public Response removeTruck(String licensePlate) {
        try {
            if (!truckMap.containsKey(licensePlate)) {
                throw new IllegalArgumentException("Truck not found.");
            }
            truckMap.remove(licensePlate);
            // Consider deleting from DB if needed
            return new Response(true, null);
        } catch (Exception e) {
            return new Response(null, "Error removing truck: " + e.getMessage());
        }
    }

    public Response setTruckLicenseType(String licensePlate, LicenseType licenseType) {
        try {
            TruckBL truck = truckMap.get(licensePlate);
            if (truck != null) {
                truck.setRequiredLicenseType(licenseType);
            }
            return new Response("License type updated.", null);
        } catch (Exception e) {
            return new Response(null, "Error setting license type: " + e.getMessage());
        }
    }

    public Response getTruckLicenseType(String licensePlate) {
        try {
            TruckBL truck = truckMap.get(licensePlate);
            if (truck == null) {
                return new Response(null, "Truck not found.");
            }
            return new Response(truck.getRequiredLicenseType(), null);
        } catch (Exception e) {
            return new Response(null, "Error retrieving license type: " + e.getMessage());
        }
    }

    public Response getAvailableTrucks(LocalDateTime from, LocalDateTime to) {
        try {
            List<String> availableTrucks = truckMap.values().stream()
                    .filter(truck -> truck.isAvailableFor(from, to))
                    .map(TruckBL::toString)
                    .collect(Collectors.toList());
            return new Response(availableTrucks, null);
        } catch (Exception e) {
            return new Response(null, "Error retrieving available trucks: " + e.getMessage());
        }
    }

    public Response getAllTrucks() {
        try {
            return new Response(new ArrayList<>(truckMap.values()), null);
        } catch (Exception e) {
            return new Response(null, "Error retrieving all trucks: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            Map<String, List<TruckTimeIntervalDTO>> allIntervals = new HashMap<>();
            for (TruckTimeIntervalDTO dto : truckTimeIntervalsDAO.getAll()) {
                allIntervals.computeIfAbsent(dto.getTruckLicense(), k -> new ArrayList<>()).add(dto);
            }

            for (TruckDTO truckDTO : truckDAO.getAll()) {
                List<TruckTimeIntervalDTO> intervals = allIntervals.getOrDefault(truckDTO.getLicensePlate(), new ArrayList<>());
                TruckBL truckBL = new TruckBL(truckDTO, intervals, truckDAO, truckTimeIntervalsDAO);
                truckMap.put(truckDTO.getLicensePlate(), truckBL);
            }
        } catch (Exception e) {
            System.err.println("Error while loading truck data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Collection<TruckBL> getAllTrucksRaw() {
        return truckMap.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TruckBL truck : truckMap.values()) {
            sb.append(truck.toString())
                    .append(" | Can Carry Weight: ").append(truck.getAvailableLoadWeight())
                    .append("\n");
        }
        return sb.toString();
    }
}
