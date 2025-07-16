package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.Message;
import com.example.demo.Entity.Ticket;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTicketOrderByTimestampAsc(Ticket ticket);
}
