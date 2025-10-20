package com.example.platform.resource;

import com.example.platform.dto.LoginRequest;
import com.example.platform.model.PlatformSession;
import com.example.platform.service.PlatformSessionManager;
import com.example.platform.service.UserService;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginResourceTest {

    @Mock
    private UserService userService;

    @Mock
    private PlatformSessionManager sessionManager;

    @InjectMocks
    private LoginResource loginResource;

    @Test
    public void testLogin_MissingCredentials_BadRequest() {
        LoginRequest loginRequest = new LoginRequest(null, null);

        Response response = loginResource.login(loginRequest);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(Map.of("error", "missing_credentials"), response.getEntity());
    }

    @Test
    public void testLogin_InvalidCredentials_Unauthorized() {
        LoginRequest loginRequest = new LoginRequest("user", "wrongpass");
        when(userService.userExists("user")).thenReturn(true);
        when(userService.verifyPassword("user", "wrongpass")).thenReturn(false);

        Response response = loginResource.login(loginRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(Map.of("error", "invalid_credentials"), response.getEntity());
    }

    @Test
    public void testLogin_ValidCredentials_Success() {
        LoginRequest loginRequest = new LoginRequest("user", "password");
        when(userService.userExists("user")).thenReturn(true);
        when(userService.verifyPassword("user", "password")).thenReturn(true);
        PlatformSession mockSession = mock(PlatformSession.class);
        when(mockSession.getSessionId()).thenReturn("session123");
        when(sessionManager.createSession("user", null)).thenReturn(mockSession);

        Response response = loginResource.login(loginRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Map.of("status", "ok", "username", "user"), response.getEntity());
        NewCookie sessionCookie = response.getCookies().get("PLATFORM_SESS");
        assertEquals("session123", sessionCookie.getValue());
    }
}