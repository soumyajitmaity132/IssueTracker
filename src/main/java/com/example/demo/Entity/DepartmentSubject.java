package com.example.demo.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "department_subjects")
public class DepartmentSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key

    private String department;
    private String subject;

    public DepartmentSubject() {}

    public DepartmentSubject(String department, String subject) {
        this.department = department;
        this.subject = subject;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}
