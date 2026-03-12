package hrmanagement.domain.repository.impl;

import hrmanagement.domain.repository.ShiftAssignmentRepository;
import hrmanagement.dal.dao.ShiftAssignmentDAO;
import hrmanagement.dal.dto.ShiftAssignmentDTO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class InMemoryShiftAssignmentRepository implements hrmanagement.domain.repository.ShiftAssignmentRepository{
    private final ShiftAssignmentDAO dao;
    private final Map<AssignmentKey, ShiftAssignmentDTO> cache = new ConcurrentHashMap<>();

    public InMemoryShiftAssignmentRepository(ShiftAssignmentDAO dao) throws SQLException {
        this.dao = dao;
        List<ShiftAssignmentDTO> all = dao.findAll();
        for (ShiftAssignmentDTO dto : all) {
            cache.put(new AssignmentKey(dto.getShiftId(), dto.getEmployeeId()), dto);
        }
    }

    @Override
    public List<ShiftAssignmentDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<ShiftAssignmentDTO> findByShift(int shiftId) throws SQLException {
        List<ShiftAssignmentDTO> result = new ArrayList<>();
        for (Map.Entry<AssignmentKey, ShiftAssignmentDTO> entry : cache.entrySet()) {
            if (entry.getKey().shiftId == shiftId) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public List<ShiftAssignmentDTO> findByEmployee(int employeeId) throws SQLException {
        List<ShiftAssignmentDTO> result = new ArrayList<>();
        for (Map.Entry<AssignmentKey, ShiftAssignmentDTO> entry : cache.entrySet()) {
            if (entry.getKey().employeeId == employeeId) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void insert(ShiftAssignmentDTO dto) throws SQLException {
        dao.insert(dto);
        cache.put(new AssignmentKey(dto.getShiftId(), dto.getEmployeeId()), dto);
    }

    @Override
    public void delete(ShiftAssignmentDTO dto) throws SQLException {
        dao.delete(dto);
        cache.remove(new AssignmentKey(dto.getShiftId(), dto.getEmployeeId()));
    }

    private static class AssignmentKey {
        private final int shiftId;
        private final int employeeId;

        AssignmentKey(int shiftId, int employeeId) {
            this.shiftId = shiftId;
            this.employeeId = employeeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AssignmentKey)) return false;
            AssignmentKey that = (AssignmentKey) o;
            return shiftId == that.shiftId && employeeId == that.employeeId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(shiftId, employeeId);
        }
    }
}
