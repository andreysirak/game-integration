package com.example.provider.model;

import java.util.Set;

/**
 * The PlatformInfo record serves as a container for core details associated with an external platform
 * integrated into the system. This information is utilized for validating tokens and related operations
 * in the context of platform management and access control.
 * The record encapsulates the following:
 * 1. platformId - The unique identifier of the platform.
 * 2. jwksUrl - The URL where the platform's JSON Web Key Set (JWKS) can be fetched.
 * 3. registeredGames - A set of games that are registered and authorized for this platform.
 * This record is primarily leveraged by various components, such as the TokenValidator and
 * TokenSignatureVerifier, to enforce platform-specific constraints, including verifying platform
 * JWTs, validating token audience, and authorizing game access based on platform registrations.
 */
public record PlatformInfo(String platformId, String jwksUrl, Set<String> registeredGames) {
}