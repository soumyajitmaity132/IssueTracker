package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Entity.Employee;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Util.ExcelHelper;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // File upload method
    public int saveEmployeesFromFile(MultipartFile file) throws Exception {
        List<Employee> employees = ExcelHelper.excelToEmployees(file.getInputStream());
        return saveAllEmployees(employees);
    }

    // Google Sheet method
    public int saveAllEmployees(List<Employee> employees) {
        int count = 0;

        for (Employee emp : employees) {
            Optional<Employee> existing = employeeRepository.findByEmpId(emp.getEmpId());

            if (existing.isPresent()) {
                // 🔹 Update existing employee details
                Employee existingEmp = existing.get();
                existingEmp.setName(emp.getName());
                existingEmp.setEmail(emp.getEmail());
                existingEmp.setDepartment(emp.getDepartment());
                existingEmp.setReportingManager(emp.getReportingManager());
                existingEmp.setRole(emp.getRole());
                existingEmp.setPassword(emp.getPassword());
                employeeRepository.save(existingEmp);
            } else {
                // 🔹 Insert new employee
                employeeRepository.save(emp);
                count++;
            }
        }

        return count; // count only new inserts
    }
}
