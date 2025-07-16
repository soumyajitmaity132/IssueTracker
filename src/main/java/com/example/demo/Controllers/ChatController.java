package com.example.demo.Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.demo.DTO.MessageDTO;
import com.example.demo.Entity.Message;
import com.example.demo.Entity.Ticket;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Repository.TicketRepository;
import com.example.demo.Service.UserService;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // ✅ Return JSON messages for a given ticket
    @GetMapping("/{ticketId}")
public List<MessageDTO> getChatMessages(@PathVariable Long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
    if (ticket == null) {
        return List.of(); // empty
    }

    List<Message> messages = messageRepository.findByTicketOrderByTimestampAsc(ticket);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a");

    return messages.stream().map(msg -> new MessageDTO(
        msg.getName() != null ? msg.getName() : msg.getSenderEmail(),
        msg.getContent(),
        msg.getTimestamp().format(formatter)
    )).collect(Collectors.toList());
}


    // ✅ Save a new message from chat box via JS
    @PostMapping("/{ticketId}")
    public void sendMessage(
        @PathVariable Long ticketId,
        @RequestParam String content,
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User userPrincipal
    ) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) return;

        String senderEmail = userPrincipal.getUsername();
        

        Message message = new Message();
        message.setTicket(ticket);
        message.setSenderEmail(senderEmail);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
    }
}
