package com.example.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("superadmin");
System.out.println(hash);
System.out.println(encoder.matches("mait", "$2a$10$7obip36xeW9QrZZPoP4ZneV.UZL6253w8SETH1hAkOXa8DQJoFDSa"));  // This must return true

}
}
