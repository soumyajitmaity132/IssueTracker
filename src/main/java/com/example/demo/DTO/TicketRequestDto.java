package com.example.demo.DTO;


public class TicketRequestDto {
    private String department;
    private String description;
    private String employeeEmail;
    private String priority;
    private String severity;

    public TicketRequestDto() {}

    public TicketRequestDto(String department, String description, String employeeEmail, String priority, String severity) {
        this.department = department;
        this.description = description;
        this.employeeEmail = employeeEmail;
        this.priority = priority;
        this.severity = severity;
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    @Override
    public String toString() {
        return "TicketRequestDto{" +
                "department='" + department + '\'' +
                ", description='" + description + '\'' +
                ", employeeEmail='" + employeeEmail + '\'' +
                ", priority='" + priority + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
