package com.example.demo.Controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.MessageRequest;
import com.example.demo.Entity.Employee;
import com.example.demo.Entity.Message;
import com.example.demo.Repository.ChatMessageRepository;
import com.example.demo.Repository.EmployeeRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatMessageRepository messageRepo;
    @Autowired private EmployeeRepository employeeRepo;

    @MessageMapping("/chat/{ticketNo}") // Client sends here
    @SendTo("/topic/chat/{ticketNo}")    // All clients subscribed here get update
    public Message sendMessage(@DestinationVariable Long ticketNo, Message message) {
        message.setSentAt(LocalDateTime.now());
        messageRepo.save(message);
        return message; // This will be broadcasted to all subscribers of this ticket
    }
    @PostMapping("/tickets/{ticketNo}/messages")
public ResponseEntity<Message> sendMessage(
        @PathVariable Long ticketNo,
        @RequestBody MessageRequest request) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // Fetch employee using email (from JWT subject)
    String email = auth.getName();

    Employee sender = employeeRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Sender not found"));

    Message message = new Message();
    message.setTicketNo(ticketNo);
    message.setSenderId(sender.getEmpId());
    message.setSenderName(sender.getName());
    message.setMessage(request.getMessage());
    message.setSentAt(LocalDateTime.now());

    return ResponseEntity.ok(messageRepo.save(message));
}


    @GetMapping("/chat/{ticketNo}")
    @ResponseBody
    public List<Message> getChatHistory(@PathVariable Long ticketNo) {
        return messageRepo.findByTicketNoOrderBySentAtAsc(ticketNo);
    }

    @DeleteMapping("/delete-message/{ticketNo}")
    public ResponseEntity<?> deleteMessage(@PathVariable("ticketNo") Long id) {
        // Find message
        Message message = messageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Get logged-in user email
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        String empId=message.getSenderId();
        Employee employee=employeeRepo.getById(empId);

        // Check if sender is the logged-in user
        if (!employee.getEmail().equals(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete this message");
        }

        // Delete message
        messageRepo.delete(message);

        return ResponseEntity.ok("Message deleted successfully");
    }
}
