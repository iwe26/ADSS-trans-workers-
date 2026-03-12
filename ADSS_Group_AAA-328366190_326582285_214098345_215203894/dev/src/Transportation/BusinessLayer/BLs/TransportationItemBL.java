package Transportation.BusinessLayer.BLs;

public class TransportationItemBL {
    private String name;
    private double weight;
    private boolean wasDelivered = false;
    private Integer deliveredAtStopId = null;

    public TransportationItemBL(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    public double getTotalWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public boolean isDelivered() {
        return wasDelivered;
    }

    public Integer getDeliveredAtStopId() {
        return deliveredAtStopId;
    }

    public void markAsDelivered(int stopId) {
        this.wasDelivered = true;
        this.deliveredAtStopId = stopId;
    }

    public void undoDelivery() {
        this.wasDelivered = false;
        this.deliveredAtStopId = null;
    }

    @Override
    public String toString() {
        return name + " (" + weight + "kg)" +
                (wasDelivered ? " [DELIVERED at Stop " + deliveredAtStopId + "]" : "");
    }
}
