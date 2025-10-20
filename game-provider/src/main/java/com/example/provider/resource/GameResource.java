package com.example.provider.resource;

import com.example.provider.model.TokenValidationResult;
import com.example.provider.service.TokenValidator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.*;

/**
 * GameResource provides RESTful endpoints for managing game-related operations.
 * It processes requests for playing games and validates authorization tokens
 * to ensure secure access control to game resources.
 * This resource consumes and produces JSON and handles token validation errors
 * and success responses appropriately.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameResource {

    private final TokenValidator tokenValidator;

    /**
     * Constructs a new GameResource instance.
     *
     * @param tokenValidator The TokenValidator instance responsible for validating
     *                       JSON Web Tokens (JWTs) to ensure authentication and
     *                       authorization for game requests.
     */
    public GameResource(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    /**
     * Processes a request to play a game by validating the authorization token provided
     * in the HTTP headers and handling errors or successful responses accordingly.
     *
     * @param gameId The unique identifier of the game being accessed.
     * @param headers The HTTP headers of the request, used to extract the authorization information.
     * @return A Response object indicating the outcome of the operation.
     *         - Returns a 401 UNAUTHORIZED status with an error message if the Authorization header is missing or invalid.
     *         - Returns a 401 UNAUTHORIZED or 403 FORBIDDEN status with an error message when token validation fails.
     *         - Returns a 500 INTERNAL SERVER ERROR status for internal errors during token validation.
     *         - Returns a 200 OK status with game details on successful execution.
     */
    @POST
    @Path("/{gameId}/play")
    public Response play(@PathParam("gameId") String gameId,
                         @Context HttpHeaders headers) {

        String hdr = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (hdr == null || !hdr.toLowerCase().startsWith("bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "missing_token", "message", "Authorization header missing")).build();
        }

        TokenValidationResult tokenValidationResult = tokenValidator.validate(hdr.substring(7).trim(), gameId);

        if (tokenValidationResult.error() != null) {
            return generateErrorResponse(tokenValidationResult);
        } else {
            return Response.ok(Map.of(
                    "status", "ok",
                    "gameId", gameId
            )).build();
        }
    }

    private Response generateErrorResponse(TokenValidationResult tokenValidationResult) {
        String error = tokenValidationResult.error();
        if ("internal_error".equals(error)) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", error, "message", error)).build();
        }
        else if ("game_mismatch".equals(error) || "game_not_allowed_for_platform".equals(error)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", error, "message", error)).build();
        }
        else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", error, "message", error)).build();
        }
    }
}