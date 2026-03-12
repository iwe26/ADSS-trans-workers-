package hrmanagement.domain.facade;
import hrmanagement.domain.repository.ShiftRepository;
import hrmanagement.dal.dto.ShiftDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Facade for Shift-related business operations.
 */
public class ShiftFacade {
    private final ShiftRepository repository;

    public ShiftFacade(ShiftRepository repository) {
        this.repository = repository;
    }

    public List<ShiftDTO> getAllShifts() throws SQLException {
        return repository.findAll();
    }

    public Optional<ShiftDTO> getShiftById(int id) throws SQLException {
        return repository.findById(id);
    }

    public int createShift(ShiftDTO dto) throws SQLException {
        int id = repository.insert(dto);
        return id;
    }

    public void updateShiftManager(int shiftId, int managerId) throws SQLException {
        repository.updateManager(shiftId, managerId);
    }

    public void deleteShift(int id) throws SQLException {
        repository.delete(id);
    }
}
