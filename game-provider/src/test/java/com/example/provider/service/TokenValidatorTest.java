package com.example.provider.service;

import com.example.provider.model.PlatformInfo;
import com.example.provider.model.TokenValidationResult;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class TokenValidatorTest {

    @Mock
    TokenSignatureVerifier mockTokenSignatureVerifier;

    @Mock
    PlatformRegistry mockPlatformRegistry;

    @InjectMocks
    TokenValidator tokenValidator;

    private static final byte[] SECRET = "your-256-bit-secret-key-here-32bytes".getBytes();

    @Test
    void shouldReturnInvalidTokenWhenTokenParsingFails() {
        String invalidToken = "invalid-token";

        TokenValidationResult result = tokenValidator.validate(invalidToken, "requiredGame");

        assertEquals("invalid_token", result.error());
        assertNull(result.claims());
    }

    @Test
    void shouldReturnUnknownIssuerWhenClaimSetIssuerIsNull() throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().build();
        String token = createSignedToken(claimsSet);

        TokenValidationResult result = tokenValidator.validate(token, "requiredGame");

        assertEquals("unknown_issuer", result.error());
        assertNull(result.claims());
    }

    @Test
    void shouldReturnInvalidTokenWhenSignatureVerificationFails() throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("knownIssuer")
                .build();
        String token = createSignedToken(claimsSet);

        Mockito.when(mockPlatformRegistry.getPlatformInfo("knownIssuer"))
                .thenReturn(new PlatformInfo("knownIssuer", "https://example.com/jwks", 
                        new HashSet<>(Collections.singleton("requiredGame"))));
        Mockito.when(mockTokenSignatureVerifier.verifySignature(any(SignedJWT.class), 
                any(PlatformInfo.class))).thenReturn(false);

        TokenValidationResult result = tokenValidator.validate(token, "requiredGame");

        assertEquals("invalid_token", result.error());
        assertNull(result.claims());
    }

    @Test
    void shouldReturnExpiredTokenWhenTokenIsExpired() throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("knownIssuer")
                .expirationTime(Date.from(Instant.now().minusSeconds(60)))
                .build();
        String token = createSignedToken(claimsSet);

        Mockito.when(mockPlatformRegistry.getPlatformInfo("knownIssuer"))
                .thenReturn(new PlatformInfo("knownIssuer", "https://example.com/jwks", 
                        new HashSet<>(Collections.singleton("requiredGame"))));
        Mockito.when(mockTokenSignatureVerifier.verifySignature(any(SignedJWT.class), 
                any(PlatformInfo.class))).thenReturn(true);

        TokenValidationResult result = tokenValidator.validate(token, "requiredGame");

        assertEquals("expired_token", result.error());
        assertNull(result.claims());
    }

    @Test
    void shouldReturnInvalidAudienceWhenAudienceDoesNotMatch() throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("knownIssuer")
                .expirationTime(Date.from(Instant.now().plusSeconds(60)))
                .audience("different-audience")
                .build();
        String token = createSignedToken(claimsSet);

        Mockito.when(mockPlatformRegistry.getPlatformInfo("knownIssuer"))
                .thenReturn(new PlatformInfo("knownIssuer", "https://example.com/jwks", 
                        new HashSet<>(Collections.singleton("requiredGame"))));
        Mockito.when(mockTokenSignatureVerifier.verifySignature(any(SignedJWT.class), 
                any(PlatformInfo.class))).thenReturn(true);

        TokenValidationResult result = tokenValidator.validate(token, "requiredGame");

        assertEquals("invalid_audience", result.error());
        assertNull(result.claims());
    }

    @Test
    void shouldReturnGameRequestMismatchWhenGameDoesNotMatch() throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("knownIssuer")
                .expirationTime(Date.from(Instant.now().plusSeconds(60)))
                .audience("game-provider")
                .claim("game", "differentGame")
                .build();
        String token = createSignedToken(claimsSet);

        Mockito.when(mockPlatformRegistry.getPlatformInfo("knownIssuer"))
                .thenReturn(new PlatformInfo("knownIssuer", "https://example.com/jwks", 
                        new HashSet<>(Collections.singleton("requiredGame"))));
        Mockito.when(mockTokenSignatureVerifier.verifySignature(any(SignedJWT.class), 
                any(PlatformInfo.class))).thenReturn(true);

        TokenValidationResult result = tokenValidator.validate(token, "requiredGame");

        assertEquals("game_request_mismatch", result.error());
        assertNull(result.claims());
    }

    @Test
    void shouldReturnGameNotAllowedForPlatformWhenGameIsNotRegistered() throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("knownIssuer")
                .expirationTime(Date.from(Instant.now().plusSeconds(60)))
                .audience("game-provider")
                .claim("game", "requiredGame")
                .build();
        String token = createSignedToken(claimsSet);

        Mockito.when(mockPlatformRegistry.getPlatformInfo("knownIssuer"))
                .thenReturn(new PlatformInfo("knownIssuer", "https://example.com/jwks", new HashSet<>()));
        Mockito.when(mockTokenSignatureVerifier.verifySignature(any(SignedJWT.class), 
                any(PlatformInfo.class))).thenReturn(true);

        TokenValidationResult result = tokenValidator.validate(token, "requiredGame");

        assertEquals("game_not_allowed_for_platform", result.error());
        assertNull(result.claims());
    }

    @Test
    void shouldReturnValidResultWhenAllValidationsPass() throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("knownIssuer")
                .expirationTime(Date.from(Instant.now().plusSeconds(60)))
                .audience("game-provider")
                .claim("game", "requiredGame")
                .build();
        String token = createSignedToken(claimsSet);

        Mockito.when(mockPlatformRegistry.getPlatformInfo("knownIssuer"))
                .thenReturn(new PlatformInfo("knownIssuer", "https://example.com/jwks", 
                        new HashSet<>(Collections.singleton("requiredGame"))));
        Mockito.when(mockTokenSignatureVerifier.verifySignature(any(SignedJWT.class), 
                any(PlatformInfo.class))).thenReturn(true);

        TokenValidationResult result = tokenValidator.validate(token, "requiredGame");

        assertNull(result.error());
        JWTClaimsSet resultClaims = result.claims();
        assertEquals(claimsSet.getIssuer(), resultClaims.getIssuer());
        assertEquals(claimsSet.getAudience(), resultClaims.getAudience());
        assertEquals(claimsSet.getClaim("game"), resultClaims.getClaim("game"));

    }

    private String createSignedToken(JWTClaimsSet claimsSet) throws JOSEException {
        JWSSigner signer = new MACSigner(SECRET);
        
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).build(),
                claimsSet);
        
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}