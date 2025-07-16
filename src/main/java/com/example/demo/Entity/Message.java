package com.example.demo.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to Ticket entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    @JsonIgnore
    private Ticket ticket;

    private String senderEmail;

    private String content;

    private LocalDateTime timestamp;

        private String name;

    

    public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    // Constructors
    public Message() {}

    public Message(Ticket ticket, String senderEmail, String content, LocalDateTime timestamp,String name) {
        this.ticket = ticket;
        this.senderEmail = senderEmail;
        this.content = content;
        this.timestamp = timestamp;
        this.name=name;
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
