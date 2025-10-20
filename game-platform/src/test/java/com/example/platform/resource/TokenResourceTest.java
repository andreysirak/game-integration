package com.example.platform.resource;

import com.example.platform.dto.TokenRequest;
import com.example.platform.exception.TokenManagerException;
import com.example.platform.model.PlatformSession;
import com.example.platform.service.PlatformSessionManager;
import com.example.platform.service.TokenManager;
import com.nimbusds.jose.JOSEException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenResourceTest {

    @Mock
    PlatformSessionManager sessions;

    @Mock
    TokenManager tokenManager;

    @InjectMocks
    TokenResource tokenResource;

    @Test
    public void testIssue_Unauthenticated_NoSessionReturns401() {
        when(sessions.getSession(anyString(), eq(true))).thenReturn(null);
        TokenRequest tokenRequest = new TokenRequest("sampleGame");

        Response response = tokenResource.issue(tokenRequest, "invalidSessionCookie");

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        Map<String, String> responseBody = (Map<String, String>) response.getEntity();
        assertNotNull(responseBody);
        assertEquals("not_authenticated", responseBody.get("error"));
        assertEquals("login required", responseBody.get("message"));
    }

    @Test
    public void testIssue_BadRequest_MissingGameIdReturns400() {
        PlatformSession session = getPlatformSession();
        when(sessions.getSession(anyString(), eq(true))).thenReturn(session);
        TokenRequest tokenRequest = new TokenRequest(null);

        Response response = tokenResource.issue(tokenRequest, "validSessionCookie");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Map<String, String> responseBody = (Map<String, String>) response.getEntity();
        assertNotNull(responseBody);
        assertEquals("missing_gameId", responseBody.get("error"));
    }

    @Test
    public void testIssue_Success_ReturnsValidToken() throws JOSEException {
        PlatformSession session = getPlatformSession();
        when(sessions.getSession(anyString(), eq(true))).thenReturn(session);
        when(tokenManager.buildToken(anyString(), anyString())).thenReturn("generatedToken123");
        TokenRequest tokenRequest = new TokenRequest("sampleGame");

        Response response = tokenResource.issue(tokenRequest, "validSessionCookie");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Map<String, String> responseBody = (Map<String, String>) response.getEntity();
        assertNotNull(responseBody);
        assertEquals("generatedToken123", responseBody.get("token"));
    }

    @Test
    public void testIssue_InternalServerError_ExceptionDuringTokenGeneration() throws JOSEException {
        PlatformSession session = getPlatformSession();
        when(sessions.getSession(anyString(), eq(true))).thenReturn(session);
        when(tokenManager.buildToken(anyString(), anyString())).thenThrow(new TokenManagerException("error while building token"));
        TokenRequest tokenRequest = new TokenRequest("sampleGame");

        Response response = tokenResource.issue(tokenRequest, "validSessionCookie");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        Map<String, String> responseBody = (Map<String, String>) response.getEntity();
        assertNotNull(responseBody);
        assertEquals("signing_failed", responseBody.get("error"));
        assertEquals("error while building token", responseBody.get("message"));
    }


    private PlatformSession getPlatformSession() {
        return new PlatformSession("sessionId123", "user123", Instant.now().plusSeconds(3600), Instant.now());
    }

}