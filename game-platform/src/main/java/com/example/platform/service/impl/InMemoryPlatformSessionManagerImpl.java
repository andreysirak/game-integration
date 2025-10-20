package com.example.platform.service.impl;

import com.example.platform.model.PlatformSession;
import com.example.platform.service.PlatformSessionManager;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory session manager for platform user sessions.
 */
@ApplicationScoped
public class InMemoryPlatformSessionManagerImpl implements PlatformSessionManager {

    private final Map<String, PlatformSession> sessionStore = new ConcurrentHashMap<>();
    private final long defaultTtlSeconds = 20 * 60; // 20 minutes

    @Override
    public PlatformSession createSession(String username, Long ttlSeconds) {
        long ttl = (ttlSeconds != null) ? ttlSeconds : defaultTtlSeconds;
        String sid = UUID.randomUUID().toString();
        Instant now = Instant.now();
        PlatformSession s = new PlatformSession(sid, username, now.plusSeconds(ttl), now);
        sessionStore.put(sid, s);
        return s;
    }

    @Override
    public PlatformSession getSession(String sessionId, boolean touch) {
        if (sessionId == null) return null;
        PlatformSession platformSession = sessionStore.get(sessionId);
        if (platformSession == null) return null;
        if (platformSession.isExpired()) {
            sessionStore.remove(sessionId);
            return null;
        }
        if (touch) {
            platformSession.touch(defaultTtlSeconds);
        }
        return platformSession;
    }

    @Override
    public void invalidateSession(String sessionId) {
        if (sessionId != null) sessionStore.remove(sessionId);
    }
}