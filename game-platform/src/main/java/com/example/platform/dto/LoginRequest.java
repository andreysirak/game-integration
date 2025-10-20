package com.example.platform.dto;

/**
 * Represents a user's login request containing username and password.
 * This record is typically used as a data structure for authentication purposes,
 * where the provided credentials are validated to authorize user access.
 * Immutable and concise by design, the LoginRequest encapsulates the necessary
 * details required for a login operation.
 */
public record LoginRequest(String username, String password) {
}
