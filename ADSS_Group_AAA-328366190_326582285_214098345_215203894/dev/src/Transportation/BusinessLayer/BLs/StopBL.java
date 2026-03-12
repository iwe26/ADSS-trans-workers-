package Transportation.BusinessLayer.BLs;

import hrmanagement.dal.dto.SiteDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stop in a transportation route, including the site,
 * items to be picked up, items to be delivered, and the arrival status.
 */
public class StopBL {
    private SiteDTO site; // The physical location of the stop
    private LocalDateTime arrivalTime; // Time the driver arrived at the stop
    private List<TransportationItemBL> itemsToPickUp = new ArrayList<>(); // Items to be picked up at this stop
    private List<TransportationItemBL> itemsToDeliver = new ArrayList<>(); // Items to be delivered at this stop

    /**
     * Constructs a StopBL with the given site.
     *
     * @param site - the site of the stop
     */
    public StopBL(SiteDTO site , LocalDateTime arrivalTime) {
        this.site = site;
        this.arrivalTime = arrivalTime;
    }

    /**
     * Returns the site of the stop.
     *
     * @return the site - SiteBL
     */
    public SiteDTO getSite() {
        return site;
    }

    /**
     * Returns the time the driver arrived at the stop.
     *
     * @return arrival time - LocalDateTime
     */
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Manually sets the arrival time.
     *
     * @param arrivalTime - the time to set
     */
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Checks whether the stop was already reached.
     *
     * @return true if arrival time is set, false otherwise
     */
    public boolean wasReached()
    {
        return arrivalTime != null;
    }

    /**
     * Returns the list of items to be picked up at this stop.
     *
     * @return list of pickup items - List<TransportationItemBL>
     */
    public List<TransportationItemBL> getItemsToPickUp() {
        return itemsToPickUp;
    }

    /**
     * Returns the list of items to be delivered at this stop.
     *
     * @return list of delivery items - List<TransportationItemBL>
     */
    public List<TransportationItemBL> getItemsToDeliver() {
        return itemsToDeliver;
    }

    /**
     * Adds an item to the pickup list.
     *
     * @param item - TransportationItemBL to be picked up
     */
    public void addItemToPickUp(TransportationItemBL item) {
        itemsToPickUp.add(item);
    }

    /**
     * Adds an item to the delivery list.
     *
     * @param item - TransportationItemBL to be delivered
     */
    public void addItemToDeliver(TransportationItemBL item) {
        itemsToDeliver.add(item);
    }

    /**
     * Calculates the total weight of all items to be picked up.
     *
     * @return total pickup weight - double
     */
    public double getTotalPickupWeight() {
        return itemsToPickUp.stream()
                .mapToDouble(TransportationItemBL::getTotalWeight)
                .sum();
    }

    /**
     * Calculates the total weight of all items to be delivered.
     *
     * @return total delivery weight - double
     */
    public double getTotalDeliveryWeight() {
        return itemsToDeliver.stream()
                .mapToDouble(TransportationItemBL::getTotalWeight)
                .sum();
    }

    /**
     * Returns a string representation of the stop, including the site name
     * and arrival time (if reached).
     *
     * @return description of the stop
     */
    @Override
    public String toString() {
        return "Stop at " + site.getAddress() +
                (arrivalTime != null ? " | Arrived at: " + arrivalTime : " | Not arrived yet");
    }
}
