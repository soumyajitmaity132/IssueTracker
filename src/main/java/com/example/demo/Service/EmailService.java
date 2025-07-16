package com.example.demo.Service;

import com.example.demo.Entity.Ticket;
import com.example.demo.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 1. Ticket Created: send to department + cc employee
    public void sendTicketCreationMail(Ticket ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(getDepartmentEmail(ticket.getDepartment()));
        message.setCc(ticket.getEmployeeEmail());
        message.setSubject("New Ticket Submitted: " + ticket.getTicketNumber());
        message.setText("A new ticket has been submitted.\n\n"
                + "Department: " + ticket.getDepartment() + "\n"
                + "Priority: " + ticket.getPriority() + "\n"
                + "Severity: " + ticket.getSeverity() + "\n"
                + "Description: " + ticket.getDescription() + "\n"
                + "Submitted by: " + ticket.getEmployeeEmail());

        mailSender.send(message);
    }

    // 2. Ticket Resolved: notify employee
    public void sendTicketResolvedMail(Ticket ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ticket.getEmployeeEmail());
        message.setSubject("Ticket Resolved: " + ticket.getTicketNumber());
        message.setText("Your ticket has been resolved by the " + ticket.getDepartment() + " department.\n\n"
                + "Description: " + ticket.getDescription() + "\n"
                + "Thank you.");
        mailSender.send(message);
    }

    // 3. New Assignee Notification
    public void sendAssigneeUpdateMail(User assignee, Ticket ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(assignee.getEmail());
        message.setSubject("Ticket Assigned: Ticket #" + ticket.getId());
        message.setText("Hello " + assignee.getName() + ",\n\n"
                + "You have been assigned to the following ticket:\n"
                + "Department: " + ticket.getDepartment() + "\n"
                + "Priority: " + ticket.getPriority() + "\n"
                + "Description: " + ticket.getDescription() + "\n\n"
                + "Please check the system for more details.\n\nThanks.");
        mailSender.send(message);
    }

    public void sendTicketClosedMail(Ticket ticket) {
    if (ticket == null || ticket.getUser() == null) return;

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(ticket.getEmployeeEmail());
    message.setSubject("Ticket Closed: Ticket #" + ticket.getId());

    message.setText("Hello " + ticket.getUser().getName() + ",\n\n"
            + "Your ticket has been marked as CLOSED by the admin.\n\n"
            + "Ticket Details:\n"
            + "Department: " + ticket.getDepartment() + "\n"
            + "Priority: " + ticket.getPriority() + "\n"
            + "Description: " + ticket.getDescription() + "\n"
            + "Final Status: CLOSED\n\n"
            + "Thank you for your patience.\n\n"
            + "Regards,\nSupport Team");

    mailSender.send(message);
}



    // Helper
    private String getDepartmentEmail(String department) {
        switch (department.toLowerCase()) {
            case "hardware":
            case "hr":
            case "transport":
                return "soumyajit56340@gmail.com";
            default:
                return "default.admin@example.com";
        }
    }
}
