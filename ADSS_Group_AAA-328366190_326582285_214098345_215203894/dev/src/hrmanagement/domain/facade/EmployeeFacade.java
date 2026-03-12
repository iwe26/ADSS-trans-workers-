package hrmanagement.domain.facade;

import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.domain.repository.EmployeeRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EmployeeFacade {
    private final EmployeeRepository repository;

    public EmployeeFacade(EmployeeRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetch all employees.
     */
    public List<EmployeeDTO> getAllEmployees() throws SQLException {
        return repository.findAll();
    }

    /**
     * Fetch a single employee by ID.
     */
    public Optional<EmployeeDTO> findById(int id) throws SQLException {
        return repository.findById(id);
    }

    /**
     * Create a new employee.
     */
    public EmployeeDTO createEmployee(EmployeeDTO dto) throws SQLException {
        repository.insert(dto);
        return dto;
    }

    /**
     * Update an existing employee.
     */
    public void updateEmployee(EmployeeDTO dto) throws SQLException {
        repository.update(dto);
    }

    /**
     * Delete an employee by ID.
     */
    public void deleteEmployee(int id) throws SQLException {
        repository.delete(id);
    }
}
