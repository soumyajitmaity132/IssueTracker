package com.example.demo.Controllers;

import com.example.demo.Entity.Message;
import com.example.demo.Entity.Ticket;
import com.example.demo.Entity.User;
import com.example.demo.Repository.TicketRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.MessageService;
import com.example.demo.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class TicketControllers {

    private final TicketRepository ticketRepository;
    private final EmailService mailService;
    private final UserService userService;

    @Autowired
    public TicketControllers(TicketRepository ticketRepository, EmailService mailService, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.mailService = mailService;
        this.userService = userService;
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String email = principal.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("username", user.getName());
        return "employee_dashboard"; // just main menu
    }

    @GetMapping("/employee/raised")
public String ticketsRaisedByEmployee(Model model, Principal principal) {
    if (principal == null) return "redirect:/login";

    String email = principal.getName();
    List<Ticket> raisedTickets = ticketRepository.findByEmployeeEmailAndStatusNot(email, "CLOSED");
    model.addAttribute("tickets", raisedTickets);
    return "employee_raised_tickets";
}

    @GetMapping("/employee/assigned")
public String ticketsAssignedToEmployee(Model model, Principal principal) {
    if (principal == null) return "redirect:/login";

    String email = principal.getName();
    User user = userService.findByEmail(email);
    List<Ticket> assignedTickets = ticketRepository.findByAssignee(user.getName());
    model.addAttribute("tickets", assignedTickets);
    return "employee_assigned_tickets";
    }

    @PostMapping("/employee/fix/{id}")
public String fixTicket(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
    Ticket ticket = ticketRepository.findById(id).orElse(null);
    User user = userService.findByEmail(principal.getName());

    if (ticket != null && user != null && user.getName().equals(ticket.getAssignee())) {
        ticket.setStatus("FIXED");
        ticketRepository.save(ticket);
        redirectAttributes.addFlashAttribute("successId", ticket.getId());
    }
    return "redirect:/employee/assigned";
}



// Admin closes the ticket only if it is already marked as FIXED
@PostMapping("/admin/mark-fixed/{id}")
public String closeTicketIfFixed(@PathVariable Long id) {
    Optional<Ticket> optionalTicket = ticketRepository.findById(id);
    if (optionalTicket.isPresent()) {
        Ticket ticket = optionalTicket.get();

        if ("FIXED".equalsIgnoreCase(ticket.getStatus())) {
            ticket.setStatus("CLOSED");
            ticket.setIsDeleted(true);
            ticketRepository.save(ticket);
            mailService.sendTicketClosedMail(ticket); // notify user
            return "redirect:/admin/tickets?success";
        } else {
            return "redirect:/admin/tickets?error=notFixed";
        }
    }

    return "redirect:/admin/tickets?error=notFound";
}

    @GetMapping("/submit")
    public String showSubmitPage(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "submit_ticket"; // Make sure submit_ticket.html exists in templates/
    }

    // Handle form submission (POST /submit)
    @PostMapping("/submit")
public String submitTicket(@ModelAttribute Ticket ticket, Model model, Principal principal) {
    try{
    if (principal == null) return "redirect:/login";

    String loggedInEmail = principal.getName();

    // Get the User entity
    User employee = userService.findByEmail(loggedInEmail);
    if (employee == null) {
        // Avoid null pointer issues
        return "redirect:/submit?error=userNotFound";
    }

    ticket.setEmployeeEmail(loggedInEmail);
    ticket.setUser(employee); // ✅ Set the User entity here
    ticket.setStatus("OPEN");
    ticket.setTicketNumber("TCKT-" + System.currentTimeMillis());
    ticket.setAssignee("Admin of " + ticket.getDepartment());

        ticket.setCreatedAt(LocalDateTime.now());


    ticketRepository.save(ticket);
    mailService.sendTicketCreationMail(ticket);

    return "redirect:/submit?success";
   }
   catch (Exception e) {
       e.printStackTrace();  // for debugging
        return "redirect:/submit?error=unexpected";
    }
}
@Autowired
private MessageService messageService;
    




    // Optional: Show admin panel (GET /admin)
    @GetMapping("/admin")
public String adminDashboard(Model model, Principal principal) {
    if (principal == null) {
        // User not logged in
        model.addAttribute("loginError", true);
        return "login";
    }

    String email = principal.getName();
    User user = userService.findByEmail(email);
    model.addAttribute("user", user);

    return "admin"; // or whatever your dashboard view is
}


    // Optional: Handle admin form submission (POST /admin)
    @PostMapping("/admin")
    public String handleAdminPost(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        String username = principal.getName();
        model.addAttribute("username", username);
        return "admin";
    }

    @GetMapping("/admin/reports")
public String adminReports(Model model) {
    return "admin_reports";
}

    @GetMapping("/admin/api/status-summary")
@ResponseBody
public Map<String, Long> getStatusSummary() {
    List<Ticket> tickets = ticketRepository.findAll();
    return tickets.stream().collect(Collectors.groupingBy(
            Ticket::getStatus,
            Collectors.counting()
    ));
}

@GetMapping("/admin/api/daily-ticket-count")
@ResponseBody
public Map<String, Long> getDailyTicketCounts() {
    List<Ticket> tickets = ticketRepository.findAll();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    return tickets.stream().collect(Collectors.groupingBy(
            t -> t.getCreatedAt().toLocalDate().format(formatter),
            TreeMap::new, // to keep sorted
            Collectors.counting()
    ));
}

@GetMapping("/admin/api/department-summary")
@ResponseBody
public Map<String, Long> getDepartmentSummary() {
    List<Ticket> tickets = ticketRepository.findAll();
    return tickets.stream().collect(Collectors.groupingBy(
            Ticket::getDepartment,
            Collectors.counting()
    ));
}

@PostMapping("/employee/restore/{id}")
public String restoreTicket(@PathVariable Long id, Principal principal) {
    Ticket ticket = ticketRepository.findById(id).orElse(null);
    if (ticket != null && principal.getName().equals(ticket.getEmployeeEmail())) {
        ticket.setIsDeleted(false);
        ticket.setStatus("OPEN"); // Or "CLOSED" or original value
        ticketRepository.save(ticket);
    }
    return "redirect:/employee/deleted-tickets?restored=true";
}


@GetMapping("/employee/deleted-tickets")
public String viewDeletedTickets(Model model, Principal principal) {
    if (principal == null) return "redirect:/login";

    String email = principal.getName();
List<Ticket> deletedTickets = ticketRepository.findByEmployeeEmailAndIsDeletedTrue(email);
    User user = userService.findByEmail(email);
    model.addAttribute("username", user != null ? user.getName() : email);
    model.addAttribute("deletedTickets", deletedTickets);

    return "employee_deleted_tickets";  // new HTML page
}




}

