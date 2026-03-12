package Transportation.BusinessLayer.BLs;

import Transportation.BusinessLayer.Resources.TransportationStatus;
import Transportation.DAL.DAO.ItemAtStopDAO;
import Transportation.DAL.DAO.TransportationDAO;
import Transportation.DAL.DAO.TransportationStopsDAO;
import Transportation.DAL.DTO.ItemAtStopDTO;
import Transportation.DAL.DTO.TransportationDTO;
import hrmanagement.dal.dto.SiteDTO;

import java.time.LocalDateTime;
import java.util.*;

public class TransportationBL {
    private int id;
    private DriverBL driver;
    private TruckBL truck;
    private TransportationDocumentBL document;
    private List<StopBL> stops;
    private double currentWeight;
    private boolean diffrentZones = false;
    private TransportationStatus status;
    private TransportationDTO dto;

    private final TransportationDAO transportationDAO;
    private final TransportationStopsDAO stopsDAO;
    private final ItemAtStopDAO itemDAO;

    public TransportationBL(int id,
                            DriverBL driver,
                            TruckBL truck,
                            List<TransportationItemBL> items,
                            SiteDTO source,
                            SiteDTO destination,
                            LocalDateTime sourceTime,
                            LocalDateTime destinationTime) {
        this.id = id;
        this.driver = driver;
        this.truck = truck;
        this.document = new TransportationDocumentBL(id);
        this.stops = new ArrayList<>();
        this.currentWeight = 0;
        this.transportationDAO = new TransportationDAO();
        this.stopsDAO = new TransportationStopsDAO();
        this.itemDAO = new ItemAtStopDAO();

        if (driver != null && truck != null) {
            if (!driver.hasLicense(truck.getRequiredLicenseType())) {
                throw new IllegalArgumentException("Driver lacks required license.");
            }

            double totalWeight = items.stream().mapToDouble(TransportationItemBL::getTotalWeight).sum();
            if (totalWeight > truck.getAvailableLoadWeight()) {
                throw new IllegalStateException("Order exceeds truck capacity.");
            }

            truck.addBusyInterval(sourceTime, destinationTime);
        }

        if (!source.getZone().equals(destination.getZone())) {
            this.diffrentZones = true;
        }

        StopBL src = findOrCreateStop(source, sourceTime);
        StopBL dst = findOrCreateStop(destination, destinationTime);

        for (TransportationItemBL item : items) {
            src.addItemToPickUp(item);
            dst.addItemToDeliver(item);
            document.addItem(item);
            currentWeight += item.getTotalWeight();
        }

        document.setDepartureWeight(currentWeight + (truck != null ? truck.getNetWeight() : 0));
        this.status = (driver != null && truck != null) ? TransportationStatus.REGISTERED : TransportationStatus.PENDING;

        this.dto = new TransportationDTO(id,
                driver != null ? driver.getEmployeeId() : null,
                truck != null ? truck.getLicensePlate() : null,
                source.getAddress(),
                destination.getAddress(),
                sourceTime,
                destinationTime,
                document.getComment());

        save();
    }

    public TransportationBL(int id) {
        this.id = id;
        this.stops = new ArrayList<>();
        this.transportationDAO = new TransportationDAO();
        this.stopsDAO = new TransportationStopsDAO();
        this.itemDAO = new ItemAtStopDAO();
    }

    public TransportationBL(TransportationDTO dto, DriverBL driver, TruckBL truck, Map<String, SiteDTO> siteMap) throws Exception {
        this(dto.getId());
        this.dto = dto;
        this.driver = driver;
        this.truck = truck;
        this.document = new TransportationDocumentBL(dto.getId());
        this.currentWeight = 0;
        this.status = (driver != null && truck != null) ? TransportationStatus.REGISTERED : TransportationStatus.PENDING;
        this.diffrentZones = false;

        this.stops = new ArrayList<>();
        for (TransportationStopsDAO.StopRecord stop : stopsDAO.getStopsForTransportation(dto.getId())) {
            SiteDTO site = siteMap.get(stop.address);
            if (site == null) throw new Exception("Missing site for stop: " + stop.address);
            StopBL s = new StopBL(site, stop.arrivalTime);
            this.stops.add(s);
        }

        List<ItemAtStopDTO> items = itemDAO.getItemsForTransportation(dto.getId());
        for (ItemAtStopDTO item : items) {
            for (StopBL stop : stops) {
                if (stop.getSite().getAddress().equals(item.getStopAddress())) {
                    TransportationItemBL bl = new TransportationItemBL(item.getItemName(), item.getItemWeight());
                    if (item.isPickup()) stop.addItemToPickUp(bl);
                    else stop.addItemToDeliver(bl);
                    document.addItem(bl);
                    currentWeight += item.getItemWeight();
                    break;
                }
            }
        }

        for (int i = 0; i < stops.size(); i++) {
            for (int j = i + 1; j < stops.size(); j++) {
                if (!stops.get(i).getSite().getZone().equals(stops.get(j).getSite().getZone())) {
                    diffrentZones = true;
                    break;
                }
            }
        }
    }

    private StopBL findOrCreateStop(SiteDTO site, LocalDateTime expectedArrivalTime) {
        for (StopBL stop : stops) {
            if (stop.getSite().equals(site) && stop.getArrivalTime().equals(expectedArrivalTime)) {
                return stop;
            }
        }
        StopBL newStop = new StopBL(site, expectedArrivalTime);
        stops.add(newStop);
        for (StopBL s : stops) {
            if (!s.getSite().getZone().equals(newStop.getSite().getZone())) {
                this.diffrentZones = true;
                break;
            }
        }
        return newStop;
    }

    public void assignOrder(SiteDTO source, LocalDateTime sourceTime, SiteDTO dest, LocalDateTime destTime, List<TransportationItemBL> items) {
        StopBL src = findOrCreateStop(source, sourceTime);
        StopBL dst = findOrCreateStop(dest, destTime);

        for (TransportationItemBL item : items) {
            src.addItemToPickUp(item);
            dst.addItemToDeliver(item);
            document.addItem(item);
            currentWeight += item.getTotalWeight();
        }

        document.setDepartureWeight(currentWeight + truck.getNetWeight());
        truck.addBusyInterval(sourceTime, destTime);

        if (sourceTime.isBefore(dto.getSourceTime())) {
            dto.setSourceAddress(source.getAddress());
            dto.setSourceTime(sourceTime);
        }

        if (destTime.isAfter(dto.getDestinationTime())) {
            dto.setDestinationAddress(dest.getAddress());
            dto.setDestinationTime(destTime);
        }

        save();
    }

    public boolean canAssignBetween(SiteDTO source, LocalDateTime sourceTime, SiteDTO dest, LocalDateTime destTime, List<TransportationItemBL> items) {
        double addedWeight = items.stream().mapToDouble(TransportationItemBL::getTotalWeight).sum();
        if (truck == null || driver == null) return false;

        boolean fitsSchedule = truck.isAvailableFor(sourceTime, destTime);
        boolean fitsWeight = (currentWeight + addedWeight) <= truck.getMaxWeight();

        return fitsSchedule && fitsWeight;
    }

    public void setTruck(TruckBL truck) {
        this.truck = truck;
        if (truck != null && dto != null)
            dto.setTruckLicensePlate(truck.getLicensePlate());
    }

    public void setDriver(DriverBL driver) {
        this.driver = driver;
        if (driver != null && dto != null)
            dto.setDriverId(driver.getEmployeeId());
    }

    public void setSourceTime(LocalDateTime sourceTime) {
        if (dto != null)
            dto.setSourceTime(sourceTime);
        if (!stops.isEmpty())
            stops.getFirst().setArrivalTime(sourceTime);
    }

    public void setDestinationTime(LocalDateTime destinationTime) {
        if (dto != null)
            dto.setDestinationTime(destinationTime);
        if (!stops.isEmpty())
            stops.getLast().setArrivalTime(destinationTime);
    }

    public void setStatus(TransportationStatus status) {
        this.status = status;
    }

    public TransportationDAO getTransportationDAO() {
        return this.transportationDAO;
    }


    public boolean canAcceptOrder(List<TransportationItemBL> items, SiteDTO source, SiteDTO dest, LocalDateTime sourceTime, LocalDateTime destTime) {
        return canAssignBetween(source, sourceTime, dest, destTime, items);
    }

    public void addOrder(List<TransportationItemBL> items, SiteDTO source, SiteDTO dest, LocalDateTime sourceTime, LocalDateTime destTime) {
        assignOrder(source, sourceTime, dest, destTime, items);
        try {
            persistStopsAndItems();
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist added order: " + e.getMessage(), e);
        }
    }

    public void persistStopsAndItems() throws Exception {
        stopsDAO.deleteStopsByTransportationId(id);
        for (StopBL stop : stops) {
            stopsDAO.insertStop(id, stop.getSite().getAddress(), stop.getArrivalTime());
            for (TransportationItemBL item : stop.getItemsToPickUp()) {
                itemDAO.insert(new ItemAtStopDTO(id, stop.getSite().getAddress(), item.getName(), item.getTotalWeight(), true));
            }
            for (TransportationItemBL item : stop.getItemsToDeliver()) {
                itemDAO.insert(new ItemAtStopDTO(id, stop.getSite().getAddress(), item.getName(), item.getTotalWeight(), false));
            }
        }
    }

    public void updateComment(String newComment) throws Exception {
        document.addComment(newComment);
        dto.setComment(document.getComment());
        transportationDAO.updateComment(dto.getId(), document.getComment());
    }

    public void save() {
        try {
            transportationDAO.insertOrUpdate(dto);
            persistStopsAndItems();
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist transportation: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Transportation ID=" + id + " | Status=" + status + " | Stops Path:\n");
        for (StopBL stop : stops.stream().sorted(Comparator.comparing(StopBL::getArrivalTime)).toList()) {
            sb.append("  -> ").append(stop).append("\n");
        }
        return sb.toString();
    }

    public int getId() { return id; }
    public List<StopBL> getStops() { return List.copyOf(stops); }
    public TransportationDTO getDto() { return dto; }
    public TruckBL getTruck() { return truck; }
    public DriverBL getDriver() { return driver; }
    public TransportationDocumentBL getDocument() { return document; }
    public TransportationStatus getStatus() { return status; }
    public void setDto(TransportationDTO dto) { this.dto = dto; }
    public SiteDTO getSourcePending(){
        StopBL stop1 = stops.getFirst();
        StopBL stop2 = stops.getLast();
        if(stop1.getArrivalTime().isBefore(stop2.getArrivalTime()))
        {
            return stop1.getSite();
        }
        return stop2.getSite();
    };
    public SiteDTO getDestPending(){
        StopBL stop1 = stops.getFirst();
        StopBL stop2 = stops.getLast();
        if(stop1.getArrivalTime().isAfter(stop2.getArrivalTime()))
        {
            return stop1.getSite();
        }
        return stop2.getSite();
    };
}
