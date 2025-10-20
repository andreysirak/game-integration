package com.example.platform.model;

import java.time.Instant;

/**
 * Represents a session for a platform user, encapsulating session identification,
 * user information, and session lifecycle management.
 * Instances of this class are immutable except for the session expiration
 * and last accessed time, which are designed to be updated dynamically to
 * extend the session duration or mark activity.
 */
public class PlatformSession {
    private final String sessionId;
    private final String username;
    private volatile Instant expiresAt;
    private volatile Instant lastAccessed;

    public PlatformSession(String sessionId, String username, Instant expiresAt, Instant lastAccessed) {
        this.sessionId = sessionId;
        this.username = username;
        this.expiresAt = expiresAt;
        this.lastAccessed = lastAccessed;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return username;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void touch(long ttlSeconds) {
        this.lastAccessed = Instant.now();
        this.expiresAt = this.lastAccessed.plusSeconds(ttlSeconds);
    }
}