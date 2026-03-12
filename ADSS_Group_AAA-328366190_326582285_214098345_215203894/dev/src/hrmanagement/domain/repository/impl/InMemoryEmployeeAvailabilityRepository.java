package hrmanagement.domain.repository.impl;

import hrmanagement.dal.dao.EmployeeAvailabilityDAO;
import hrmanagement.dal.dto.EmployeeAvailabilityDTO;
import hrmanagement.domain.enums.ShiftType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEmployeeAvailabilityRepository implements hrmanagement.domain.repository.EmployeeAvailabilityRepository{
    private final EmployeeAvailabilityDAO dao;
    private final Map<AvailabilityKey, EmployeeAvailabilityDTO> cache = new ConcurrentHashMap<>();

    public InMemoryEmployeeAvailabilityRepository(EmployeeAvailabilityDAO dao) throws SQLException {
        this.dao = dao;
        List<EmployeeAvailabilityDTO> all = dao.findAll();
        for (EmployeeAvailabilityDTO dto : all) {
            AvailabilityKey key = new AvailabilityKey(dto.getEmployeeId(), dto.getDate(), dto.getShiftType());
            cache.put(key, dto);
        }
    }

    @Override
    public Optional<EmployeeAvailabilityDTO> findById(int employeeId, LocalDate date, ShiftType shiftType) throws SQLException {
        AvailabilityKey key = new AvailabilityKey(employeeId, date, shiftType);
        EmployeeAvailabilityDTO dto = cache.get(key);
        if (dto != null) return Optional.of(dto);
        dto = dao.getAvailability(employeeId, date, shiftType);
        if (dto != null) {
            cache.put(key, dto);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<EmployeeAvailabilityDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public List<EmployeeAvailabilityDTO> findByEmployeeId(int employeeId) throws SQLException {
        List<EmployeeAvailabilityDTO> list = new ArrayList<>();
        for (Map.Entry<AvailabilityKey, EmployeeAvailabilityDTO> entry : cache.entrySet()) {
            if (entry.getKey().getEmployeeId() == employeeId) {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    @Override
    public void insert(EmployeeAvailabilityDTO availability) throws SQLException {
        dao.insert(availability);
        AvailabilityKey key = new AvailabilityKey(availability.getEmployeeId(), availability.getDate(), availability.getShiftType());
        cache.put(key, availability);
    }

    @Override
    public void update(EmployeeAvailabilityDTO availability) throws SQLException {
        dao.insert(availability);
        AvailabilityKey key = new AvailabilityKey(availability.getEmployeeId(), availability.getDate(), availability.getShiftType());
        cache.put(key, availability);
    }

    @Override
    public void delete(int employeeId, LocalDate date, ShiftType shiftType) throws SQLException {
        EmployeeAvailabilityDTO dto = dao.getAvailability(employeeId, date, shiftType);
        if (dto != null){
            dao.delete(dto);
            cache.remove(new AvailabilityKey(employeeId, date, shiftType));
        }

    }

    private static class AvailabilityKey {
        private final int employeeId;
        private final LocalDate date;
        private final ShiftType shiftType;

        public AvailabilityKey(int employeeId, LocalDate date, ShiftType shiftType) {
            this.employeeId = employeeId;
            this.date = date;
            this.shiftType = shiftType;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AvailabilityKey)) return false;
            AvailabilityKey that = (AvailabilityKey) o;
            return employeeId == that.employeeId &&
                    date.equals(that.date) &&
                    shiftType.equals(that.shiftType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, date, shiftType);
        }
    }
}
