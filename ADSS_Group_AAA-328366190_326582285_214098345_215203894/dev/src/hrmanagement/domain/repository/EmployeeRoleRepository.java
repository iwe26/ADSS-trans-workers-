package hrmanagement.domain.repository;

import hrmanagement.dal.dto.EmployeeRoleDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeRoleRepository {
    List<EmployeeRoleDTO> findAll() throws SQLException;
    List<EmployeeRoleDTO> findByEmployeeId(int employeeId) throws SQLException;
    List<EmployeeRoleDTO> findByRoleId(int roleId) throws SQLException;

    void insert(EmployeeRoleDTO role) throws SQLException;
    void delete(int employeeId, int roleId) throws SQLException;
}
