// src/hrmanagement/domain/services/HRService.java
package hrmanagement.service;

import hrmanagement.domain.enums.ShiftType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface HRService {
    // Shift queries & mutations
    boolean shiftExists(int shiftId);
    List<Integer> getAssignedEmployees(int shiftId);
    int getShiftManager(int shiftId);
    void removeEmployeeFromShift(int shiftId, int employeeId);
    void assignEmployeeToShift(int shiftId, int employeeId);
    void setShiftManager(int shiftId, int managerId);
    boolean isEmployeeAvailable(int shiftId, int employeeId);
    boolean isEmployeeAvailable(LocalDate date, ShiftType type, int employeeId);
    List<String> getShiftRoles(int shiftId);
    boolean employeeHasRole(int employeeId, String role);
    void createShift(LocalDate date, ShiftType type,
                     List<String> requiredRoles, HashSet<Integer> employeeIds,
                     int managerId,String siteName);

    // Employee queries & mutations
    boolean employeeExists(int employeeId);
    void createEmployee(String name, int id, String bankAccount,
                        double salary, String terms, LocalDate startDate,String branchName);
    void assignRoleToEmployee(int employeeId, String role);
    boolean isEmployeeAvailableCreateShift(LocalDate date, ShiftType type, int employeeId);
}
