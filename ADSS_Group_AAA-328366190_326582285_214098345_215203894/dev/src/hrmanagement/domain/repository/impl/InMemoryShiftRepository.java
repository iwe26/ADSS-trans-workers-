package hrmanagement.domain.repository.impl;
import hrmanagement.domain.repository.ShiftRepository;
import hrmanagement.dal.dao.ShiftDAO;
import hrmanagement.dal.dto.ShiftDTO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ShiftRepository.
 * Caches ShiftDTO instances and delegates persistence to ShiftDAO.
 */
public class InMemoryShiftRepository implements hrmanagement.domain.repository.ShiftRepository{
    private final ShiftDAO dao;
    private final Map<Integer, ShiftDTO> cache = new ConcurrentHashMap<>();

    public InMemoryShiftRepository(ShiftDAO dao) throws SQLException {
        this.dao = dao;
        // Load all shifts into cache at startup
        List<ShiftDTO> all = dao.findAll();
        for (ShiftDTO shift : all) {
            cache.put(shift.getId(), shift);
        }
    }

    @Override
    public int insert(ShiftDTO shift) throws SQLException {
        int generatedId = dao.insert(shift);
        cache.put(generatedId, shift);
        return generatedId;
    }

    @Override
    public Optional<ShiftDTO> findById(int id) throws SQLException {
        ShiftDTO shift = cache.get(id);
        if (shift != null) return Optional.of(shift);
        shift = dao.findById(id);
        if (shift != null) {
            cache.put(id, shift);
            return Optional.of(shift);
        }
        return Optional.empty();
    }

    @Override
    public List<ShiftDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void updateManager(int shiftId, int managerId) throws SQLException {
        dao.updateManager(shiftId, managerId);
        ShiftDTO shift = cache.get(shiftId);
        if (shift != null) {
            shift.setShiftManagerId(managerId);
            cache.put(shiftId, shift);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        dao.delete(id);
        cache.remove(id);
    }
}
