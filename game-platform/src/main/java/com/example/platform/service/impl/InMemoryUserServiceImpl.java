package com.example.platform.service.impl;

import com.example.platform.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory user store for POC.
 * Stores username -> bcrypt hashed password.
 *'
 * For testing purposes this version uses a static initializer so the test user
 * is available as soon as the class is loaded.
 */
@ApplicationScoped
public class InMemoryUserServiceImpl implements UserService {
    private static final Map<String, String> STORE = new ConcurrentHashMap<>();

    static {
        // Create a test user 'test' with password 'test' for testing
        String hashed = BCrypt.hashpw("test", BCrypt.gensalt(10));
        STORE.put("test", hashed);
    }

    @Override
    public boolean userExists(String username) {
        return STORE.containsKey(username);
    }

    @Override
    public boolean verifyPassword(String username, String password) {
        if (username == null || password == null) return false;
        String hashed = STORE.get(username);
        if (hashed == null) return false;
        return BCrypt.checkpw(password, hashed);
    }
}