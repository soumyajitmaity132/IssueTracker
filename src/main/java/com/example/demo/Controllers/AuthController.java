package com.example.demo.Controllers;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.DTO.AuthRequest;
import com.example.demo.DTO.AuthResponse;
import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import com.example.demo.SecurityConfiguration.*;
import com.example.demo.security.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private EmployeeRepository employeeRepository;

    @Autowired
private ActiveUserStore activeUserStore;

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    Employee emp = employeeRepository.findByEmail(request.getEmail()).orElse(null);
    if (emp == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

    String token = jwtTokenUtil.generateToken(emp.getEmail(), emp.getRole());

    // Store active user
    activeUserStore.add(token, emp.getEmail());

    return ResponseEntity.ok(new AuthResponse(token, emp.getRole()));
}


    @PostMapping("/logout")
public ResponseEntity<?> logout(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        activeUserStore.remove(token);
    }
    return ResponseEntity.ok("Logged out successfully");
}


    @GetMapping("/active-users")
@PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
public ResponseEntity<?> getActiveUsers() {
    return ResponseEntity.ok(activeUserStore.getActiveUsers());
}

     @GetMapping("/me")
//@PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
public ResponseEntity<?> getMyDetails() {
    // Get logged-in user's email from JWT token
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    // Fetch employee from DB
    Employee emp = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    // Mask password before returning
    emp.setPassword(null);

    return ResponseEntity.ok(emp);
}



}
