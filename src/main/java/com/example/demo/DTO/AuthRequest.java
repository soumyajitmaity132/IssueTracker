package com.example.demo.DTO;


public class AuthRequest {
    private String email;
    private String password;
    // getters and setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public AuthRequest(){
        
    }
}
