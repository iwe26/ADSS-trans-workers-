package hrmanagement.service.impl;

import hrmanagement.domain.entities.HRManager;
import hrmanagement.domain.enums.ShiftType;

import java.time.LocalDate;
import java.util.*;

public class HRServiceImpl implements hrmanagement.service.HRService{
    private final HRManager hrManager;

    /**
     * We inject the domain façade here so that the service doesn't talk directly
     * to DataStore—it goes through HRManager.
     */
    public HRServiceImpl(HRManager hrManager) {
        this.hrManager = hrManager;
    }

    @Override
    public boolean shiftExists(int shiftId) {
        return hrManager.shiftExists(shiftId);
    }

    @Override
    public List<Integer> getAssignedEmployees(int shiftId) {
        return hrManager.getAssignedEmployees(shiftId);
    }

    @Override
    public int getShiftManager(int shiftId) {
        Optional<Integer> hrManagerId = hrManager.getShiftManager(shiftId);
        return hrManagerId.orElse(-1);

    }

    @Override
    public void removeEmployeeFromShift(int shiftId, int employeeId) {
        hrManager.removeEmployeeFromShift(shiftId, employeeId);
    }

    @Override
    public void assignEmployeeToShift(int shiftId, int employeeId) {
        hrManager.assignEmployeeToShift(shiftId, employeeId);
    }

    @Override
    public void setShiftManager(int shiftId, int managerId) {
        hrManager.setShiftManager(shiftId, managerId);
    }

    @Override
    public boolean isEmployeeAvailable(int shiftId, int employeeId) {
        return hrManager.isEmployeeAvailable(shiftId, employeeId);
    }

    @Override
    public boolean isEmployeeAvailable(LocalDate date, ShiftType type, int employeeId) {
        return hrManager.isEmployeeAvailable(date, type, employeeId);
    }

    @Override
    public boolean isEmployeeAvailableCreateShift(LocalDate date, ShiftType type, int employeeId) {
        return hrManager.isEmployeeAvailableCreateShift(date, type, employeeId);
    }

    @Override
    public List<String> getShiftRoles(int shiftId) {
        return hrManager.getShiftRoles(shiftId);
    }

    @Override
    public boolean employeeHasRole(int employeeId, String role) {
        return hrManager.employeeHasRole(employeeId, role);
    }

    @Override
    public void createShift(LocalDate date, ShiftType type, List<String> requiredRoles, HashSet<Integer> employeeIds, int managerId, String siteName) {
        hrManager.createShift(date, type, requiredRoles, employeeIds, managerId, siteName);
    }

    @Override
    public boolean employeeExists(int employeeId) {
        return hrManager.employeeExists(employeeId);
    }

    @Override
    public void createEmployee(String name, int id, String bankAccount, double salary, String terms, LocalDate startDate,String branchName) {
        hrManager.createEmployee(name, id, bankAccount, salary, terms, startDate,branchName);
    }

    @Override
    public void assignRoleToEmployee(int employeeId, String role) {
        hrManager.assignRoleToEmployee(employeeId, role);
    }


}
