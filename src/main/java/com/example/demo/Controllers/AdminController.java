package com.example.demo.Controllers;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService mailService;
        private final UserService userService;

       @Autowired 
       private TicketService ticketService;


        @Autowired
private PasswordEncoder passwordEncoder;



    @GetMapping("/employees")
public String viewEmployees(Model model) {
    
    List<User> users = userService.findAll();
    model.addAttribute("employees", users);
    return "employee_list"; // or "admin_employees" if you renamed it
}



     public AdminController(UserService userService) {
          this.userService = userService;
     }
 


    // 1. Show all tickets for admin's department
    @GetMapping("/tickets")
public String viewDepartmentTickets(
        @RequestParam(name = "success", required = false) String success,
        @RequestParam(name = "error", required = false) String error,
        Model model,
        Principal principal) {

    String adminEmail = principal.getName();  // e.g., hr.admin@example.com
    String department = getAdminDepartment(adminEmail); // Helper function

    List<Ticket> tickets = ticketRepository.findAll();

    model.addAttribute("tickets", tickets);
    model.addAttribute("adminDepartment", department);

    // Pass the request parameters explicitly to the model for Thymeleaf
    model.addAttribute("success", success);
    model.addAttribute("error", error);

    return "admin_tickets";
}

    // 2. Update status (open, assigned, fixed)
    @PostMapping("/ticket/update")
    public String updateTicketStatus(@RequestParam Long ticketId,
                                     @RequestParam String status) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);

        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setStatus(status);
            ticketRepository.save(ticket);

            if (status.equalsIgnoreCase("fixed")) {
                mailService.sendTicketResolvedMail(ticket);
            }
        }

        return "redirect:/admin/tickets";
    }

   /*  @GetMapping("/ticket/edit/{id}")
public String showEditForm(@PathVariable Long id, Model model) {
    Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
    model.addAttribute("ticket", ticket);
    return "edit_ticket";
}*/

@GetMapping("/ticket/edit/{id}")
public String editTicketForm(@PathVariable Long id, Model model) {
    Ticket ticket = ticketService.findById(id);
    List<User> employees = userService.findAllEmployees();

    model.addAttribute("ticket", ticket);
    model.addAttribute("employees", employees);
    return "edit_ticket";
}




@PostMapping("/ticket/edit/{id}")
public String updateTicket(@PathVariable Long id,
                           @ModelAttribute("ticket") Ticket updatedTicket,
                           @RequestParam("assigneeId") Long assigneeId,
                           RedirectAttributes redirectAttributes) {
    
    Optional<Ticket> ticketOpt = ticketRepository.findById(id);
    Optional<User> assigneeOpt = userRepository.findById(assigneeId);    

    if (ticketOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Ticket not found.");
        return "redirect:/admin/tickets?error=ticketNotFound";
    }

    if (assigneeOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Assignee not found.");
        return "redirect:/admin/tickets?error=assigneeNotFound";
    }

    Ticket ticket = ticketOpt.get();
    User assignee = assigneeOpt.get();

    // Update ticket fields
    ticket.setDescription(updatedTicket.getDescription());
    ticket.setStatus(updatedTicket.getStatus());
    ticket.setDepartment(updatedTicket.getDepartment());
    ticket.setPriority(updatedTicket.getPriority());
    ticket.setAssignee(assignee.getName());

    ticketRepository.save(ticket);

    // Notify new assignee
    mailService.sendAssigneeUpdateMail(assignee, ticket);

    redirectAttributes.addFlashAttribute("success", "Ticket updated and assigned to " + assignee.getName());
    return "redirect:/admin/tickets?assigned=success";
}


@GetMapping("/ticket/view/{id}")
public String viewTicketDetails(@PathVariable Long id, Model model) {
    Ticket ticket = ticketRepository.findById(id).orElse(null);
    if (ticket == null) return "redirect:/admin/tickets?error=notFound";

    model.addAttribute("ticket", ticket);
    return "ticket_details"; // Create this new page
}

@GetMapping("/employees/add")
public String showAddEmployeeForm() {
    return "add_employee";
}

@PostMapping("/employees/add")
public String addEmployee(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String department,
                          @RequestParam String role) {
    if (!role.equalsIgnoreCase("EMPLOYEE")) {
        return "redirect:/admin/employees?error=InvalidRole";
    }

    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(password); // ❗Don't encode here
    user.setOriginalPassword(password); 
    user.setDepartment(department);
    user.setRole(role.toUpperCase());

    userService.save(user); // make sure UserService and UserRepository support save()
    return "redirect:/admin/employees?success";
}


    // Helper: Map admin email to department (or use a DB table later)
    private String getAdminDepartment(String email) {
        email = email.toLowerCase();
        if (email.contains("hr")|| email.equals("admin")) return "HR";
        if (email.contains("hardware")) return "Hardware";
        if (email.contains("transport")) return "Transport";
        return "Unknown";
    }
}