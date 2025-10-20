package com.example.provider.model;

import com.nimbusds.jwt.JWTClaimsSet;

/**
 * TokenValidationResult is a simple container that represents the result of a token validation process.
 * It contains information about any validation error that occurred and the associated JWTClaimsSet
 * if the token was successfully validated.
 * This record is utilized primarily to indicate the outcome of token validation operations in the system.
 *
 * @param error  A string representing the error encountered during validation. If no error occurred, this is null.
 * @param claims The JWTClaimsSet associated with the token if it was successfully validated. If validation fails, this is null.
 */
public record TokenValidationResult(String error, JWTClaimsSet claims) {
}