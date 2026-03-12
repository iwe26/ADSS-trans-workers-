package hrmanagement.dal.dto;

import java.util.Objects;

/**
 * Represents the assignment of an Employee to a Shift.
 */
public class ShiftAssignmentDTO {
    private final int shiftId;     // FK → ShiftDTO.id
    private final int employeeId;  // FK → EmployeeDTO.id

    public ShiftAssignmentDTO(int shiftId, int employeeId) {
        this.shiftId = shiftId;
        this.employeeId = employeeId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShiftAssignmentDTO)) return false;
        ShiftAssignmentDTO that = (ShiftAssignmentDTO) o;
        return shiftId == that.shiftId &&
                employeeId == that.employeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId, employeeId);
    }
}
