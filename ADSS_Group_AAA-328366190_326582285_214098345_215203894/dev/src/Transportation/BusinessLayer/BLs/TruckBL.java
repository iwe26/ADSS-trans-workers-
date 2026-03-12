package Transportation.BusinessLayer.BLs;

import Transportation.BusinessLayer.Resources.LicenseType;
import Transportation.DAL.DAO.TruckDAO;
import Transportation.DAL.DAO.TruckTimeIntervalsDAO;
import Transportation.DAL.DTO.TruckDTO;
import Transportation.DAL.DTO.TruckTimeIntervalDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TruckBL {
    private final TruckDAO truckDAO;
    private final TruckTimeIntervalsDAO intervalsDAO;
    private TruckDTO truckDTO;
    private final List<TruckTimeIntervalDTO> intervals = new ArrayList<>();

    // Constructor for loading from database
    public TruckBL(TruckDTO truckDTO, List<TruckTimeIntervalDTO> intervalDTOs,
                   TruckDAO truckDAO, TruckTimeIntervalsDAO intervalsDAO) {
        this.truckDTO = truckDTO;
        this.truckDAO = truckDAO;
        this.intervalsDAO = intervalsDAO;
        this.intervals.addAll(intervalDTOs);
    }

    // Constructor for creating new Truck
    public TruckBL(String licensePlate, double netWeight, double maxWeight, LicenseType licenseType,
                   TruckDAO truckDAO, TruckTimeIntervalsDAO intervalsDAO) {
        this.truckDAO = truckDAO;
        this.intervalsDAO = intervalsDAO;
        this.truckDTO = new TruckDTO(licensePlate, netWeight, maxWeight, licenseType.ordinal());
        persistTruck(); // Insert into DB immediately
    }

    // -- Persistence Helpers --

    private void persistTruck() {
        try {
            truckDAO.insert(truckDTO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist truck: " + e.getMessage());
        }
    }

    private void updateTruck() {
        try {
            truckDAO.update(truckDTO);  // assumes you have an `update()` method
        } catch (Exception e) {
            throw new RuntimeException("Failed to update truck: " + e.getMessage());
        }
    }

    private void persistInterval(TruckTimeIntervalDTO dto) {
        try {
            intervalsDAO.insert(dto.getTruckLicense(), dto.getDatetime1(), dto.getDatetime2());
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist interval: " + e.getMessage());
        }
    }

    private void deleteInterval(TruckTimeIntervalDTO dto) {
        try {
            intervalsDAO.delete(dto.getTruckLicense(), dto.getDatetime1(), dto.getDatetime2());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete interval: " + e.getMessage());
        }
    }

    // -- Business Logic --

    public void addBusyInterval(LocalDateTime start, LocalDateTime end) {
        TruckTimeIntervalDTO dto = new TruckTimeIntervalDTO(truckDTO.getLicensePlate(), start, end);
        intervals.add(dto);
        persistInterval(dto);
    }

    public void removeBusyInterval(LocalDateTime start, LocalDateTime end) {
        TruckTimeIntervalDTO match = null;
        for (TruckTimeIntervalDTO dto : intervals) {
            if (dto.getDatetime1().equals(start) && dto.getDatetime2().equals(end)) {
                match = dto;
                break;
            }
        }
        if (match != null) {
            intervals.remove(match);
            deleteInterval(match);
        }
    }

    public boolean isAvailableFor(LocalDateTime newStart, LocalDateTime newEnd) {
        for (TruckTimeIntervalDTO interval : intervals) {
            if (!(newEnd.isBefore(interval.getDatetime1()) || newStart.isAfter(interval.getDatetime2()))) {
                return false; // Overlap
            }
        }
        return true;
    }

    // -- Field Access --

    public double getAvailableLoadWeight() {
        return truckDTO.getMaxWeight() - truckDTO.getNetWeight();
    }

    public String getLicensePlate() {
        return truckDTO.getLicensePlate();
    }

    public double getNetWeight() {
        return truckDTO.getNetWeight();
    }

    public double getMaxWeight() {
        return truckDTO.getMaxWeight();
    }

    public void setRequiredLicenseType(LicenseType licenseType) {
        if (truckDTO.getRequiredLicense() != licenseType.ordinal()) {
            truckDTO = new TruckDTO(
                    truckDTO.getLicensePlate(),
                    truckDTO.getNetWeight(),
                    truckDTO.getMaxWeight(),
                    licenseType.ordinal()
            );
            updateTruck();
        }
    }

    public LicenseType getRequiredLicenseType() {
        return LicenseType.values()[truckDTO.getRequiredLicense()];
    }

    public boolean canCarryWeightBetween(List<StopBL> allStops, StopBL from, StopBL to, double addedWeight) {
        double currentLoad = 0;
        boolean inSegment = false;
        for (StopBL stop : allStops) {
            if (stop.equals(from)) inSegment = true;
            if (inSegment) currentLoad += addedWeight;
            if (stop.equals(to)) break;
            if (currentLoad > truckDTO.getMaxWeight()) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Truck " + getLicensePlate() +
                " | Net Weight: " + getNetWeight() +
                " | Max Weight: " + getMaxWeight() +
                " | Can Carry: " + getAvailableLoadWeight() +
                " | Required License: " + getRequiredLicenseType();
    }
}
