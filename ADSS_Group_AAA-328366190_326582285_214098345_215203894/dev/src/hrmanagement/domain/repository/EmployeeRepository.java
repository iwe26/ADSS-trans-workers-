package hrmanagement.domain.repository;

import hrmanagement.dal.dto.EmployeeDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Optional<EmployeeDTO> findById(int id) throws SQLException;
    List<EmployeeDTO> findAll() throws SQLException;
    void insert(EmployeeDTO emp) throws SQLException;
    void update(EmployeeDTO emp) throws SQLException;
    void delete(int id) throws SQLException;
}
