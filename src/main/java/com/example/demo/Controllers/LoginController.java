package com.example.demo.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // this maps to login.html
    }

    @GetMapping("/default")
     public String redirectBasedOnRole(Authentication auth) {
    if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        return "redirect:/admin";
    } else {
        return "redirect:/submit";
    }
}
}