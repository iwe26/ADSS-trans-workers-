package hrmanagement.dal.dto;

import hrmanagement.domain.enums.ShiftType;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an employee’s unavailability for a specific date and shift.
 * Composite PK: (employeeId, date, shiftType)
 */
public class EmployeeAvailabilityDTO {
    private final int employeeId;    // FK → EmployeeDTO.id
    private final LocalDate date;    // part of PK
    private final ShiftType shiftType; // part of PK

    public EmployeeAvailabilityDTO(int employeeId, LocalDate date, ShiftType shiftType) {
        this.employeeId = employeeId;
        this.date = date;
        this.shiftType = shiftType;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeAvailabilityDTO)) return false;
        EmployeeAvailabilityDTO that = (EmployeeAvailabilityDTO) o;
        return employeeId == that.employeeId &&
                Objects.equals(date, that.date) &&
                shiftType == that.shiftType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, date, shiftType);
    }
}
