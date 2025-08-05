package com.example.demo.security;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ActiveUserStore {
    private final Map<String, String> activeTokens = new ConcurrentHashMap<>();

    public void add(String token, String email) {
        activeTokens.put(token, email);
    }

    public Collection<String> getActiveUsers() {
        return activeTokens.values();
    }

    public void remove(String token) {
        activeTokens.remove(token);
    }
}
