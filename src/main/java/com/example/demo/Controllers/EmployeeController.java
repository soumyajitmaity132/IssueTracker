package com.example.demo.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<Employee> list() {
        return employeeRepo.findAll();
    }

    @PostMapping("/add_employees")
    public ResponseEntity<?> add(@RequestBody AddEmployeeRequest req) {
        if (employeeRepo.existsById(req.getEmpId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Employee exists.");
        }
        Employee emp = new Employee();
        emp.setEmpId(req.getEmpId());
        emp.setName(req.getName());
        emp.setEmail(req.getEmail());
        emp.setDepartment(req.getDepartment());
        emp.setReportingManager(req.getReportingManager());
        emp.setRole(req.getRole());
        emp.setPassword(passwordEncoder.encode(req.getPassword()));
        employeeRepo.save(emp);
        return ResponseEntity.ok("Employee added");
    }

}
