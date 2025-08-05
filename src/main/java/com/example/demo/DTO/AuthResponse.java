package com.example.demo.DTO;


public class AuthResponse {
    private String token;
    private String role;
    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
    // getters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    
}
