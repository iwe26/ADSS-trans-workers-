package hrmanagement.domain.repository;
import hrmanagement.dal.dto.ShiftRequiredRoleDTO;
import java.sql.SQLException;
import java.util.List;

/**
 * Repository interface for shift-required-role operations.
 */
public interface ShiftRequiredRoleRepository {
    /**
     * Retrieve all shift-to-role requirements.
     */
    List<ShiftRequiredRoleDTO> findAll() throws SQLException;

    /**
     * Find requirements by shift ID.
     */
    List<ShiftRequiredRoleDTO> findByShift(int shiftId) throws SQLException;

    /**
     * Find requirements by role ID.
     */
    List<ShiftRequiredRoleDTO> findByRole(int roleId) throws SQLException;

    /**
     * Add a required role to a shift.
     */
    void insert(ShiftRequiredRoleDTO dto) throws SQLException;

    /**
     * Remove a required role from a shift.
     */
    void delete(ShiftRequiredRoleDTO dto) throws SQLException;
}
