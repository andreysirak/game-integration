package com.example.platform.dto;

/**
 * Represents a request to issue a token for a specific game.
 * This record encapsulates the necessary information required to
 * generate a token, primarily the identifier of the game (`gameId`).
 * It is utilized in API endpoints related to token issuance to
 * authenticate and authorize requests, ensuring that all required
 * parameters are provided.
 * As an immutable data structure, the `TokenRequest` ensures
 * data integrity and simplicity during the token issuance process.
 */
public record TokenRequest(String gameId) {
}
