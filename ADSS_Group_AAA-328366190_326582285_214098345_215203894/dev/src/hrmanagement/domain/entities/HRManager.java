package hrmanagement.domain.entities;

import hrmanagement.dal.dto.*;
import hrmanagement.domain.enums.ShiftType;
import hrmanagement.domain.facade.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class HRManager {


    private final EmployeeFacade            employees;
    private final RoleFacade roles;
    private final EmployeeRoleFacade employeeRoles;
    private final ShiftFacade               shifts;
    private final ShiftAssignmentFacade assignments;
    private final ShiftRequiredRoleFacade requirements;
    private final EmployeeAvailabilityFacade availability;

    public HRManager(
            EmployeeFacade employees,
            RoleFacade roles,
            EmployeeRoleFacade employeeRoles,
            ShiftFacade shifts,
            ShiftAssignmentFacade assignments,
            ShiftRequiredRoleFacade requirements,
            EmployeeAvailabilityFacade availability
    ) {
        this.employees       = employees;
        this.roles           = roles;
        this.employeeRoles   = employeeRoles;
        this.shifts          = shifts;
        this.assignments     = assignments;
        this.requirements    = requirements;
        this.availability    = availability;
    }
    // --- Employee operations ----------------------------------------------



    public void createEmployee(String name, int id, String bankAccount,
                               double salary, String terms, LocalDate startDate, String branchAddress) {
        EmployeeDTO dto = new EmployeeDTO(id, name, bankAccount, salary, terms, startDate, true, branchAddress);
        try {
            employees.createEmployee(dto);
        } catch (Exception e) {
            // handle or log
        }
    }

    public boolean employeeExists(int employeeId) {
        try {
            return employees.findById(employeeId).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    // --- Role operations --------------------------------------------------

    public void assignRoleToEmployee(int employeeId, String roleName) {
        try {
            RoleDTO role = roles.findByName(roleName).orElse(null);
            if (role == null) {
                int newRoleId = roles.createRole(new RoleDTO(roleName));
                role = new RoleDTO(newRoleId, roleName);
            }
            employeeRoles.createRole(new EmployeeRoleDTO(employeeId, role.getId()));
        } catch (Exception e) {
            // handle or log
        }
    }

    public boolean employeeHasRole(int employeeId, String roleName) {
        try {
            RoleDTO role = roles.findByName(roleName).orElse(null);
            if (role == null) return false;
            return employeeRoles.getRolesForEmployee(employeeId)
                    .stream()
                    .anyMatch(er -> er.getRoleId() == role.getId());
        } catch (Exception e) {
            return false;
        }
    }

    // --- Availability queries --------------------------------------------

    public boolean isEmployeeAvailable(LocalDate date, ShiftType shiftType, int employeeId) {
        try {
            // 1) Fetch employee record to get their branch (siteAddress)
            EmployeeDTO emp = employees.findById(employeeId).orElse(null);
            if (emp == null) {
                return false;
            }
            String employeeBranch = emp.getBranchAddress();

            // 2) Find the shift for this date+type that matches the same site as employee
            Optional<ShiftDTO> maybeShift = shifts.getAllShifts().stream()
                    .filter(s -> s.getDate().equals(date)
                            && s.getShiftType() == shiftType
                            && s.getSiteAddress().equals(employeeBranch))
                    .findFirst();

            if (maybeShift.isEmpty()) {
                // No shift at this employee’s branch on that date+type
                return false;
            }

            // 3) Finally, check if employee has an “unavailable” record for that date+shiftType
            return availability.getAvailabilitiesForEmployee(employeeId).stream()
                    .noneMatch(dto ->
                            dto.getDate().equals(date) &&
                                    dto.getShiftType() == shiftType
                    );
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmployeeAvailableCreateShift(LocalDate date, ShiftType shiftType, int employeeId)
    {
        try {
            // 1) Fetch employee record to get their branch (siteAddress)
            EmployeeDTO emp = employees.findById(employeeId).orElse(null);
            if (emp == null) {
                return false;
            }
            String employeeBranch = emp.getBranchAddress();

            // 2) Find the shift for this date+type that matches the same site as employee
            Optional<ShiftDTO> maybeShift = shifts.getAllShifts().stream()
                    .filter(s -> s.getDate().equals(date)
                            && s.getShiftType() == shiftType
                            && s.getSiteAddress().equals(employeeBranch))
                    .findFirst();

            if (maybeShift.isPresent()) {
                // No shift at this employee’s branch on that date+type
                return false;
            }

            // 3) Finally, check if employee has an “unavailable” record for that date+shiftType
            return availability.getAvailabilitiesForEmployee(employeeId).stream()
                    .noneMatch(dto ->
                            dto.getDate().equals(date) &&
                                    dto.getShiftType() == shiftType
                    );
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmployeeAvailable(int shiftId, int employeeId) {
        try {
            ShiftDTO shift = shifts.getShiftById(shiftId).orElse(null);
            if (shift == null) return false;
            return isEmployeeAvailable(shift.getDate(), shift.getShiftType(), employeeId);
        } catch (Exception e) {
            return false;
        }
    }

    // --- Shift operations -------------------------------------------------

    public Optional<Integer> createShift(LocalDate date, ShiftType type,
                                         List<String> requiredRoleNames,
                                         Set<Integer> employeeIds,
                                         int managerId,
                                         String siteAddress) {
        try {
            int shiftId = shifts.createShift(new ShiftDTO(date, type, managerId, siteAddress));

            for (String roleName : requiredRoleNames) {
                RoleDTO role = roles.findByName(roleName).orElse(null);
                if (role == null) {
                    int newRoleId = roles.createRole(new RoleDTO(roleName));
                    role = new RoleDTO(newRoleId, roleName);
                }
                requirements.addRequirement(new ShiftRequiredRoleDTO(shiftId, role.getId()));
            }

            for (int empId : employeeIds) {
                assignments.assign(new ShiftAssignmentDTO(shiftId, empId));
            }

            return Optional.of(shiftId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean shiftExists(int shiftId) {
        try {
            return shifts.getShiftById(shiftId).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    public List<Integer> getAssignedEmployees(int shiftId) {
        try {
            return assignments.getAssignmentsByShift(shiftId)
                    .stream()
                    .map(ShiftAssignmentDTO::getEmployeeId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void assignEmployeeToShift(int shiftId, int employeeId) {
        try {
            assignments.assign(new ShiftAssignmentDTO(shiftId, employeeId));
        } catch (Exception e) {
            // handle or log
        }
    }

    public void removeEmployeeFromShift(int shiftId, int employeeId) {
        try {
            assignments.unassign(new ShiftAssignmentDTO(shiftId, employeeId));
        } catch (Exception e) {
            // handle or log
        }
    }

    public Optional<Integer> getShiftManager(int shiftId) {
        try {
            ShiftDTO shift = shifts.getShiftById(shiftId).orElse(null);
            return shift == null
                    ? Optional.empty()
                    : Optional.of(shift.getShiftManagerId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void setShiftManager(int shiftId, int managerId) {
        try {
            shifts.updateShiftManager(shiftId, managerId);
        } catch (Exception e) {
            // handle or log
        }
    }

    public List<String> getShiftRoles(int shiftId) {
        try {
            return requirements.getRequirementsByShift(shiftId)
                    .stream()
                    .map(dto -> {
                        try { if (roles.getRoleById(dto.getRoleId()).isPresent()) return roles.getRoleById(dto.getRoleId()).get().getName();else return null; }
                        catch (Exception ex) { return null; }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean validateRoleCoverage(int shiftId) {
        List<String> required = getShiftRoles(shiftId);
        List<Integer> assigned = getAssignedEmployees(shiftId);
        for (String req : required) {
            boolean covered = assigned.stream()
                    .anyMatch(empId -> employeeHasRole(empId, req));
            if (!covered) return false;
        }
        return true;
    }
}