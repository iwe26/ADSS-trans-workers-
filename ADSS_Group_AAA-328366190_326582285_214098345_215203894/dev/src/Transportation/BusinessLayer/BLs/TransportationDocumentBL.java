package Transportation.BusinessLayer.BLs;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transportation document that holds all the items
 * involved in a transportation process.
 */
public class TransportationDocumentBL {
    private int transportationId;
    private double deparuteWeight = 0.0;
    private List<TransportationItemBL> allItems = new ArrayList<>();
    private String comment;

    /**
     * Constructor to create a document for a specific transportation.
     *
     * @param transportationId - The ID of the associated transportation
     */
    public TransportationDocumentBL(int transportationId) {
        this.transportationId = transportationId;
    }

    /**
     * Adds an item to the transportation document.
     *
     * @param item - TransportationItemBL to be added
     */
    public void addItem(TransportationItemBL item) {
        allItems.add(item);
    }

    public void setDepartureWeight(double departureWeight)
    {
        this.deparuteWeight = departureWeight;
    }

    /**
     * Returns all items listed in the document.
     *
     * @return list of transportation items
     */
    public List<TransportationItemBL> getAllItems() {
        return allItems;
    }

    /**
     * Returns only items that were delivered.
     *
     * @return list of delivered transportation items
     */
    public List<TransportationItemBL> getDeliveredItems() {
        List<TransportationItemBL> delivered = new ArrayList<>();
        for (TransportationItemBL item : allItems) {
            if (item.isDelivered()) {
                delivered.add(item);
            }
        }
        return delivered;
    }

    /**
     * Returns only items that are still onboard.
     *
     * @return list of undelivered transportation items
     */
    public List<TransportationItemBL> getUndeliveredItems() {
        List<TransportationItemBL> onboard = new ArrayList<>();
        for (TransportationItemBL item : allItems) {
            if (!item.isDelivered()) {
                onboard.add(item);
            }
        }
        return onboard;
    }

    /**
     * Returns the transportation ID associated with this document.
     *
     * @return transportation ID - int
     */
    public int getTransportationId() {
        return transportationId;
    }

    /**
     * Gets the comment associated with this transportation document.
     *
     * @return comment - String
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets a comment for the transportation document.
     *
     * @param comment - descriptive comment regarding the transportation
     */
    public void addComment(String comment)
    {
        if(this.comment == null || this.comment.equals("null"))
        {
            this.comment =comment;
        }
        else
        {
            this.comment += "\n" + comment;
        }
    }
}
