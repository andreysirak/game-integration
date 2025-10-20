package com.example.platform.resource;


import com.example.platform.service.KeyManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Mocks JWKS endpoint that exposes the platform's public keys.
 * This resource mocks a trusted authority for testing purposes only.
 * It serves a JWKS document at /.well-known/jwks.json so test clients and
 * integration tests can retrieve the platform's public keys and verify tokens.
 *
 */
@Path("/.well-known")
@Produces(MediaType.APPLICATION_JSON)
public class JwksResource {

    private final KeyManager keyManager;

    /**
     * Constructs a new instance of the JwksResource class.
     *
     * @param keyManager1 the KeyManager instance used to manage and provide
     *                    the JSON Web Key Set (JWKS) for the JWKS endpoint.
     *                    This must not be null.
     */
    public JwksResource(KeyManager keyManager1) {
        this.keyManager = keyManager1;
    }

    @GET
    @Path("/jwks.json")
    public Response jwks() {
        return Response.ok(keyManager.getJwkSet().toJSONObject()).build();
    }
}