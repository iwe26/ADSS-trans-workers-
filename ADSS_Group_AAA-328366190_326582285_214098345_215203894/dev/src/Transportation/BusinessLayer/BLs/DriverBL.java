package Transportation.BusinessLayer.BLs;

import Transportation.BusinessLayer.Resources.LicenseType;
import Transportation.DAL.DAO.DriverLicenseDAO;
import Transportation.DAL.DTO.DriverLicenseDTO;

import java.util.ArrayList;
import java.util.List;

public class DriverBL {
    private final int employeeId;
    private final DriverLicenseDAO driverLicenseDAO;
    private final List<LicenseType> licenseTypes;

    // Constructor used when loading or creating a driver with existing licenses
    public DriverBL(int employeeId, List<LicenseType> licenseTypes) {
        this.employeeId = employeeId;
        this.driverLicenseDAO = new DriverLicenseDAO();
        this.licenseTypes = licenseTypes;
    }

    // Constructor used when creating a new driver (no licenses yet)
    public DriverBL(int employeeId) {
        this.employeeId = employeeId;
        this.driverLicenseDAO = new DriverLicenseDAO();
        this.licenseTypes = new ArrayList<>();
        // No licenses to persist at this point
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public List<LicenseType> getLicenseTypes() {
        return new ArrayList<>(licenseTypes);
    }

    public boolean hasLicense(LicenseType licenseType) {
        return licenseTypes.contains(licenseType);
    }

    public void addLicense(LicenseType licenseType) {
        if (!licenseTypes.contains(licenseType)) {
            licenseTypes.add(licenseType);
            persistLicense(licenseType);
        }
    }

    private void persistLicense(LicenseType licenseType) {
        try {
            DriverLicenseDTO dto = new DriverLicenseDTO(employeeId, licenseType.ordinal());
            driverLicenseDAO.insert(dto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist license: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "Driver ID: " + employeeId + " | Licenses: " + licenseTypes;
    }
}
