package hrmanagement.domain.repository;
import hrmanagement.dal.dto.ShiftAssignmentDTO;
import java.sql.SQLException;
import java.util.List;
public interface ShiftAssignmentRepository {
    List<ShiftAssignmentDTO> findAll() throws SQLException;
    List<ShiftAssignmentDTO> findByShift(int shiftId) throws SQLException;
    List<ShiftAssignmentDTO> findByEmployee(int employeeId) throws SQLException;
    void insert(ShiftAssignmentDTO dto) throws SQLException;
    void delete(ShiftAssignmentDTO dto) throws SQLException;
}
