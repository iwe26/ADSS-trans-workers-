package hrmanagement.domain.facade;
import hrmanagement.domain.repository.ShiftRequiredRoleRepository;
import hrmanagement.dal.dto.ShiftRequiredRoleDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Facade for shift-required-role business operations.
 */
public class ShiftRequiredRoleFacade {
    private final ShiftRequiredRoleRepository repository;

    public ShiftRequiredRoleFacade(ShiftRequiredRoleRepository repository) {
        this.repository = repository;
    }

    public List<ShiftRequiredRoleDTO> getAllRequirements() throws SQLException {
        return repository.findAll();
    }

    public List<ShiftRequiredRoleDTO> getRequirementsByShift(int shiftId) throws SQLException {
        return repository.findByShift(shiftId);
    }

    public List<ShiftRequiredRoleDTO> getRequirementsByRole(int roleId) throws SQLException {
        return repository.findByRole(roleId);
    }

    public void addRequirement(ShiftRequiredRoleDTO dto) throws SQLException {
        repository.insert(dto);
    }

    public void removeRequirement(ShiftRequiredRoleDTO dto) throws SQLException {
        repository.delete(dto);
    }
}
