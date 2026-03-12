package hrmanagement.dal.dto;

import java.util.Objects;

/**
 * Represents a required role for a shift.
 * Composite PK: (shiftId, roleId)
 */
public class ShiftRequiredRoleDTO {
    private final int shiftId; // FK → ShiftDTO.id
    private final int roleId;  // FK → RoleDTO.id

    public ShiftRequiredRoleDTO(int shiftId, int roleId) {
        this.shiftId = shiftId;
        this.roleId = roleId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public int getRoleId() {
        return roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShiftRequiredRoleDTO)) return false;
        ShiftRequiredRoleDTO that = (ShiftRequiredRoleDTO) o;
        return shiftId == that.shiftId &&
                roleId  == that.roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId, roleId);
    }
}
