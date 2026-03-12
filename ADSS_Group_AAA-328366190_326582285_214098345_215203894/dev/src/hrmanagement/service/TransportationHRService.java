package hrmanagement.service;

import Transportation.BusinessLayer.BLs.DriverBL;
import hrmanagement.dal.dto.EmployeeDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TransportationHRService {

    boolean isWarehouseEmployeeAssignToTransportation(LocalDateTime shiftTime,String branchName);

    List<EmployeeDTO> getAvailableDrivers(LocalDateTime transportationStartTime, LocalDateTime transportationEndTime);

    boolean assignDriverToShift(int driverid , LocalDateTime shiftTime,String branchName);
}
