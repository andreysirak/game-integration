package com.example.platform.service.impl;

import com.example.platform.model.PlatformSession;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPlatformSessionManagerImplTest {

    @Test
    void createSession_WithValidUsernameAndCustomTTL() {
        String username = "testUser";
        long customTTL = 600L; // 10 minutes
        InMemoryPlatformSessionManagerImpl sessionManager = new InMemoryPlatformSessionManagerImpl();

        PlatformSession session = sessionManager.createSession(username, customTTL);

        assertNotNull(session, "Session should not be null");
        assertEquals(username, session.getUsername(), "Username should match");
        assertNotNull(session.getSessionId(), "Session ID should not be null");
        assertNotNull(session.getExpiresAt(), "Expiration time should not be null");
        assertNotNull(session.getLastAccessed(), "Last accessed time should not be null");
        assertFalse(session.isExpired(), "Session should not be expired immediately after creation");

        Instant now = Instant.now();
        assertTrue(session.getExpiresAt().isAfter(now), "Expiration time should be in the future");
    }

    @Test
    void createSession_WithValidUsernameAndDefaultTTL() {
        String username = "defaultUser";
        InMemoryPlatformSessionManagerImpl sessionManager = new InMemoryPlatformSessionManagerImpl();

        PlatformSession session = sessionManager.createSession(username, null);

        assertNotNull(session, "Session should not be null");
        assertEquals(username, session.getUsername(), "Username should match");
        assertNotNull(session.getSessionId(), "Session ID should not be null");
        assertNotNull(session.getExpiresAt(), "Expiration time should not be null");
        assertFalse(session.isExpired(), "Session should not be expired immediately after creation");

        Instant now = Instant.now();
        assertTrue(session.getExpiresAt().isAfter(now), "Expiration time should be in the future");
    }

    @Test
    void createSession_GeneratesUniqueSessionIds() {
        String username1 = "user1";
        String username2 = "user2";
        InMemoryPlatformSessionManagerImpl sessionManager = new InMemoryPlatformSessionManagerImpl();

        PlatformSession session1 = sessionManager.createSession(username1, null);
        PlatformSession session2 = sessionManager.createSession(username2, null);

        assertNotNull(session1, "First session should not be null");
        assertNotNull(session2, "Second session should not be null");
        assertNotEquals(session1.getSessionId(), session2.getSessionId(), "Session IDs should be unique");
    }
}