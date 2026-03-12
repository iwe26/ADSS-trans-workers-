package hrmanagement.domain.repository;

import hrmanagement.dal.dto.EmployeeAvailabilityDTO;
import hrmanagement.domain.enums.ShiftType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeAvailabilityRepository {
    Optional<EmployeeAvailabilityDTO> findById(int employeeId, LocalDate date, ShiftType shiftType) throws SQLException;
    List<EmployeeAvailabilityDTO> findAll() throws SQLException;
    List<EmployeeAvailabilityDTO> findByEmployeeId(int employeeId) throws SQLException;
    void insert(EmployeeAvailabilityDTO availability) throws SQLException;
    void update(EmployeeAvailabilityDTO availability) throws SQLException;
    void delete(int employeeId, LocalDate date, ShiftType shiftType) throws SQLException;
}

