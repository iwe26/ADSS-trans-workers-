package hrmanagement.domain.repository.impl;

import hrmanagement.dal.dao.EmployeeDAO;
import hrmanagement.dal.dto.EmployeeDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEmployeeRepository implements hrmanagement.domain.repository.EmployeeRepository{
    private final EmployeeDAO dao;
    private final Map<Integer, EmployeeDTO> cache = new ConcurrentHashMap<>();

    public InMemoryEmployeeRepository(EmployeeDAO dao) throws SQLException {
        this.dao = dao;
        // Load all employees into cache at startup
        List<EmployeeDTO> all = dao.findAll();
        for (EmployeeDTO emp : all) {
            cache.put(emp.getId(), emp);
        }
    }

    @Override
    public Optional<EmployeeDTO> findById(int id) throws SQLException {
        EmployeeDTO emp = cache.get(id);
        if (emp != null) {
            return Optional.of(emp);
        }
        // Fallback to DAO if missing
        emp = dao.findById(id);
        if (emp != null) {
            cache.put(id, emp);
            return Optional.of(emp);
        }
        return Optional.empty();
    }

    @Override
    public List<EmployeeDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void insert(EmployeeDTO emp) throws SQLException {
        dao.insert(emp);
        cache.put(emp.getId(), emp);
    }

    @Override
    public void update(EmployeeDTO emp) throws SQLException {
        dao.update(emp);
        cache.put(emp.getId(), emp);
    }

    @Override
    public void delete(int id) throws SQLException {
        dao.delete(id);
        cache.remove(id);
    }
}
