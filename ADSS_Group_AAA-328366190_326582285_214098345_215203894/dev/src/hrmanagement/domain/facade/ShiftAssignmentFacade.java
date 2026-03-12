package hrmanagement.domain.facade;

import hrmanagement.domain.repository.ShiftAssignmentRepository;
import hrmanagement.dal.dto.ShiftAssignmentDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Facade for shift-assignment business operations.
 */
public class ShiftAssignmentFacade {
    private final ShiftAssignmentRepository repository;

    public ShiftAssignmentFacade(ShiftAssignmentRepository repository) {
        this.repository = repository;
    }

    public List<ShiftAssignmentDTO> getAllAssignments() throws SQLException {
        return repository.findAll();
    }

    public List<ShiftAssignmentDTO> getAssignmentsByShift(int shiftId) throws SQLException {
        return repository.findByShift(shiftId);
    }

    public List<ShiftAssignmentDTO> getAssignmentsByEmployee(int employeeId) throws SQLException {
        return repository.findByEmployee(employeeId);
    }

    public void assign(ShiftAssignmentDTO dto) throws SQLException {
        repository.insert(dto);
    }

    public void unassign(ShiftAssignmentDTO dto) throws SQLException {
        repository.delete(dto);
    }
}
