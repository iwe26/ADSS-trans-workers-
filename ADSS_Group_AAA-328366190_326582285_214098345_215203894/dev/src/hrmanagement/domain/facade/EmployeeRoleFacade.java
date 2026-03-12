package hrmanagement.domain.facade;

import hrmanagement.domain.repository.EmployeeRoleRepository;
import hrmanagement.dal.dto.EmployeeRoleDTO;

import java.sql.SQLException;
import java.util.List;
public class EmployeeRoleFacade {
    private final EmployeeRoleRepository repository;

    public EmployeeRoleFacade(EmployeeRoleRepository repository) {
        this.repository = repository;
    }

    public List<EmployeeRoleDTO> getAllRoles() throws SQLException {
        return repository.findAll();
    }

    public List<EmployeeRoleDTO> getRolesForEmployee(int employeeId) throws SQLException {
        return repository.findByEmployeeId(employeeId);
    }

    public List<EmployeeRoleDTO> getRolesByRoleId(int roleId) throws SQLException {
        return repository.findByRoleId(roleId);
    }

    public void createRole(EmployeeRoleDTO dto) throws SQLException {
        repository.insert(dto);
    }

    public void deleteRole(int employeeId, int roleId) throws SQLException {
        repository.delete(employeeId, roleId);
    }
}
