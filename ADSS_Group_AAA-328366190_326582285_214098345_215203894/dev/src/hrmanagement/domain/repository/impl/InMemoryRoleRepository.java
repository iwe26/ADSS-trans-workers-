package hrmanagement.domain.repository.impl;
import hrmanagement.domain.repository.RoleRepository;
import hrmanagement.dal.dao.RoleDAO;
import hrmanagement.dal.dto.RoleDTO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of RoleRepository.
 * Caches RoleDTO instances and delegates persistence to RoleDAO.
 */
public class InMemoryRoleRepository implements hrmanagement.domain.repository.RoleRepository{
    private final RoleDAO dao;
    private final Map<Integer, RoleDTO> cache = new ConcurrentHashMap<>();

    public InMemoryRoleRepository(RoleDAO dao) throws SQLException {
        this.dao = dao;
        // Load all roles into cache at startup
        List<RoleDTO> all = dao.findAll();
        for (RoleDTO role : all) {
            cache.put(role.getId(), role);
        }
    }

    @Override
    public int insert(RoleDTO role) throws SQLException {
        int generatedId = dao.insert(role);
        role.setId(generatedId);
        cache.put(generatedId, role);
        return generatedId;
    }

    @Override
    public Optional<RoleDTO> findById(int id) throws SQLException {
        RoleDTO role = cache.get(id);
        if (role != null) return Optional.of(role);
        role = dao.findById(id);
        if (role != null) cache.put(id, role);
        return Optional.ofNullable(role);
    }

    @Override
    public Optional<RoleDTO> findByName(String name) throws SQLException {
        for (RoleDTO r : cache.values()) {
            if (r.getName().equals(name)) {
                return Optional.of(r);
            }
        }
        RoleDTO role = dao.findByName(name);
        if (role != null) {
            cache.put(role.getId(), role);
            return Optional.of(role);
        }
        return Optional.empty();
    }

    @Override
    public List<RoleDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void update(RoleDTO role) throws SQLException {
        dao.update(role);
        cache.put(role.getId(), role);
    }

    @Override
    public void delete(int id) throws SQLException {
        dao.delete(id);
        cache.remove(id);
    }
}
