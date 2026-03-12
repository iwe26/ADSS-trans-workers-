package hrmanagement.domain.facade;

import hrmanagement.domain.repository.RoleRepository;
import hrmanagement.dal.dto.RoleDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
/**
 * Facade for Role-related business operations.
 */
public class RoleFacade {
    private final RoleRepository repository;

    public RoleFacade(RoleRepository repository) {
        this.repository = repository;
    }

    public List<RoleDTO> getAllRoles() throws SQLException {
        return repository.findAll();
    }

    public Optional<RoleDTO> getRoleById(int id) throws SQLException {
        return repository.findById(id);
    }

    public Optional<RoleDTO> findByName(String name) throws SQLException {
        return repository.findByName(name);
    }

    public int createRole(RoleDTO dto) throws SQLException {
        int id = repository.insert(dto);
        return id;
    }

    public void updateRole(RoleDTO dto) throws SQLException {
        repository.update(dto);
    }

    public void deleteRole(int id) throws SQLException {
        repository.delete(id);
    }
}
