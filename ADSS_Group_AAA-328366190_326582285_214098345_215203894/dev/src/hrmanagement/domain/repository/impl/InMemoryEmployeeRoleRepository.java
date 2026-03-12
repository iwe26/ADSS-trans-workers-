package hrmanagement.domain.repository.impl;

import hrmanagement.domain.repository.EmployeeRoleRepository;
import hrmanagement.dal.dao.EmployeeRoleDAO;
import hrmanagement.dal.dto.EmployeeRoleDTO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of EmployeeRoleRepository.
 * Caches EmployeeRoleDTO instances keyed by employeeId and roleName.
 */
public class InMemoryEmployeeRoleRepository implements EmployeeRoleRepository{
    private final EmployeeRoleDAO dao;
    private final Map<RoleKey, EmployeeRoleDTO> cache = new ConcurrentHashMap<>();

    public InMemoryEmployeeRoleRepository(EmployeeRoleDAO dao) throws SQLException {
        this.dao = dao;
        List<EmployeeRoleDTO> all = dao.findAll();
        for (EmployeeRoleDTO dto : all) {
            cache.put(new RoleKey(dto.getEmployeeId(), dto.getRoleId()), dto);
        }
    }

    @Override
    public List<EmployeeRoleDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<EmployeeRoleDTO> findByEmployeeId(int employeeId) throws SQLException {
        List<EmployeeRoleDTO> result = new ArrayList<>();
        for (Map.Entry<RoleKey, EmployeeRoleDTO> entry : cache.entrySet()) {
            if (entry.getKey().employeeId == employeeId) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public List<EmployeeRoleDTO> findByRoleId(int roleId) throws SQLException {
        List<EmployeeRoleDTO> result = new ArrayList<>();
        for (Map.Entry<RoleKey, EmployeeRoleDTO> entry : cache.entrySet()) {
            if (entry.getKey().roleId == roleId) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void insert(EmployeeRoleDTO role) throws SQLException {
        dao.insert(role);
        cache.put(new RoleKey(role.getEmployeeId(), role.getRoleId()), role);
    }

    @Override
    public void delete(int employeeId, int roleId) throws SQLException {
        EmployeeRoleDTO role = getRole(employeeId, roleId);
        if (role != null){
            dao.delete(role);
            cache.remove(new RoleKey(employeeId, roleId));
        }
    }

    private EmployeeRoleDTO getRole(int employeeId, int roleId) {
        return cache.get(new RoleKey(employeeId, roleId));
    }

    private static class RoleKey {
        private final int employeeId;
        private final int roleId;

        RoleKey(int employeeId, int roleId) {
            this.employeeId = employeeId;
            this.roleId = roleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RoleKey)) return false;
            RoleKey that = (RoleKey) o;
            return employeeId == that.employeeId && roleId == that.roleId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, roleId);
        }
    }
}
