package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.DepartmentSubject;
import com.example.demo.Repository.DepartmentSubjectRepository;

@Service
public class DepartmentSubjectService {

    @Autowired
    private DepartmentSubjectRepository repo;

    @Transactional
    public void deleteDepartment(String department) {
        repo.deleteByDepartment(department);
    }

    @Transactional
    public void deleteSubjectFromDepartment(String department, String subject) {
        if (!repo.existsByDepartmentAndSubject(department, subject)) {
            throw new RuntimeException("Subject '" + subject + "' not found in department: " + department);
        }
        repo.deleteByDepartmentAndSubject(department, subject);
    }

    @Transactional
    public void editSubjectInDepartment(String department, String oldSubject, String newSubject) {
        DepartmentSubject ds = repo.findByDepartmentAndSubject(department, oldSubject)
                .orElseThrow(() -> new RuntimeException(
                        "Subject '" + oldSubject + "' not found in department: " + department));

        // check if newSubject already exists in same department
        if (repo.existsByDepartmentAndSubject(department, newSubject)) {
            throw new RuntimeException("Subject '" + newSubject + "' already exists in department: " + department);
        }

        ds.setSubject(newSubject);
        repo.save(ds);
    }
}


