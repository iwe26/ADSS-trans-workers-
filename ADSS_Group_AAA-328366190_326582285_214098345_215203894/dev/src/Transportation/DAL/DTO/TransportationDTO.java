package Transportation.DAL.DTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class TransportationDTO {
    private  int id;
    private  Integer driverId;
    private  String truckLicensePlate;
    private String sourceAddress;
    private  String destinationAddress;
    private  LocalDateTime sourceTime;
    private  LocalDateTime destinationTime;
    private  String comment; // New field

    public TransportationDTO(int id, Integer driverId, String truckLicensePlate,
                             String sourceAddress, String destinationAddress,
                             LocalDateTime sourceTime, LocalDateTime destinationTime,
                             String comment) {
        this.id = id;
        this.driverId = driverId;
        this.truckLicensePlate = truckLicensePlate;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.sourceTime = sourceTime;
        this.destinationTime = destinationTime;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setSourceTime(LocalDateTime sourceTime) {
        this.sourceTime = sourceTime;
    }

    public void setDestinationTime(LocalDateTime destinationTime) {
        this.destinationTime = destinationTime;
    }

    public void setTruckLicensePlate(String plate) {
        this.truckLicensePlate = plate;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getTruckLicensePlate() {
        return truckLicensePlate;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public LocalDateTime getSourceTime() {
        return sourceTime;
    }

    public LocalDateTime getDestinationTime() {
        return destinationTime;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportationDTO)) return false;
        TransportationDTO that = (TransportationDTO) o;
        return id == that.id &&
                driverId == that.driverId &&
                Objects.equals(truckLicensePlate, that.truckLicensePlate) &&
                Objects.equals(sourceAddress, that.sourceAddress) &&
                Objects.equals(destinationAddress, that.destinationAddress) &&
                Objects.equals(sourceTime, that.sourceTime) &&
                Objects.equals(destinationTime, that.destinationTime) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, truckLicensePlate, sourceAddress, destinationAddress, sourceTime, destinationTime, comment);
    }
}
