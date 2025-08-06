package com.example.demo.Controllers;

import java.util.List;
import java.util.Optional;

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

@RestController
@RequestMapping("/api/admin/tickets")
@CrossOrigin
public class TicketController {

    @Autowired private TicketRepository ticketRepo;
        @Autowired private DepartmentSubjectRepository repo;
        @Autowired private EmployeeRepository employeeRepo;


    @GetMapping
@PreAuthorize("hasAuthority('ADMIN')")
public List<Ticket> allForAdminDepartment() {
    // Get logged-in admin's email from JWT
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    // Find admin in DB
    Employee admin = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

    // Get department name
    String department = admin.getDepartment();

    // Return only tickets for that department
    return ticketRepo.findByDepartmentIgnoreCase(department);
}


    /// Update Assignee
    @PutMapping("/{id}/assignee")
    public ResponseEntity<?> updateAssignee(@PathVariable Long id, @RequestParam String assignee) {
        Optional<Ticket> ticketOpt = ticketRepo.findById(id);
        if (ticketOpt.isEmpty()) return ResponseEntity.notFound().build();
        Ticket ticket = ticketOpt.get();
        ticket.setAssignee(assignee);
        ticketRepo.save(ticket);
        return ResponseEntity.ok("Updated");
    }
      // Get Ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> detail(@PathVariable Long id) {
        return ticketRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //ADMIN closes ticket whose status is FIXED
    @PutMapping("/close/{ticketNo}")
@PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<?> closeFixedTicket(@PathVariable Long ticketNo) {
    // Find ticket
    Ticket ticket = ticketRepo.findById(ticketNo).orElse(null);
    if (ticket == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");
    }

    // Check if ticket status is FIXED
    if (!"FIXED".equalsIgnoreCase(ticket.getStatus())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Only tickets with status 'FIXED' can be closed");
    }

    // Update status
    ticket.setStatus("CLOSED");
    ticketRepo.save(ticket);

    return ResponseEntity.ok("Ticket marked as CLOSED successfully");
}

     // Add new subject
    @PostMapping("/add_subjects")
@PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<?> addSubject(@RequestBody DepartmentSubject subject) {
    // Get logged-in admin's email from JWT
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    // Find admin details
    Employee admin = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

    // Check if entered department matches admin's department
    if (!subject.getDepartment().equalsIgnoreCase(admin.getDepartment())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You can only add subjects for your own department: " + admin.getDepartment());
    }

    // Save the subject
    DepartmentSubject savedSubject = repo.save(subject);

    return ResponseEntity.ok(savedSubject);
}



}
