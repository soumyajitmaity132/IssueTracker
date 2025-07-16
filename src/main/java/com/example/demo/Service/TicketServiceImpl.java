package com.example.demo.Service;

import com.example.demo.Entity.Ticket;
import com.example.demo.Repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));
    }

    @Override
    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    @Override
public void deleteById(Long id) {
    Ticket ticket = ticketRepository.findById(id).orElse(null);
    if (ticket != null) {
        ticket.setStatus("DELETED");   // Optional: mark status
        ticket.setIsDeleted(true);     // Mark as soft deleted
        ticketRepository.save(ticket);
    }
}

}
