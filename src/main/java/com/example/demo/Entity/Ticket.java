package com.example.demo.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketNo;

    private String employeeName;
    private String empId;
    private String department;
    private String subject;
    @Column(length = 500)
    private String detailedMessage;

    private String assignee;
    private String status;
    private String priority = "Medium";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    public Long getTicketNo() {
        return ticketNo;
    }
    public void setTicketNo(Long ticketNo) {
        this.ticketNo = ticketNo;
    }
    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public String getEmpId() {
        return empId;
    }
    public void setEmpId(String empId) {
        this.empId = empId;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getDetailedMessage() {
        return detailedMessage;
    }
    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }
    public String getAssignee() {
        return assignee;
    }
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }


    public Ticket(){
        
    }
    
    public Ticket(Long ticketNo, String employeeName, String empId, String department, String subject,
            String detailedMessage, String assignee, String status, String priority, LocalDateTime createdAt) {
        this.ticketNo = ticketNo;
        this.employeeName = employeeName;
        this.empId = empId;
        this.department = department;
        this.subject = subject;
        this.detailedMessage = detailedMessage;
        this.assignee = assignee;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
