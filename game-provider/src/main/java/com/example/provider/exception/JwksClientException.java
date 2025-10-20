package com.example.provider.exception;

/**
 * JwksClientException is a custom runtime exception that encapsulates errors encountered during
 *  HTTP requests
 * This exception is used in the context of the JwksClient class to signal issues such as
 * connection errors, invalid responses, or failed HTTP operations while fetching JWKS.
 * It provides constructors to create an empty exception or initialize it with a specific error message.
 */
public class JwksClientException extends RuntimeException {
    public JwksClientException() {
    }

    public JwksClientException(String message) {
        super(message);
    }
}
