package com.example.demo.Service;

import com.example.demo.DTO.TicketRequestDto;
import com.example.demo.Entity.Ticket;
import com.example.demo.Entity.User;
import com.example.demo.Repository.TicketRepository;
import com.example.demo.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketBusinessService {

    @Autowired private TicketRepository ticketRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private EmailService emailService;

    // ✅ Create ticket and send mail to department + employee
    public Ticket createTicket(TicketRequestDto dto, String employeeEmail) {
        User employee = userRepo.findByEmail(employeeEmail).orElseThrow();
        User admin = userRepo.findAll().stream()
                .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()) &&
                             dto.getDepartment().equalsIgnoreCase(u.getDepartment()))
                .findFirst()
                .orElseThrow();

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(UUID.randomUUID().toString().substring(0, 8));
        ticket.setDepartment(dto.getDepartment());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());
        ticket.setSeverity(dto.getSeverity());
        ticket.setStatus("OPEN");
        ticket.setAssignee(admin.getName());
        ticket.setRaisedBy(employee);

        ticketRepo.save(ticket);
        emailService.sendTicketCreationMail(ticket);

        return ticket;
    }

    // ✅ Update ticket status and notify employee if resolved
    public void updateStatus(Long ticketId, String status) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        ticket.setStatus(status);
        ticketRepo.save(ticket);

        if ("FIXED".equalsIgnoreCase(status)) {
            emailService.sendTicketResolvedMail(ticket);
        }
    }
}
