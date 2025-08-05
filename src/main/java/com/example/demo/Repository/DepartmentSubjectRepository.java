package com.example.demo.Repository;

import com.example.demo.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartmentSubjectRepository extends JpaRepository<DepartmentSubject, Long> {
    List<DepartmentSubject> findByDepartmentIgnoreCase(String department);
}
