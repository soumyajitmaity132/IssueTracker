package com.example.demo.Controllers.impl;

import com.example.demo.Entity.Employee;
import com.example.demo.Repository.DepartmentSubjectRepository;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeTicketDetailsImpl {

    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private TicketRepository ticketRepo;
    @Autowired
    DepartmentSubjectRepository departmentRepo;

    // ✅ Get Employee by EmpId
    public Optional<Employee> getEmployeeById(String empId) {
        return employeeRepo.findByEmpId(empId);
    }

    // ✅ Get Employee Names by List of EmpIds
    public List<String> getEmployeeNamesByIds(List<String> empIds) {
        List<String> employeeNames = new ArrayList<>();

        for (String empId : empIds) {
            employeeRepo.findByEmpId(empId).ifPresent(employee ->
                    employeeNames.add(employee.getName())
            );
        }

        return employeeNames;
    }

    // ✅ Department-wise Summary
    public Map<String, Object> getDepartmentTicketSummary(String department) {
        long open = ticketRepo.countByDepartmentAndStatus(department, "OPEN");
        long closed = ticketRepo.countByDepartmentAndStatus(department, "CLOSED");
        long assigned = ticketRepo.countByDepartmentAndStatus(department, "ASSIGNED");
        long all = ticketRepo.countByDepartment(department);

        return Map.of(
                "department", department,
                "openTickets", open,
                "closedTickets", closed,
                "assignedTickets", assigned,
                "allTickets", all
        );
    }

    // ✅ Global Summary
    public Map<String, Object> getGlobalTicketSummary() {
        long open = ticketRepo.countByStatus("OPEN");
        long closed = ticketRepo.countByStatus("CLOSED");
        long assigned = ticketRepo.countByStatus("ASSIGNED");
        long all = ticketRepo.count();

        return Map.of(
                "department", "global",
                "openTickets", open,
                "closedTickets", closed,
                "assignedTickets", assigned,
                "allTickets", all
        );
    }

    // ✅ Global + All Departments Summary
    public List<Map<String, Object>> getGlobalAndDepartmentSummaries() {
        List<Map<String, Object>> list = new ArrayList<>();

        // Add global summary first
        list.add(getGlobalTicketSummary());

        // Add each department summary
        List<String> departments = departmentRepo.findAllDepartments();
        for (String dept : departments) {
            list.add(getDepartmentTicketSummary(dept));
        }

        return list;
    }
}
