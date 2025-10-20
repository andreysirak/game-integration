package com.example.platform.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserServiceImplTest {

    @Test
    void testVerifyPasswordWithCorrectCredentials() {
        InMemoryUserServiceImpl userService = new InMemoryUserServiceImpl();
        String username = "test";
        String password = "test";

        boolean result = userService.verifyPassword(username, password);

        assertTrue(result, "Password verification should succeed with correct credentials.");
    }

    @Test
    void testVerifyPasswordWithIncorrectPassword() {
        InMemoryUserServiceImpl userService = new InMemoryUserServiceImpl();
        String username = "test";
        String password = "wrongPassword";

        boolean result = userService.verifyPassword(username, password);

        assertFalse(result, "Password verification should fail with incorrect password.");
    }

    @Test
    void testVerifyPasswordWithNonExistentUsername() {
        InMemoryUserServiceImpl userService = new InMemoryUserServiceImpl();
        String username = "nonExistentUser";
        String password = "test";

        boolean result = userService.verifyPassword(username, password);

        assertFalse(result, "Password verification should fail for a non-existent username.");
    }

    @Test
    void testVerifyPasswordWithNullPassword() {
        InMemoryUserServiceImpl userService = new InMemoryUserServiceImpl();
        String username = "test";
        String password = null;

        boolean result = userService.verifyPassword(username, password);

        assertFalse(result, "Password verification should fail when the password is null.");
    }

    @Test
    void testVerifyPasswordWithNullUsername() {
        InMemoryUserServiceImpl userService = new InMemoryUserServiceImpl();
        String username = null;
        String password = "test";

        boolean result = userService.verifyPassword(username, password);

        assertFalse(result, "Password verification should fail when the username is null.");
    }

}