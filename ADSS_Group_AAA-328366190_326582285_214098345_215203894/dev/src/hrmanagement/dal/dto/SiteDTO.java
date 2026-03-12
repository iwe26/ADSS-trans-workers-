package hrmanagement.dal.dto;


import Transportation.BusinessLayer.Resources.TransportationZone;

import java.util.Objects;

/**
 * Represents a branch or transport site, now including its zone.
 */
public class SiteDTO {
    private final String name;
    private String address; // PK
    private String contactName;
    private String phone;
    private TransportationZone zone;        // new field

    public SiteDTO(String name,
                   String address,
                   String contactName,
                   String phone,
                   TransportationZone zone) {
        this.name        = name;
        this.address     = address;
        this.contactName = contactName;
        this.phone       = phone;
        this.zone        = zone;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPhone() {
        return phone;
    }

    public TransportationZone getZone() {
        return zone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setZone(TransportationZone zone) {
        this.zone = zone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteDTO)) return false;
        SiteDTO site = (SiteDTO) o;
        return Objects.equals(address, site.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    public void setTransportationZone(TransportationZone transportationZone) {
        if (transportationZone == null) {
            throw new IllegalArgumentException("Transportation zone cannot be null");
        }
        this.zone = transportationZone;
    }

    @Override
    public String toString() {
        return "Site: " + address +
                ", Contact: " + contactName +
                ", Phone: " + phone +
                ", Transportation Zone: " + (zone != null ? zone.name() : "N/A");
    }

}
