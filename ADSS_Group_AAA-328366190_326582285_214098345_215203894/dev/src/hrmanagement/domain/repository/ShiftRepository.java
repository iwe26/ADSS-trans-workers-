package hrmanagement.domain.repository;

import hrmanagement.dal.dto.ShiftDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
public interface ShiftRepository {
    /**
     * Insert a new shift and return its generated ID.
     */
    int insert(ShiftDTO shift) throws SQLException;

    /**
     * Find a shift by its ID.
     */
    Optional<ShiftDTO> findById(int id) throws SQLException;

    /**
     * Retrieve all shifts.
     */
    List<ShiftDTO> findAll() throws SQLException;

    /**
     * Update the manager for a given shift.
     */
    void updateManager(int shiftId, int managerId) throws SQLException;

    /**
     * Delete a shift by its ID.
     */
    void delete(int id) throws SQLException;
}
