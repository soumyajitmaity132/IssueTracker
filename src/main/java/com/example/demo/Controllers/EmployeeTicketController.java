package com.example.demo.Controllers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.example.demo.Service.EmailService;

@RestController
@RequestMapping("/api/employee/tickets")
@CrossOrigin
public class EmployeeTicketController {

    @Autowired private TicketRepository ticketRepo;
    @Autowired private EmployeeRepository employeeRepo;
    @Autowired private DepartmentSubjectRepository departmentSubjectRepo;
    @Autowired private EmailService emailService;


    // 1. Tickets You Raised
    @GetMapping("/raised")
    //@PreAuthorize("hasAuthority('EMPLOYEE')")
    public List<Ticket> ticketsRaised() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    Employee emp = employeeRepo.findByEmail(email).orElse(null);
    if (emp == null) {
        return Collections.emptyList(); // No employee found for logged-in user
    }
    return ticketRepo.findByEmpId(emp.getEmpId());
    }

    // 2. Tickets Assigned to You
    @GetMapping("/assigned")
    //@PreAuthorize("hasAuthority('EMPLOYEE')")
    public List<Ticket> ticketsAssigned() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    Employee emp = employeeRepo.findByEmail(email).orElse(null);
    if (emp == null) {
        return Collections.emptyList(); // No employee found for logged-in user
    }
        return ticketRepo.findByAssignee(emp.getEmpId());
    }

    // 3. View Closed Tickets
    @GetMapping("/closed")
public List<Ticket> closedTickets() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    Employee emp = employeeRepo.findByEmail(email).orElse(null);
    if (emp == null) {
        return Collections.emptyList();
    }

    boolean isAdmin = emp.getRole() != null && emp.getRole().equalsIgnoreCase("ADMIN");

    if (isAdmin) {
        // Admin: return all closed tickets from their department
        return ticketRepo.findByDepartmentAndStatus(emp.getDepartment(), "CLOSED");
    } else {
        // Employee: return only their closed tickets
        return ticketRepo.findByEmpIdAndStatus(emp.getEmpId(), "CLOSED");
    }
}


    // 4. Submit New Ticket
    @PostMapping("/submit")
public ResponseEntity<?> submitTicket(@RequestBody Ticket ticket) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    Employee emp = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    String departmentFromForm = ticket.getDepartment();
    String subjectFromForm = ticket.getSubject();

    if (departmentFromForm == null || departmentFromForm.isBlank()) {
        return ResponseEntity.badRequest().body("Department is required");
    }
    if (subjectFromForm == null || subjectFromForm.isBlank()) {
        return ResponseEntity.badRequest().body("Subject is required");
    }

    boolean subjectValid = departmentSubjectRepo.findByDepartmentIgnoreCase(departmentFromForm)
            .stream()
            .anyMatch(ds -> ds.getSubject().equalsIgnoreCase(subjectFromForm));
    if (!subjectValid) {
        return ResponseEntity.badRequest().body("Invalid subject for the selected department");
    }

    // Prevent duplicate tickets in 5 seconds
    LocalDateTime fiveSecondsAgo = LocalDateTime.now().minusSeconds(5);
    boolean duplicateExists = ticketRepo.existsByEmpIdAndSubjectIgnoreCaseAndCreatedAtAfter(
            emp.getEmpId(), subjectFromForm, fiveSecondsAgo
    );
    if (duplicateExists) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Please wait before submitting the same ticket again.");
    }

    // Validate CC employees
    String ccIds = ticket.getCcEmployeeIds();
    if (ccIds != null && !ccIds.isBlank()) {
        List<String> validIds = Arrays.stream(ccIds.split(","))
                .map(String::trim)
                .filter(id -> employeeRepo.findByEmpId(id).isPresent())
                .toList();
        ccIds = String.join(",", validIds);
    } else {
        ccIds = null;
    }

    // Validate Google Drive link
    String attachmentLink = ticket.getAttachmentLink();
    if (attachmentLink != null && !attachmentLink.isBlank()) {
        if (!isValidGoogleDriveLink(attachmentLink)) {
            return ResponseEntity.badRequest().body("Invalid Google Drive link format.");
        }
        if (!attachmentLink.contains("sharing")) {
            return ResponseEntity.badRequest().body("Google Drive link must be in sharing mode.");
        }
    } else {
        attachmentLink = null;
    }

    // Set ticket details
    ticket.setEmpId(emp.getEmpId());
    ticket.setEmployeeName(emp.getName());
    ticket.setStatus("OPEN");
    ticket.setCreatedAt(LocalDateTime.now());
    ticket.setDepartment(departmentFromForm);
    ticket.setCcEmployeeIds(ccIds);
    ticket.setAttachmentLink(attachmentLink);

    ticketRepo.save(ticket);

    // Send email to Department Admin
    Employee deptAdmin = employeeRepo.findByDepartmentAndRole(departmentFromForm, "ADMIN")
            .orElseThrow(() -> new RuntimeException("Admin not found for department: " + departmentFromForm));

    String subject = "New Ticket Raised: " + ticket.getSubject();
    String body = "Hello,\n\nA new ticket has been raised.\n\n" +
            "Ticket No: " + ticket.getTicketNo() + "\n" +
            "Raised By: " + emp.getName() + "\n" +
            "Subject: " + ticket.getSubject() + "\n" +
            "Description: " + ticket.getDetailedMessage() + "\n" +
            "Priority: " + ticket.getPriority() + "\n" +
            "Created At: " + ticket.getCreatedAt() + "\n" +
            (attachmentLink != null ? "Attachment: " + attachmentLink + "\n" : "") +
            "\nRegards,\nCompany Tracker System";

    emailService.sendEmail(deptAdmin.getEmail(), subject, body);

    // Send email to CC’d employees
    if (ccIds != null) {
        for (String id : ccIds.split(",")) {
            employeeRepo.findByEmpId(id).ifPresent(ccEmp ->
                    emailService.sendEmail(ccEmp.getEmail(), subject, body + "\n\nYou have been CC'd on this ticket.")
            );
        }
    }

    return ResponseEntity.ok("Ticket submitted successfully.");
}

// Helper method to validate Google Drive link
private boolean isValidGoogleDriveLink(String link) {
    return link.matches("https:\\/\\/(drive|docs)\\.google\\.com\\/.*");
}




     //6 EMPLOYEE REOPEN CLOSED or DELETED TICKET
     @PutMapping("/reopen/{ticketNo}")
@PreAuthorize("hasAuthority('EMPLOYEE')")
public ResponseEntity<?> reopenTicket(@PathVariable Long ticketNo) {
    // Get logged-in employee
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    Employee emp = employeeRepo.findByEmail(email).orElse(null);
    if (emp == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Employee not found");
    }

    // Find ticket
    Ticket ticket = ticketRepo.findById(ticketNo).orElse(null);
    if (ticket == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
    }

    // Ensure the employee is the one who raised the ticket
    if (!ticket.getEmpId().equals(emp.getEmpId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to reopen this ticket");
    }

    // Allow only CLOSED tickets to be reopened
    if (!"CLOSED".equalsIgnoreCase(ticket.getStatus())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Only CLOSED tickets can be reopened");
    }

    // Reopen ticket
    ticket.setStatus("OPEN");
    ticketRepo.save(ticket);

    return ResponseEntity.ok("Ticket reopened successfully");
}

     //Employee Gets all the subject from his department
     @GetMapping("/get_subjects")
@PreAuthorize("hasAuthority('EMPLOYEE')")
public List<String> getSubjectsForDepartment(@RequestParam("department") String department) {
    // Find subjects by the department specified in the request
    return departmentSubjectRepo.findByDepartmentIgnoreCase(department)
            .stream()
            .map(DepartmentSubject::getSubject)
            .collect(Collectors.toList());
}


     // EMPLOYEE can see all tickets that are raised in his/her Department
    @GetMapping("/department-tickets")
@PreAuthorize("hasAuthority('EMPLOYEE')")
public ResponseEntity<?> getDepartmentTickets() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    // Find logged-in employee
    Employee emp = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    // Get all tickets in employee's department
    List<Ticket> tickets = ticketRepo.findByDepartmentIgnoreCase(emp.getDepartment());

    return ResponseEntity.ok(tickets);
}


// The EMployee can assign Tickets raised in his department to his own name
   @PutMapping("/assign-to-me/{ticketNo}")
@PreAuthorize("hasAuthority('EMPLOYEE')")
public ResponseEntity<?> assignTicketToSelf(@PathVariable Long ticketNo) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    // Find logged-in employee
    Employee emp = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    // Find ticket
    Ticket ticket = ticketRepo.findById(ticketNo)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

    // Ensure ticket belongs to the employee's department
    if (!ticket.getDepartment().equalsIgnoreCase(emp.getDepartment())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You can only assign tickets from your own department");
    }

    // Assign ticket to this employee
    ticket.setAssignee(emp.getEmpId()); // store employee ID
    ticket.setStatus("ASSIGNED");
    ticketRepo.save(ticket);

    return ResponseEntity.ok("Ticket assigned to you successfully");
}

      //EMPLOYEE can update Ticket Details raised by him
      @PutMapping("/update_ticket/{ticketId}")
@PreAuthorize("hasAuthority('EMPLOYEE')")
public ResponseEntity<String> updateTicketByEmployee(
        @PathVariable Long ticketId,
        @RequestBody Ticket updatedTicketDetails) {

    // Get logged-in employee's email
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    // Fetch the logged-in employee
    Employee employee = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    // Fetch the ticket by ID
    Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

    // Check if the logged-in employee is the creator of the ticket
    if (!ticket.getEmpId().equals(employee.getEmpId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You are not authorized to update this ticket.");
    }

    // Update allowed fields
    ticket.setSubject(updatedTicketDetails.getSubject());
    ticket.setDetailedMessage(updatedTicketDetails.getDetailedMessage());
    ticket.setPriority(updatedTicketDetails.getPriority());
    ticket.setStatus(updatedTicketDetails.getStatus());

    // Save updated ticket
    ticketRepo.save(ticket);

    return ResponseEntity.ok("Ticket updated successfully.");
}

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Ticket> detail(@PathVariable Long id) {
        return ticketRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //EMPLOYEES will get all Tickets where they have been CC'd to
     @GetMapping("/cc-tickets")
public List<Ticket> getCcTickets() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    Employee emp = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    return ticketRepo.findTicketsByCcEmployeeId(emp.getEmpId());
}

   //GET the Attachment LInk for each Ticket
   @GetMapping("/{ticketNo}/attachment")
public ResponseEntity<?> getAttachmentLink(@PathVariable Long ticketNo) {
    // Find the ticket
    Ticket ticket = ticketRepo.findByTicketNo(ticketNo)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

    // Assuming your Ticket entity has a field for attachment link
    String attachmentLink = ticket.getAttachmentLink();

    // If no attachment link
    if (attachmentLink == null || attachmentLink.trim().isEmpty()) {
        return ResponseEntity.ok("No attachment available");
    }

    return ResponseEntity.ok(attachmentLink);
}


   // API to get all employees (for dropdown)
    @GetMapping("/dropdown")
    public ResponseEntity<List<Map<String, String>>> getEmployeesForDropdown() {
        List<Employee> employees = employeeRepo.findAll();

        // Create a simple list with only empId and name
        List<Map<String, String>> dropdownList = employees.stream()
                .map(emp -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("empId", emp.getEmpId());
                    map.put("name", emp.getName());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(dropdownList);
    }


    
}

