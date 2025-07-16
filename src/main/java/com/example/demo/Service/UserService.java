package com.example.demo.Service;


import com.example.demo.Entity.*;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findByEmail(String email);
    void save(User user);
    User findById(Long id);

    List<User> findAllEmployees();

}
