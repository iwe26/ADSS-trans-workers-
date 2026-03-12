package Transportation.DAL.DTO;

import java.util.Objects;

public class ItemAtStopDTO {
    private final int transportationId;
    private final String stopAddress;
    private final String itemName;
    private final double itemWeight;
    private final boolean pickup;

    public ItemAtStopDTO(int transportationId, String stopAddress, String itemName, double itemWeight, boolean pickup) {
        this.transportationId = transportationId;
        this.stopAddress = stopAddress;
        this.itemName = itemName;
        this.itemWeight = itemWeight;
        this.pickup = pickup;
    }

    public int getTransportationId() {
        return transportationId;
    }

    public String getStopAddress() {
        return stopAddress;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemWeight() {
        return itemWeight;
    }

    public boolean isPickup() {
        return pickup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemAtStopDTO)) return false;
        ItemAtStopDTO that = (ItemAtStopDTO) o;
        return transportationId == that.transportationId &&
                Double.compare(that.itemWeight, itemWeight) == 0 &&
                pickup == that.pickup &&
                Objects.equals(stopAddress, that.stopAddress) &&
                Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportationId, stopAddress, itemName, itemWeight, pickup);
    }
}
