package hrmanagement.presentation.controller;

import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.domain.enums.ShiftType;
import hrmanagement.service.EmployeeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    public Optional<EmployeeDTO> lookupEmployee(int id) {
        return service.findById(id);
    }

    public boolean updateAvailability(int empId, Map<LocalDate, List<ShiftType>> toAdd) {
        // any additional validation or transformation lives here
        return service.updateAvailability(empId, toAdd);
    }

    public boolean requestLeave(int empId) {
        return service.requestLeave(empId);
    }
}
