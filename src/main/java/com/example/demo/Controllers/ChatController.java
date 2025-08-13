package com.example.demo.Controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
         @RequestBody MessageRequest request,
        Principal principal) {

    // Fetch employee using email (principal.getName())
    Employee sender = employeeRepo.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Sender not found"));

    // Create the message
    Message message = new Message();
    message.setTicketNo(ticketNo);
    message.setSenderId(String.valueOf(sender.getEmpId())); // Employee ID as string
    message.setSenderName(sender.getName()); // Display name
    message.setMessage(request.getMessage());
    message.setSentAt(LocalDateTime.now());

    // Save the message
    Message savedMessage = messageRepo.save(message);

    return ResponseEntity.ok(savedMessage);
}


    @GetMapping("/chat/{ticketNo}")
    @ResponseBody
    public List<Message> getChatHistory(@PathVariable Long ticketNo) {
        return messageRepo.findByTicketNoOrderBySentAtAsc(ticketNo);
    }
}
