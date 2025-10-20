package com.example.platform.resource;

import com.example.platform.dto.TokenRequest;
import com.example.platform.exception.TokenManagerException;
import com.example.platform.model.PlatformSession;
import com.example.platform.service.PlatformSessionManager;
import com.example.platform.service.TokenManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.Map;

/**
 * This resource exposes an endpoint to issue tokens tied to specific game sessions,
 * ensuring that only authenticated users can request and obtain valid tokens. It
 * depends on session and token management services for authentication, validation,
 * and token creation.
 *
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TokenResource {

    private final PlatformSessionManager sessions;
    private final TokenManager tokenManager;

    /**
     * Constructs a new instance of the TokenResource class to handle token issuance
     * requests, ensuring authenticated sessions and secure token generation.
     *
     * @param sessions an instance of PlatformSessionManager used to manage user sessions,
     *                 including validation and retrieval of active sessions. Must not be null.
     * @param tokenManager an instance of TokenManager responsible for generating and signing
     *                     tokens for authenticated users and specific game sessions. Must not be null.
     */
    public TokenResource(PlatformSessionManager sessions, TokenManager tokenManager) {
        this.sessions = sessions;
        this.tokenManager = tokenManager;
    }

    /**
     * Issues a token for a specified game session based on the authenticated user's session.
     * This method validates the user's session using the provided session cookie. If the user
     * is authenticated and specifies a valid game ID, a signed token is generated and returned.
     * Otherwise, appropriate error responses are returned.
     *
     * @param tokenRequest The request containing the `gameId` for which the token is being issued.
     * @param cookieSession The session identifier provided via the "PLATFORM_SESS" cookie.
     *                      It is used to validate the user's session.
     * @return A Response object:
     *         - 200 OK: If the token is successfully created. The response contains the generated token.
     *         - 400 BAD REQUEST: If the `gameId` is missing in the request.
     *         - 401 UNAUTHORIZED: If the session is invalid or the user is not authenticated.
     *         - 500 INTERNAL SERVER ERROR: If an error occurs during token signing.
     */
    @POST
    @Path("/issue")
    public Response issue(TokenRequest tokenRequest, @CookieParam("PLATFORM_SESS") String cookieSession) {
        PlatformSession session = sessions.getSession(cookieSession, true);
        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "not_authenticated", "message", "login required"))
                    .build();
        }
        String gameId = tokenRequest.gameId();
        if (gameId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "missing_gameId"))
                    .build();
        }
        try {
            return Response.ok(Map.of("token", tokenManager.buildToken(session.getUsername(), gameId)))
                    .build();
        } catch (TokenManagerException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "signing_failed", "message", e.getMessage()))
                    .build();
        }
    }

}