package com.example.demo.Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.DTO.AddEmployeeRequest;
import com.example.demo.DTO.EditSubjectRequest;
import com.example.demo.Entity.Employee;
import com.example.demo.Repository.DepartmentSubjectRepository;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Service.DepartmentSubjectService;
import com.example.demo.Service.EmployeeService;
import com.example.demo.Util.ExcelHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class EmployeeController {

    @Autowired private EmployeeRepository employeeRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmployeeService employeeService;

    //UPLOAD EMPLOYEEE DATA in MASS through EXCEL
    @PostMapping("/employees_data/upload")
    public ResponseEntity<String> uploadEmployeesFromLink(@RequestParam("sheetUrl") String sheetUrl) {
    try {
        // Download sheet as CSV
        URL url = new URL(sheetUrl);
                int count = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            List<Employee> employees = ExcelHelper.csvToEmployees(reader);
            count = employeeService.saveAllEmployees(employees);
        }
        return ResponseEntity.ok(count + " employees uploaded from Google Sheet successfully!");    } 
        catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Failed to process Google Sheet: " + e.getMessage());
    }
}



    @GetMapping("/get_employees")
    public ResponseEntity<List<Employee>> getEmployeesInSameDepartment() {
        // Get the logged-in admin's email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        

        // Fetch admin's details
        Employee admin = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if("SUPER_ADMIN".equalsIgnoreCase(admin.getRole())){
            List<Employee> emp=employeeRepo.findAll();
            return ResponseEntity.ok(emp);
        }

        // Get all employees in the same department, excluding the admin themselves
        List<Employee> employees = employeeRepo.findByDepartment(admin.getDepartment())
                .stream()
                .filter(e -> !e.getEmail().equalsIgnoreCase(admin.getEmail()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(employees);
    }


    //EDIT EMPLOYEE DETAILS
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editEmployee(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {

       Employee emp = employeeRepo.findByEmpId(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();   
        Employee emp1 = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));     
        String currentUserRole = emp1.getRole();
        updates.forEach((field, value) -> {
            switch (field) {
                case "name":
                    emp.setName((String) value);
                    break;
                case "email":
                    emp.setEmail((String) value);
                    break;
                case "password":
                    emp.setPassword(passwordEncoder.encode((String) value));
                    break;
                case "department":
                    // Only SuperAdmin can update department
                    if (currentUserRole.equals("SUPER_ADMIN")) {
                        emp.setDepartment((String) value);
                    }
                    break;
                case "role":
                    emp.setRole((String) value);
                    break;
                case "reportingManager":
                     emp.setReportingManager((String) value);
                     break;

                default:
                    // ignore unknown fields
                    break;
            }
        });

        employeeRepo.save(emp);
        return ResponseEntity.ok("Employee updated successfully!");
    }





    /// ADD NEW EMPLOYEE
    @PostMapping("/add-employee")
    public ResponseEntity<?> addEmployee(@RequestBody Employee request) {
        // Get logged-in user's email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = auth.getName();

        // Fetch logged-in employee (Admin or SuperAdmin)
        Employee loggedInUser = employeeRepo.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        // Validate role of the new user
        if (request.getRole() == null ||
            (!request.getRole().equalsIgnoreCase("EMPLOYEE")
             && !request.getRole().equalsIgnoreCase("ADMIN"))) {
            return ResponseEntity.badRequest().body("Role must be EMPLOYEE or ADMIN");
        }

        // Prevent duplicate employees
        if (employeeRepo.existsById(request.getEmpId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Employee already exists.");
        }

        // Create new employee
        Employee newUser = new Employee();
        newUser.setEmpId(request.getEmpId());
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));  
        newUser.setReportingManager(request.getReportingManager());
        newUser.setRole(request.getRole().toUpperCase());

        // Admin can only add employee to their own department
        if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {
            newUser.setDepartment(loggedInUser.getDepartment());
        }
        // Super Admin can assign to any department
        else if (loggedInUser.getRole().equalsIgnoreCase("SUPER_ADMIN")) {
            newUser.setDepartment(request.getDepartment());
        }

        employeeRepo.save(newUser);

        return ResponseEntity.ok("User added successfully as " + newUser.getRole() +
                " in " + newUser.getDepartment() + " department.");
    }


    //Delete a SUbject from a department
    @Autowired
    private DepartmentSubjectService service;

    @DeleteMapping("/{department}/subjects/{subject}")
    public ResponseEntity<String> deleteSubject(
            @PathVariable String department,
            @PathVariable String subject) {

        service.deleteSubjectFromDepartment(department, subject);
        return ResponseEntity.ok("Subject '" + subject + "' deleted from department '" + department + "'");
    }

    //Edit a Subject
    @PutMapping("/edit-subject")
    public ResponseEntity<String> editSubject(@RequestBody EditSubjectRequest request) {
        service.editSubjectInDepartment(
                request.getDepartment(),
                request.getOldSubject(),
                request.getNewSubject()
        );
        return ResponseEntity.ok("Subject '" + request.getOldSubject() +
                "' updated to '" + request.getNewSubject() +
                "' in department '" + request.getDepartment() + "'");
    }

    
    // Delete employee by ID
    @DeleteMapping("/delete/{empId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String empId) {
        // Find the requester (Admin or Superadmin)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();   
        Employee requester = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));     
        String requesterOpt = requester.getRole();        
        if (requesterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Requester not found");
        }
         

        // Find the employee to delete
        Optional<Employee> employeeOpt = employeeRepo.findByEmpId(empId);
        if (!employeeOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found");
        }

        Employee employeeToDelete = employeeOpt.get();

        // ✅ Superadmin can delete anyone
        if ("SUPER_ADMIN".equalsIgnoreCase(requesterOpt)) {
            employeeRepo.delete(employeeToDelete);
            return ResponseEntity.ok("Employee deleted by Superadmin");
        }

        // ✅ Admin can delete only from same department
        if ("ADMIN".equalsIgnoreCase(requesterOpt)) {
            if (requester.getDepartment().equalsIgnoreCase(employeeToDelete.getDepartment())) {
                employeeRepo.delete(employeeToDelete);
                return ResponseEntity.ok("Employee deleted by Admin from same department");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Admin can delete only from own department");
            }
        }

        // ❌ Others cannot delete
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You do not have permission to delete employees");
    }



}
