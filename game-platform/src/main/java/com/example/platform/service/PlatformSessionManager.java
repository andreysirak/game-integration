package com.example.platform.service;

import com.example.platform.model.PlatformSession;

/**
 * Manages user sessions within the platform, providing functionality for
 * creating, retrieving, and invalidating sessions. This interface abstracts
 * session management to allow different implementations, such as in-memory
 * or distributed session storage.
 */
public interface PlatformSessionManager {
    /**
     * Creates a new session for the specified user with an optional time-to-live (TTL).
     * If the TTL is not provided, the session will use a default expiration time.
     *
     * @param username the username associated with the session
     * @param ttlSeconds the desired duration of the session in seconds; if null, a default TTL is applied
     * @return the created PlatformSession containing the session details
     */
    PlatformSession createSession(String username, Long ttlSeconds);
    /**
     * Retrieves an existing session by its session identifier. Optionally, the session's
     * last accessed time can be updated, effectively extending its validity if the session
     * management implementation supports this behavior.
     *
     * @param sessionId the unique identifier of the session to retrieve
     * @param touch if true, the session's last accessed time is updated, extending its lifespan
     * @return the PlatformSession object associated with the given sessionId if it exists;
     *         null if the session does not exist or has expired
     */
    PlatformSession getSession(String sessionId, boolean touch);
    /**
     * Invalidates an existing session identified by the specified sessionId. This method
     * is used to terminate a session, making the associated session identifier no longer valid.
     *
     * @param sessionId the unique identifier of the session to invalidate. Must not be null.
     */
    void invalidateSession(String sessionId);
}
