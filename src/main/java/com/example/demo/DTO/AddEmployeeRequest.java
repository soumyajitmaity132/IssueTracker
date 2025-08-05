package com.example.demo.DTO;

import java.time.LocalDateTime;

public class AddEmployeeRequest {
    private String empId;
    private String name;
    private String email;
    private String department;
    private String reportingManager;
    private String role;
    private String password;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    private LocalDateTime createdAt;
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    // getters and setters
    public String getEmpId() {
        return empId;
    }
    public void setEmpId(String empId) {
        this.empId = empId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getReportingManager() {
        return reportingManager;
    }
    public void setReportingManager(String reportingManager) {
        this.reportingManager = reportingManager;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public AddEmployeeRequest(String empId, String name, String email, String department, String reportingManager,
            String role, LocalDateTime createdAt,String password) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.reportingManager = reportingManager;
        this.role = role;
        this.createdAt = createdAt;
        this.password=password;
    }
    

    
}
