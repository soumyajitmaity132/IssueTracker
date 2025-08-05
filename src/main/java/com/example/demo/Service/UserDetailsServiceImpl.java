package com.example.demo.Service;


import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee emp = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var authority = new SimpleGrantedAuthority("ROLE_" + emp.getRole().toUpperCase());
        return new User(emp.getEmail(), emp.getPassword(), Collections.singletonList(authority));
    }
}
