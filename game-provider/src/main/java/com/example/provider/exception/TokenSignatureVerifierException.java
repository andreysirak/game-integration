package com.example.provider.exception;

/**
 * TokenSignatureVerifierException is a custom runtime exception that represents issues or failures
 * encountered during the process of verifying a token's signature.
 * This exception is typically thrown in the context of signature verification when a problem occurs,
 * such as issues with fetching the necessary JWKS or verifying the token's cryptographic signature.
 * It serves as an indicator of problems that prevent the system from successfully validating the token
 * signature, often acting as a wrapper for lower-level exceptions that arise during key fetching or
 * signature verification.
 */
public class TokenSignatureVerifierException extends RuntimeException {
    public TokenSignatureVerifierException() {
    }
}
