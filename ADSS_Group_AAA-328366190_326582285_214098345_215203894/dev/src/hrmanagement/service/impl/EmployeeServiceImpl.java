package hrmanagement.service.impl;


import hrmanagement.dal.dto.EmployeeAvailabilityDTO;
import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.domain.enums.ShiftType;
import hrmanagement.domain.facade.EmployeeAvailabilityFacade;
import hrmanagement.domain.facade.EmployeeFacade;
import hrmanagement.service.EmployeeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeFacade employeeFacade;
    private final EmployeeAvailabilityFacade availabilityFacade;

    public EmployeeServiceImpl(EmployeeFacade employeeFacade,
                               EmployeeAvailabilityFacade availabilityFacade) {
        this.employeeFacade       = employeeFacade;
        this.availabilityFacade   = availabilityFacade;
    }

    @Override
    public Optional<EmployeeDTO> findById(int id) {
        try {
            return employeeFacade.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean requestLeave(int id) {
        try {
            // for now, delete or deactivate
            employeeFacade.deleteEmployee(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateAvailability(int id, Map<LocalDate, List<ShiftType>> availability) {
        try {
            // Insert each unavailable slot via the facade
            for (var entry : availability.entrySet()) {
                var date   = entry.getKey();
                for (ShiftType shift : entry.getValue()) {
                    availabilityFacade.createAvailability(
                            new EmployeeAvailabilityDTO(id, date, shift)
                    );
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
