package hrmanagement.domain.repository.impl;
import hrmanagement.domain.repository.ShiftRequiredRoleRepository;
import hrmanagement.dal.dao.ShiftRequiredRoleDAO;
import hrmanagement.dal.dto.ShiftRequiredRoleDTO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ShiftRequiredRoleRepository.
 * Caches ShiftRequiredRoleDTO keyed by (shiftId, roleId).
 */
public class InMemoryShiftRequiredRoleRepository implements hrmanagement.domain.repository.ShiftRequiredRoleRepository{
    private final ShiftRequiredRoleDAO dao;
    private final Map<RequirementKey, ShiftRequiredRoleDTO> cache = new ConcurrentHashMap<>();

    public InMemoryShiftRequiredRoleRepository(ShiftRequiredRoleDAO dao) throws SQLException {
        this.dao = dao;
        List<ShiftRequiredRoleDTO> all = dao.findAll();
        for (ShiftRequiredRoleDTO dto : all) {
            cache.put(new RequirementKey(dto.getShiftId(), dto.getRoleId()), dto);
        }
    }

    @Override
    public List<ShiftRequiredRoleDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<ShiftRequiredRoleDTO> findByShift(int shiftId) throws SQLException {
        List<ShiftRequiredRoleDTO> result = new ArrayList<>();
        for (Map.Entry<RequirementKey, ShiftRequiredRoleDTO> entry : cache.entrySet()) {
            if (entry.getKey().shiftId == shiftId) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public List<ShiftRequiredRoleDTO> findByRole(int roleId) throws SQLException {
        List<ShiftRequiredRoleDTO> result = new ArrayList<>();
        for (Map.Entry<RequirementKey, ShiftRequiredRoleDTO> entry : cache.entrySet()) {
            if (entry.getKey().roleId == roleId) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void insert(ShiftRequiredRoleDTO dto) throws SQLException {
        dao.insert(dto);
        cache.put(new RequirementKey(dto.getShiftId(), dto.getRoleId()), dto);
    }

    @Override
    public void delete(ShiftRequiredRoleDTO dto) throws SQLException {
        dao.delete(dto);
        cache.remove(new RequirementKey(dto.getShiftId(), dto.getRoleId()));
    }

    private static class RequirementKey {
        private final int shiftId;
        private final int roleId;

        RequirementKey(int shiftId, int roleId) {
            this.shiftId = shiftId;
            this.roleId = roleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RequirementKey)) return false;
            RequirementKey that = (RequirementKey) o;
            return shiftId == that.shiftId && roleId == that.roleId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(shiftId, roleId);
        }
    }
}
