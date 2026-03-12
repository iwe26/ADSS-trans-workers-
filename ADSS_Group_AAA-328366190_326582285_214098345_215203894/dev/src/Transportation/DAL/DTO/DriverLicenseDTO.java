package Transportation.DAL.DTO;

import java.util.Objects;

public class DriverLicenseDTO {
    private final int employeeId;
    private final int license;

    public DriverLicenseDTO(int employeeId, int license) {
        this.employeeId = employeeId;
        this.license = license;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getLicense() {
        return license;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DriverLicenseDTO)) return false;
        DriverLicenseDTO that = (DriverLicenseDTO) o;
        return employeeId == that.employeeId && license == that.license;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, license);
    }
}
