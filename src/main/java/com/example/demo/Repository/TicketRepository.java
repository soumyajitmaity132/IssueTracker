package com.example.demo.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEmpId(String empId);

    List<Ticket> findByAssignee(String empId);

    List<Ticket> findByEmpIdAndStatus(String empId, String status);
    List<Ticket> findByDepartmentIgnoreCase(String department);
    List<Ticket> findByDepartmentAndStatus(String department, String status);
    boolean existsByEmpIdAndSubjectIgnoreCaseAndCreatedAtAfter(String empId, String subject, LocalDateTime afterTime);
    @Query("SELECT t FROM Ticket t WHERE t.ccEmployeeIds LIKE %:empId%")
    List<Ticket> findTicketsByCcEmployeeId(@Param("empId") String empId);
    Optional<Ticket> findByTicketNo(Long ticketNo);

    long countByStatus(String status);

    // Or return grouped result
    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> getTicketCountByStatus();

    int countByDepartmentAndStatus(String department, String status);

    // ✅ Count all tickets of a department
    int countByDepartment(String department);

}
