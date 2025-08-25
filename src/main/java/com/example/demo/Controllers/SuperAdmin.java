package com.example.demo.Controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.DepartmentSubject;
import com.example.demo.Entity.Employee;
import com.example.demo.Entity.Ticket;
import com.example.demo.Repository.DepartmentSubjectRepository;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.TicketRepository;
import com.example.demo.Service.DepartmentSubjectService;

@RestController
@CrossOrigin
@RequestMapping("/api/superadmin")
public class SuperAdmin {

    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ADD EMPLOYEE OR ADMIN
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody Employee request) {
        // request.role must be either "EMPLOYEE" or "ADMIN"
        if (request.getRole() == null ||
                (!request.getRole().equalsIgnoreCase("EMPLOYEE")
                        && !request.getRole().equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.badRequest().body("Role must be EMPLOYEE or ADMIN");
        }

        Employee newUser = new Employee();
        newUser.setEmpId(request.getEmpId());
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Assume already encrypted
        newUser.setDepartment(request.getDepartment());
        newUser.setRole(request.getRole().toUpperCase());

        employeeRepo.save(newUser);
        return ResponseEntity.ok("User added successfully as " + newUser.getRole());
    }

    // VIEW EMPLOYEES OR ADMIN
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepo.findAll());
    }

    // PROMOTING EMPLOYEE to ADMIN
    @PutMapping("/promote/{id}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeRepo.findByEmpId(String.valueOf(id));

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Employee not found");
        }

        Employee employee = employeeOpt.get();
        employee.setRole("ADMIN");
        employeeRepo.save(employee);

        return ResponseEntity.ok("Employee promoted to ADMIN successfully");
    }

    // Demote Admin to Employee ----------
    @PutMapping("/demote/{id}")
    public ResponseEntity<?> demoteToEmployee(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeRepo.findByEmpId(String.valueOf(id));

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Employee not found");
        }

        Employee employee = employeeOpt.get();

        if (!"ADMIN".equalsIgnoreCase(employee.getRole())) {
            return ResponseEntity.badRequest().body("This user is not an ADMIN");
        }

        employee.setRole("EMPLOYEE");
        employeeRepo.save(employee);

        return ResponseEntity.ok("Admin demoted to EMPLOYEE successfully");
    }

    // ✅ API for SuperAdmin to add subjects to any department
    @Autowired
    private DepartmentSubjectRepository repo;

    @PostMapping("/add_subjects")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> addSubjectBySuperAdmin(@RequestBody DepartmentSubject subject) {
        DepartmentSubject savedSubject = repo.save(subject);
        return ResponseEntity.ok(savedSubject);
    }

    @Autowired
    private TicketRepository ticketRepository;

    // View All Tickets
    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> viewAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return ResponseEntity.ok(tickets);
    }

    // Adding Department
    @Autowired
    private DepartmentSubjectRepository departmentSubjectRepo;

    @PostMapping("/add_department")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> addDepartment(@RequestBody DepartmentSubject req) {

        // Check if department already exists
        if (departmentSubjectRepo.existsByDepartment(req.getDepartment())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Department already exists.");
        }

        // Save department with empty subject
        DepartmentSubject dept = new DepartmentSubject();
        dept.setDepartment(req.getDepartment());
        dept.setSubject(null); // no subject yet
        departmentSubjectRepo.save(dept);

        return ResponseEntity.ok("Department added successfully.");
    }

    // Delete a Department
    @Autowired
    private DepartmentSubjectService service;

    @DeleteMapping("/{department}")
    public ResponseEntity<String> deleteDepartment(@PathVariable String department) {
        service.deleteDepartment(department);
        return ResponseEntity.ok("Department and its subjects deleted successfully.");
    }

}
