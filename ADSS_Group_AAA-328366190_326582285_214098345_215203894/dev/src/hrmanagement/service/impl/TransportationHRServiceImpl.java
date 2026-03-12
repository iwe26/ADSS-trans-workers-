package hrmanagement.service.impl;

import Transportation.BusinessLayer.BLs.DriverBL;
import hrmanagement.dal.dao.*;
import hrmanagement.dal.dto.EmployeeAvailabilityDTO;
import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.dal.dto.ShiftAssignmentDTO;
import hrmanagement.dal.dto.ShiftDTO;
import hrmanagement.domain.enums.ShiftType;
import hrmanagement.domain.facade.*;
import hrmanagement.service.TransportationHRService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TransportationHRServiceImpl implements TransportationHRService {


    private final ShiftFacade               shiftFacade;
    private final ShiftAssignmentFacade assignmentFacade;
    private final RoleFacade roleFacade;
    private final EmployeeRoleFacade employeeRoleFacade;
    private final EmployeeFacade employeeFacade;
    private final EmployeeAvailabilityFacade availabilityFacade;


    public TransportationHRServiceImpl(
            ShiftFacade shiftFacade,
            ShiftAssignmentFacade assignmentFacade,
            RoleFacade roleFacade,
            EmployeeRoleFacade employeeRoleFacade,
            EmployeeFacade employeeFacade,
            EmployeeAvailabilityFacade availabilityFacade
    ) {
        this.shiftFacade               = shiftFacade;
        this.assignmentFacade          = assignmentFacade;
        this.roleFacade                = roleFacade;
        this.employeeRoleFacade        = employeeRoleFacade;
        this.employeeFacade            = employeeFacade;
        this.availabilityFacade        = availabilityFacade;
    }

    @Override
    public boolean isWarehouseEmployeeAssignToTransportation(
            LocalDateTime shiftTime,
            String branchAddress
    ) {
        try {
            LocalDate date = shiftTime.toLocalDate();
            ShiftType type = shiftTime.toLocalTime().isBefore(LocalTime.NOON)
                    ? ShiftType.MORNING
                    : ShiftType.EVENING;

            // Find shift matching date, type, and branchName (site_name)
            Optional<ShiftDTO> shiftOpt = shiftFacade.getAllShifts().stream()
                    .filter(s ->
                            s.getDate().equals(date)
                                    && s.getShiftType() == type
                                    && s.getSiteAddress().equals(branchAddress)
                    )
                    .findFirst();
            if (shiftOpt.isEmpty()) {
                return false;
            }
            int shiftId = shiftOpt.get().getId();

            // Get assigned employees for that shift
            List<ShiftAssignmentDTO> assignments = assignmentFacade.getAssignmentsByShift(shiftId);

            // Get the "Warehouse" role ID
            var warehouseRole = roleFacade.findByName("Warehouse");
            if (warehouseRole.isEmpty()) {
                return false;
            }
            int warehouseRoleId = warehouseRole.get().getId();

            // Check if any assigned employee has the warehouse role
            for (ShiftAssignmentDTO asg : assignments) {
                List<Integer> roleIds = employeeRoleFacade.getRolesForEmployee(asg.getEmployeeId())
                        .stream()
                        .map(r -> r.getRoleId())
                        .collect(Collectors.toList());
                if (roleIds.contains(warehouseRoleId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<EmployeeDTO> getAvailableDrivers(LocalDateTime transportationStartTime,
                                              LocalDateTime transportationEndTime) {
        try {
            // Determine the shift type for start and end
            LocalDate date = transportationStartTime.toLocalDate();
            ShiftType type = transportationStartTime.toLocalTime().isBefore(LocalTime.NOON) ?
                    ShiftType.MORNING : ShiftType.EVENING;

            // Find all employees with the "Driver" role
            var driverRoleOpt = Optional.ofNullable(roleFacade.findByName("Driver"));
            if (driverRoleOpt.isEmpty()) return Collections.emptyList();
            int driverRoleId = driverRoleOpt.get().isPresent() ? driverRoleOpt.get().get().getId() : -1111;

            List<Integer> driverIds = employeeRoleFacade.getRolesByRoleId(driverRoleId)
                    .stream()
                    .map(d -> d.getEmployeeId())
                    .toList();

            // Filter out drivers who are unavailable on that date & shift
            List<EmployeeDTO> result = new ArrayList<>();
            for (int empId : driverIds) {
                // check unavailability records
                List<EmployeeAvailabilityDTO> unavail = availabilityFacade.getAvailabilitiesForEmployee(empId);
                boolean isUnavailable = unavail.stream()
                        .anyMatch(u -> u.getDate().equals(date) && u.getShiftType() == type);
                if (isUnavailable) continue;

                // If passes, fetch employee details and create DriverBL
                EmployeeDTO dto = employeeFacade.findById(empId).orElse(null);
                if (dto != null)
                {
                    result.add(dto);
                }
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean assignDriverToShift(
            int driverId,
            LocalDateTime shiftTime,
            String branchAddress
    ) {
        try {
            LocalDate date = shiftTime.toLocalDate();
            ShiftType type = shiftTime.toLocalTime().isBefore(LocalTime.NOON)
                    ? ShiftType.MORNING
                    : ShiftType.EVENING;

            // Find shift matching date, type, and branchName
            Optional<ShiftDTO> shiftOpt = shiftFacade.getAllShifts().stream()
                    .filter(s ->
                            s.getDate().equals(date)
                                    && s.getShiftType() == type
                                    && s.getSiteAddress().equals(branchAddress)
                    )
                    .findFirst();

            if (shiftOpt.isEmpty()) {
                return false;
            }
            int shiftId = shiftOpt.get().getId();

            // Insert assignment record
            ShiftAssignmentDTO assignment = new ShiftAssignmentDTO(shiftId, driverId);
            assignmentFacade.assign(assignment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
