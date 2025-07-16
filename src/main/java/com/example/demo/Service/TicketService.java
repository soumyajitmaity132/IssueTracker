package com.example.demo.Service;

import com.example.demo.Entity.Ticket;
import java.util.List;

public interface TicketService {
    List<Ticket> findAll();
    Ticket findById(Long id);
    void save(Ticket ticket);
    void deleteById(Long id);
}
