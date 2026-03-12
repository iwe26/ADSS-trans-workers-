package Transportation.ServiceLayer;

import Transportation.BusinessLayer.BLs.DriverBL;
import Transportation.BusinessLayer.Resources.LicenseType;
import Transportation.BusinessLayer.Resources.Response;
import Transportation.DAL.DAO.DriverLicenseDAO;
import Transportation.DAL.DTO.DriverLicenseDTO;
import hrmanagement.dal.dto.EmployeeDTO;
import hrmanagement.service.TransportationHRService;
import hrmanagement.service.impl.TransportationHRServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

public class DriverService {
    private final Map<Integer, DriverBL> drivers;
    private final DriverLicenseDAO driverLicenseDAO;
    private final TransportationHRService hrService;

    public DriverService(Map<Integer, DriverBL> drivers , TransportationHRService hrService) {
        this.drivers = drivers;
        this.driverLicenseDAO = new DriverLicenseDAO();// uses internal DAO instance
        this.hrService = hrService;
        loadDriversFromDB();
    }

    public Response addLicenseToDriver(int employeeId, LicenseType licenseType) {
        try {
            DriverBL driver = drivers.get(employeeId);
            if (driver == null) {
                driver = new DriverBL(employeeId);
                drivers.put(employeeId, driver);
            }
            driver.addLicense(licenseType);
            return new Response("License added successfully", null);
        } catch (Exception e) {
            return new Response(null, "Failed to add license: " + e.getMessage());
        }
    }

    public Response getDriver(int employeeId) {
        try {
            DriverBL driver = drivers.get(employeeId);
            if (driver == null) {
                return new Response(null, "Driver not found.");
            }
            return new Response(driver, null);
        } catch (Exception e) {
            return new Response(null, "Failed to get driver: " + e.getMessage());
        }
    }

    public Response getAvailableDriverNamesWithLicenses(LocalDateTime start, LocalDateTime end) {
        try {
            List<EmployeeDTO> availableEmployees = hrService.getAvailableDrivers(start, end);

            List<String> result = new ArrayList<>();
            for (EmployeeDTO employee : availableEmployees) {
                int id = employee.getId();
                String name = employee.getName();

                DriverBL driver = drivers.get(id);
                if (driver != null) {
                    Set<LicenseType> licenses = new HashSet<>(driver.getLicenseTypes());
                    result.add("ID: " + id + " - Name: " + name + " - Licenses: " + licenses);
                }
            }

            return new Response(result, null);
        } catch (Exception e) {
            return new Response(null, "Failed to retrieve driver list: " + e.getMessage());
        }
    }

    private void loadDriversFromDB() {
        try {
            List<DriverLicenseDTO> allRecords = driverLicenseDAO.getAllRecords();
            Map<Integer, List<LicenseType>> tempMap = new HashMap<>();

            for (DriverLicenseDTO dto : allRecords) {
                int empId = dto.getEmployeeId();
                LicenseType license = LicenseType.values()[dto.getLicense()];

                tempMap.putIfAbsent(empId, new ArrayList<>());
                tempMap.get(empId).add(license);
            }

            for (Map.Entry<Integer, List<LicenseType>> entry : tempMap.entrySet()) {
                int empId = entry.getKey();
                List<LicenseType> licenses = entry.getValue();
                drivers.put(empId, new DriverBL(empId, licenses));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load driver licenses: " + e.getMessage(), e);
        }
    }
}
