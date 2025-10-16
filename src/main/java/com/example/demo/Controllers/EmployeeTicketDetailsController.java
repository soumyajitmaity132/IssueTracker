package com.example.demo.Controllers;

import com.example.demo.Controllers.impl.EmployeTicketDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/employeeticket")
@CrossOrigin
public class EmployeeTicketDetailsController {

    @Autowired
    private EmployeTicketDetailsImpl employeeService;

    // ✅ Get Employee by single EmpId
    @GetMapping("/{empId}")
    public ResponseEntity<?> getEmployeeById(@PathVariable String empId) {
        return employeeService.getEmployeeById(empId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee with ID " + empId + " not found"));
    }

    // ✅ Get Employee Names by List of EmpIds
    @PostMapping("/names")
    public ResponseEntity<?> getEmployeeNamesByIds(@RequestBody List<String> empIds) {
        List<String> employeeNames = employeeService.getEmployeeNamesByIds(empIds);

        if (employeeNames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No employees found for given IDs");
        }

        return ResponseEntity.ok(employeeNames);
    }

    // ✅ Department-wise Summary
    @GetMapping("/tickets-summary/{department}")
    public ResponseEntity<?> getDepartmentTicketSummary(@PathVariable String department) {
        return ResponseEntity.ok(employeeService.getDepartmentTicketSummary(department));
    }

    // ✅ Global + All Departments Summary
    @GetMapping("/tickets-summary/all")
    public ResponseEntity<?> getGlobalAndDepartmentSummaries() {
        return ResponseEntity.ok(employeeService.getGlobalAndDepartmentSummaries());
    }


}
