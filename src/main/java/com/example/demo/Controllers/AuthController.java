package com.example.demo.Controllers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.DTO.AuthRequest;
import com.example.demo.DTO.AuthResponse;
import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import com.example.demo.SecurityConfiguration.*;
import com.example.demo.security.JwtTokenUtil;

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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtTokenUtil jwtTokenUtil;
    @Autowired private EmployeeRepository employeeRepository;

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    try {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Auth error: " + e.getMessage());
    }

    Employee emp = employeeRepository.findByEmail(request.getEmail()).orElse(null);
    if (emp == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

    String token = jwtTokenUtil.generateToken(emp.getEmail(), emp.getRole());
    return ResponseEntity.ok(new AuthResponse(token, emp.getRole()));
}

}
