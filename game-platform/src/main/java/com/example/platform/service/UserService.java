package com.example.platform.service;

/**
 * Defines the contract for user-related operations such as user existence verification
 * and password validation. This interface acts as an abstraction for any concrete
 * user-related service implementations.
 */
public interface UserService {
    /**
     * Checks whether a user with the specified username exists.
     *
     * @param username the username to check for existence. Must not be null or empty.
     * @return true if the user exists, false otherwise.
     */
    boolean userExists(String username);
    /**
     * Verifies whether the provided password is valid for the specified username.
     *
     * @param username the username whose password needs to be verified. Must not be null or empty.
     * @param password the password to be validated for the username. Must not be null or empty.
     * @return true if the password is valid for the given username; false otherwise.
     */
    boolean verifyPassword(String username, String password);
}
