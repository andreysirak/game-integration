package com.example.provider.resource;

import com.example.provider.model.TokenValidationResult;
import com.example.provider.service.TokenValidator;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameResourceTest {

    @Mock
    TokenValidator tokenValidator;

    @InjectMocks
    GameResource gameResource;

    @Test
    public void testPlayMissingAuthorizationHeader() {
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        Response response = gameResource.play("12345", headers);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(Map.of("error", "missing_token", "message", "Authorization header missing"), response.getEntity());
    }

    @Test
    public void testPlayInvalidAuthorizationHeader() {
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidToken");

        Response response = gameResource.play("12345", headers);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(Map.of("error", "missing_token", "message", "Authorization header missing"), response.getEntity());
    }

    @Test
    public void testPlayTokenValidationErrorUnauthorized() {
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer validToken");
        when(tokenValidator.validate("validToken", "12345"))
                .thenReturn(new TokenValidationResult("unauthorized", null));

        Response response = gameResource.play("12345", headers);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(Map.of("error", "unauthorized", "message", "unauthorized"), response.getEntity());
    }

    @Test
    public void testPlayTokenValidationErrorForbidden() {
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer validToken");
        when(tokenValidator.validate("validToken", "12345"))
                .thenReturn(new TokenValidationResult("game_mismatch", null));

        Response response = gameResource.play("12345", headers);

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals(Map.of("error", "game_mismatch", "message", "game_mismatch"), response.getEntity());
    }

    @Test
    public void testPlayTokenValidationErrorInternal() {
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer validToken");
        when(tokenValidator.validate("validToken", "12345"))
                .thenReturn(new TokenValidationResult("internal_error", null));

        Response response = gameResource.play("12345", headers);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(Map.of("error", "internal_error", "message", "internal_error"), response.getEntity());
    }

    @Test
    public void testPlaySuccess() {
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer validToken");
        when(tokenValidator.validate("validToken", "12345"))
                .thenReturn(new TokenValidationResult(null, null));

        Response response = gameResource.play("12345", headers);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Map.of("status", "ok", "gameId", "12345"), response.getEntity());
    }
}