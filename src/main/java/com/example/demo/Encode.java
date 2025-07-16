package com.example.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encode {
    public static void main(String[] args) {
        String rawPassword1 = "user123"; // Change as needed
        String hashed = new BCryptPasswordEncoder().encode(rawPassword1);
        System.out.println("Encoded password for \"" + rawPassword1 + "\": " + hashed);
        String rawPassword2 = "admin123"; // Change as needed
        String hashed2 = new BCryptPasswordEncoder().encode(rawPassword2);
        System.out.println("Encoded password for \"" + rawPassword2 + "\": " + hashed2);
    }
}