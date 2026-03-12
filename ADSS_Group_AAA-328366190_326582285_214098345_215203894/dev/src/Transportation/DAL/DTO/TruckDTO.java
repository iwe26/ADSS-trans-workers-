package Transportation.DAL.DTO;

import java.util.Objects;

public class TruckDTO {
    private final String licensePlate;
    private final double netWeight;
    private final double maxWeight;
    private final int requiredLicense;

    public TruckDTO(String licensePlate, double netWeight, double maxWeight, int requiredLicense) {
        this.licensePlate = licensePlate;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.requiredLicense = requiredLicense;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public double getNetWeight() {
        return netWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public int getRequiredLicense() {
        return requiredLicense;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TruckDTO)) return false;
        TruckDTO that = (TruckDTO) o;
        return Double.compare(that.netWeight, netWeight) == 0 &&
                Double.compare(that.maxWeight, maxWeight) == 0 &&
                requiredLicense == that.requiredLicense &&
                Objects.equals(licensePlate, that.licensePlate);
    }
}
