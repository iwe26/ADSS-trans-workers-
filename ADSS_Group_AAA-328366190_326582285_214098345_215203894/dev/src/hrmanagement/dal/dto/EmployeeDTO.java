package hrmanagement.dal.dto;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeeDTO {
    private int id; // National ID
    private String name;
    private String bankAccount;
    private double salary;
    private String employmentTerms;
    private LocalDate startDate;
    private boolean isActive;
    private String branchAddress;

    public EmployeeDTO(int id, String name, String bankAccount, double salary,
                       String employmentTerms, LocalDate startDate,
                       boolean isActive, String branchAddress) {
        this.id = id;
        this.name = name;
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.employmentTerms = employmentTerms;
        this.startDate = startDate;
        this.isActive = isActive;
        this.branchAddress = branchAddress;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public double getSalary() {
        return salary;
    }

    public String getEmploymentTerms() {
        return employmentTerms;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setEmploymentTerms(String employmentTerms) {
        this.employmentTerms = employmentTerms;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setBranchAddress(String branchName) {
        this.branchAddress = branchName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeDTO)) return false;
        EmployeeDTO that = (EmployeeDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
