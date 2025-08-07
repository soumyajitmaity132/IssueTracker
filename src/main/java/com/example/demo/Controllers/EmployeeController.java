package com.example.demo.Controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.AddEmployeeRequest;
import com.example.demo.Entity.Employee;
import com.example.demo.Repository.DepartmentSubjectRepository;
import com.example.demo.Repository.EmployeeRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class EmployeeController {

    @Autowired private EmployeeRepository employeeRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/get_employees")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Employee>> getEmployeesInSameDepartment() {
        // Get the logged-in admin's email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Fetch admin's details
        Employee admin = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Get all employees in the same department, excluding the admin themselves
        List<Employee> employees = employeeRepo.findByDepartment(admin.getDepartment())
                .stream()
                .filter(e -> !e.getEmail().equalsIgnoreCase(admin.getEmail()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(employees);
    }

    @PostMapping("/add_employees")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> add(@RequestBody AddEmployeeRequest req) {
        // Get logged-in admin's email from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();

        // Find the admin's full record
        Employee admin = employeeRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Prevent duplicate employee creation
        if (employeeRepo.existsById(req.getEmpId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Employee already exists.");
        }

        // Create new employee and set only allowed fields
        Employee emp = new Employee();
        emp.setEmpId(req.getEmpId());
        emp.setName(req.getName());
        emp.setEmail(req.getEmail());
        emp.setDepartment(admin.getDepartment()); // Forcefully set to admin's department
        emp.setReportingManager(req.getReportingManager());
        emp.setRole(req.getRole());
        emp.setPassword(passwordEncoder.encode(req.getPassword()));

        employeeRepo.save(emp);

        return ResponseEntity.ok("Employee added successfully to " + admin.getDepartment() + " department.");
    }

}
