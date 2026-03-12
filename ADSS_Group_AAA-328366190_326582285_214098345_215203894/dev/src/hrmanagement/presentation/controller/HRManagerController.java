package hrmanagement.presentation.controller;

import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.dal.dto.ShiftDTO;
import hrmanagement.domain.enums.ShiftType;
import hrmanagement.dal.dto.ShiftRequiredRoleDTO;
import hrmanagement.service.HRService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class HRManagerController {
    private final HRService service;

    public HRManagerController(HRService service) {
        this.service = service;
    }

    public boolean shiftExists(int shiftId)  {
        return service.shiftExists(shiftId);
    }

    public List<Integer> getAssignedEmployees(int shiftId){
        return service.getAssignedEmployees(shiftId);
    }

    public int getShiftManager(int shiftId) {
        return service.getShiftManager(shiftId);
    }

    public void removeEmployeeFromShift(int shiftId, int employeeId)  {
        service.removeEmployeeFromShift(shiftId, employeeId);
    }

    public void assignEmployeeToShift(int shiftId, int employeeId)  {
        service.assignEmployeeToShift(shiftId, employeeId);
    }

    public boolean isEmployeeAvailable(int shiftId, int employeeId)  {
        return service.isEmployeeAvailable(shiftId, employeeId);
    }

    public List<String> getShiftRoles(int shiftId)  {
        return service.getShiftRoles(shiftId);
    }

    public void setShiftManager(int shiftId, int managerId)  {
        service.setShiftManager(shiftId, managerId);
    }

    public void createShift(LocalDate date,
                            ShiftType type,
                            List<String> requiredRoles,
                            HashSet<Integer> employeeIds,
                            int managerId,
                            String siteAddress)  {
        service.createShift(date, type, requiredRoles, employeeIds, managerId, siteAddress);
    }



    public void createEmployee(String name,
                               int id,
                               String bankAccount,
                               double salary,
                               String terms,
                               LocalDate startDate,
                               String siteAddress)  {
        service.createEmployee(name, id, bankAccount, salary, terms, startDate, siteAddress);
    }

    public boolean employeeExists(int employeeId)  {
        return service.employeeExists(employeeId);
    }

    public void assignRoleToEmployee(int employeeId, String role)  {
        service.assignRoleToEmployee(employeeId, role);
    }

    public boolean employeeHasRole(int employeeId, String role)  {
        return service.employeeHasRole(employeeId, role);
    }
    public boolean isEmployeeAvailableCreateShift(LocalDate date, ShiftType type, int employeeId) {
        return service.isEmployeeAvailableCreateShift(date, type, employeeId);
    }


}
