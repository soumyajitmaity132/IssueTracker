package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.Message;

public interface ChatMessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTicketNoOrderBySentAtAsc(Long ticketNo);

}


