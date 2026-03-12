package hrmanagement.service;

import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.domain.enums.ShiftType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmployeeService {
    /**
     * Fetches an employee by ID.
     * @param id the employee’s ID
     * @return Optional.empty() if not found
     */
    Optional<EmployeeDTO> findById(int id);

    /**
     * Marks leave for an employee (deactivates).
     * @param id the employee’s ID
     * @return true if successful
     */
    boolean requestLeave(int id);

    /**
     * Adds an unavailability constraint.
     * @param id           the employee’s ID
     * @param availability map of dates to shifts they cannot work
     * @return true if all constraints were saved
     */
    boolean updateAvailability(int id, Map<LocalDate, List<ShiftType>> availability);
}
