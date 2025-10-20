package com.example.platform.resource;

import com.example.platform.dto.LoginRequest;
import com.example.platform.model.PlatformSession;
import com.example.platform.service.PlatformSessionManager;
import com.example.platform.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

/**
 * The `LoginResource` class provides RESTful endpoints for managing user login
 * and logout functionality. It handles user authentication, session management,
 * and ensures the proper handling of session cookies for requests.
 */
@Path("/player")
public class LoginResource {

    private final UserService userService;
    private final PlatformSessionManager sessionManager;

    /**
     * Constructs a new instance of the LoginResource class, initializing it with
     * the required UserService and PlatformSessionManager components.
     *
     * @param userService the UserService instance used for validating user credentials.
     *                    This must not be null.
     * @param sessionManager the PlatformSessionManager instance used for managing
     *                       user sessions. This must not be null.
     */
    public LoginResource (UserService userService, PlatformSessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    /**
     * Authenticates a user based on the provided credentials in the login request.
     * If the username or password is not provided, the method returns a BAD_REQUEST response.
     * If the credentials are invalid, the method returns an UNAUTHORIZED response.
     * Upon successful authentication, the method returns an OK response with a session cookie.
     *
     * @param loginRequest the login request containing the user's username and password
     * @return a Response indicating the result of the authentication:
     *         - BAD_REQUEST if required credentials are missing
     *         - UNAUTHORIZED if authentication fails
     *         - OK with a session cookie if authentication is successful
     */
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "missing_credentials"))
                    .build();
        }
        if (!userService.userExists(username) || !userService.verifyPassword(username, password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "invalid_credentials"))
                    .build();
        }
        return Response
                .ok(Map.of("status", "ok", "username", username))
                .cookie(createNewSessionCookie(sessionManager.createSession(username, null)))
                .build();
    }

    /**
     * Ends the current session by invalidating the provided session identifier and
     * setting an expired session cookie. If the session cookie is not provided,
     * the method returns a BAD_REQUEST response.
     *
     * @param cookieSession The session identifier provided via the "PLATFORM_SESS" cookie.
     *                      If null, it indicates that no session cookie was provided in the request.
     * @return A Response object indicating the result of the logout operation:
     *         - BAD_REQUEST if the session cookie is missing.
     *         - OK with an expired session cookie if the logout is successful.
     */
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@CookieParam("PLATFORM_SESS") String cookieSession) {
        if (cookieSession == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "missing_session"))
                    .build();
        }
        sessionManager.invalidateSession(cookieSession);
        return Response
                .ok(Map.of("status", "ok")).cookie(createExpiredSessionCookie())
                .build();
    }

    private static NewCookie createNewSessionCookie(PlatformSession session) {
        Cookie cookieBase = new Cookie.Builder("PLATFORM_SESS")
                .value(session.getSessionId())
                .path("/")
                .build();
        return new NewCookie.Builder(cookieBase)
                .path("/")
                .maxAge(20 * 60)
                .secure(false)
                .httpOnly(true)
                .comment("platform session")
                .build();
    }

    private static NewCookie createExpiredSessionCookie() {
        Cookie expiredBase = new Cookie.Builder("PLATFORM_SESS")
                .value("")
                .path("/")
                .build();
        return new NewCookie.Builder(expiredBase)
                .maxAge(0)
                .secure(false)
                .httpOnly(true)
                .comment("platform session")
                .build();
    }
}