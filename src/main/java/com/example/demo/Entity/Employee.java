package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Employee {
    @Id
    private String empId;

    private String name;
    private String email;
    private String department;
    private String reportingManager;
    private String role; // "ADMIN" or "EMPLOYEE"
    private String password;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Employee(String empId, String name, String email, String department, String reportingManager, String role,
            String password) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.reportingManager = reportingManager;
        this.role = role;
        this.password = password;
    }

    public Employee(){
        
    }
    
}
