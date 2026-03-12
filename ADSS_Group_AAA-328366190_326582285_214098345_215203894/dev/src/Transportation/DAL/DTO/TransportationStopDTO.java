package Transportation.DAL.DTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class TransportationStopDTO {
    private final int transportationId;
    private final String address;
    private final LocalDateTime arrivalTime;

    public TransportationStopDTO(int transportationId, String address, LocalDateTime arrivalTime) {
        this.transportationId = transportationId;
        this.address = address;
        this.arrivalTime = arrivalTime;
    }

    public int getTransportationId() {
        return transportationId;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportationStopDTO)) return false;
        TransportationStopDTO that = (TransportationStopDTO) o;
        return transportationId == that.transportationId &&
                Objects.equals(address, that.address) &&
                Objects.equals(arrivalTime, that.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportationId, address, arrivalTime);
    }
}
