package com.example.demo.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Ticket;
import com.example.demo.Repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Map<String, Long> getTicketStatusCountsDynamic(String department) {
    Map<String, Long> result = new HashMap<>();
    List<Object[]> list;

    if (department != null && !department.trim().isEmpty()) {
        list = ticketRepository.getTicketCountByStatusAndDepartment(department);
    } else {
        list = ticketRepository.getTicketCountByStatus();
    }

    for (Object[] obj : list) {
        String status = (String) obj[0];
        Long count = (Long) obj[1];
        result.put(status, count);
    }
    return result;
    }


    public boolean deleteTicket(Long ticketNo, String loggedInUser) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketNo);

        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();

            // Only creator can delete
            if (ticket.getEmpId().equalsIgnoreCase(loggedInUser)) {
                ticketRepository.delete(ticket);
                return true;
            }
        }
        return false; // not found or not authorized
    }
}
