package com.example.demo.Repository;

import com.example.demo.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    

    List<Ticket> findByDepartment(String department);
    @SuppressWarnings("null")
    List<Ticket> findAll();
    List<Ticket> findByUser_Email(String email); 
    List<Ticket> findByEmployeeEmail(String email);
    List<Ticket> findByAssignee(String assignee); 
    List<Ticket> findByStatusNot(String status);
    List<Ticket> findByEmployeeEmailAndStatusNot(String email, String status);
    List<Ticket> findByEmployeeEmailAndStatus(String employeeEmail, String status);
     List<Ticket> findByIsDeletedFalse(); // for listing active tickets

List<Ticket> findByEmployeeEmailAndIsDeletedTrue(String email); // for deleted tickets


}
