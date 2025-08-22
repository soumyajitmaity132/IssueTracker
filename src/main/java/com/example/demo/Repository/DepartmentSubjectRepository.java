package com.example.demo.Repository;

import com.example.demo.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DepartmentSubjectRepository extends JpaRepository<DepartmentSubject, Long> {
    List<DepartmentSubject> findByDepartmentIgnoreCase(String department);
        boolean existsByDepartment(String department);
 @Transactional
    void deleteByDepartment(String department);
    void deleteByDepartmentAndSubject(String department, String subject);
    boolean existsByDepartmentAndSubject(String department, String subject);
        Optional<DepartmentSubject> findByDepartmentAndSubject(String department, String subject);

}
