package hrmanagement.domain.repository;

import hrmanagement.dal.dto.RoleDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public interface RoleRepository {
    /**
     * Insert a new role and return its generated ID.
     */
    int insert(RoleDTO role) throws SQLException;

    /**
     * Find a role by its ID.
     */
    Optional<RoleDTO> findById(int id) throws SQLException;

    /**
     * Find a role by its name.
     */
    Optional<RoleDTO> findByName(String name) throws SQLException;

    /**
     * List all roles.
     */
    List<RoleDTO> findAll() throws SQLException;

    /**
     * Update an existing role.
     */
    void update(RoleDTO role) throws SQLException;

    /**
     * Delete a role by its ID.
     */
    void delete(int id) throws SQLException;
}
