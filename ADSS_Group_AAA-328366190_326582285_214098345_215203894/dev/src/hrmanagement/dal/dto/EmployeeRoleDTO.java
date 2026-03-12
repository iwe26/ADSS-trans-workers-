package hrmanagement.dal.dto;

import java.util.Objects;

/**
 * Represents the many-to-many relationship between Employee and Role.
 */
public class EmployeeRoleDTO {
    private final int employeeId;  // FK → EmployeeDTO.id
    private final int roleId;      // FK → RoleDTO.id

    public EmployeeRoleDTO(int employeeId, int roleId) {
        this.employeeId = employeeId;
        this.roleId = roleId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getRoleId() {
        return roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeRoleDTO)) return false;
        EmployeeRoleDTO that = (EmployeeRoleDTO) o;
        return employeeId == that.employeeId &&
                roleId == that.roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, roleId);
    }
}
