package hrmanagement.domain.facade;

import hrmanagement.domain.enums.ShiftType;
import hrmanagement.domain.repository.EmployeeAvailabilityRepository;
import hrmanagement.dal.dto.EmployeeAvailabilityDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public class EmployeeAvailabilityFacade {
    private final EmployeeAvailabilityRepository repository;

    public EmployeeAvailabilityFacade(EmployeeAvailabilityRepository repository) {
        this.repository = repository;
    }

    public List<EmployeeAvailabilityDTO> getAllAvailabilities() throws SQLException {
        return repository.findAll();
    }

    public Optional<EmployeeAvailabilityDTO> getAvailability(int employeeId, LocalDate date, ShiftType shiftType) throws SQLException {
        return repository.findById(employeeId, date, shiftType);
    }

    public List<EmployeeAvailabilityDTO> getAvailabilitiesForEmployee(int employeeId) throws SQLException {
        return repository.findByEmployeeId(employeeId);
    }

    public EmployeeAvailabilityDTO createAvailability(EmployeeAvailabilityDTO dto) throws SQLException {
        repository.insert(dto);
        return dto;
    }

    public void updateAvailability(EmployeeAvailabilityDTO dto) throws SQLException {
        repository.update(dto);
    }

    public void deleteAvailability(int employeeId, LocalDate date, ShiftType shiftType) throws SQLException {
        repository.delete(employeeId, date, shiftType);
    }
}
