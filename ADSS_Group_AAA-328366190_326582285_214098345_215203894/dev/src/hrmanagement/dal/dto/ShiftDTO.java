package hrmanagement.dal.dto;

import hrmanagement.domain.enums.ShiftType;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * Data Transfer Object for the Shift table.
 */
public class ShiftDTO {
    private final int id;                   // PK
    private final LocalDate date;
    private final ShiftType shiftType;
    private int shiftManagerId;       // FK → EmployeeDTO.id
    private final String siteAddress;       // FK → SiteDTO.address


    public ShiftDTO(int id, LocalDate date, ShiftType shiftType, int shiftManagerId, String siteAddress) {
        this.id = id;
        this.date = date;
        this.shiftType = shiftType;
        this.shiftManagerId = shiftManagerId;
        this.siteAddress = siteAddress;
    }

    /** Constructor for a new shift (ID assigned by DB). */
    public ShiftDTO(LocalDate date, ShiftType shiftType, int shiftManagerId, String siteAddress) {
        this.id = 0;
        this.date = date;
        this.shiftType = shiftType;
        this.shiftManagerId = shiftManagerId;
        this.siteAddress = siteAddress;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public int getShiftManagerId() {
        return shiftManagerId;
    }

    public void setShiftManagerId(int shiftManagerId) {
        this.shiftManagerId = shiftManagerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShiftDTO)) return false;
        ShiftDTO shiftDTO = (ShiftDTO) o;
        return id == shiftDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    public String getSiteAddress() {
        return siteAddress;
    }
}
