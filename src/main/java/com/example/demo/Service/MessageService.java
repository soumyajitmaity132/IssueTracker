package com.example.demo.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Message;
import com.example.demo.Entity.Ticket;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Repository.TicketRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public List<Message> getMessagesByTicketId(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            return List.of(); // or throw custom exception
        }
        return messageRepository.findByTicketOrderByTimestampAsc(ticket);
    }

    public Message saveMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }
}

