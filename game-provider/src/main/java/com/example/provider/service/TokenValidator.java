package com.example.provider.service;

import com.example.provider.exception.TokenSignatureVerifierException;
import com.example.provider.model.PlatformInfo;
import com.example.provider.model.TokenValidationResult;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

/**
 * The TokenValidator class is responsible for validating JSON Web Tokens (JWTs) used within the system.
 * It performs a series of checks on the token to ensure its authenticity, validity, and adherence to
 * platform-specific constraints. These validations include signature verification, issuer checks,
 * expiration checks, audience validation, game request validation, and platform registration checks.
 * Validation results are encapsulated in a TokenValidationResult object, which communicates either
 * a validation error or the successfully extracted claims contained in the token.
 */
@ApplicationScoped
public class TokenValidator {

    private static final String UNKNOWN_ISSUER = "unknown_issuer";
    private static final String INVALID_TOKEN = "invalid_token";
    private static final String EXPIRED_TOKEN = "expired_token";
    private static final String INTERNAL_ERROR = "internal_error";
    private static final String INVALID_AUDIENCE = "invalid_audience";
    private static final String GAME_REQUEST_MISMATCH = "game_request_mismatch";
    private static final String GAME_NOT_ALLOWED_FOR_PLATFORM = "game_not_allowed_for_platform";

    private final TokenSignatureVerifier tokenSignatureVerifier;
    private final PlatformRegistry platformRegistry;

    /**
     * Constructs a new instance of TokenValidator for handling token validation.
     *
     * @param tokenSignatureVerifier An instance of {@link TokenSignatureVerifier} used to verify
     *        the cryptographic signature of JWTs against the appropriate key set.
     * @param platformRegistry An instance of {@link PlatformRegistry} responsible for retrieving platform-related
     *        information associated with the token being validated.
     */
    public TokenValidator(TokenSignatureVerifier tokenSignatureVerifier, PlatformRegistry platformRegistry) {
        this.tokenSignatureVerifier = tokenSignatureVerifier;
        this.platformRegistry = platformRegistry;
    }

    /**
     * Validates a given JWT token for a specific game ensuring that it meets various criteria,
     * such as having a valid signature, being unexpired, matching the required audience, and other game-specific checks.
     * This method processes the provided token and determines if it is valid for the requested game.
     * If validation fails, the method returns a detailed error message. Otherwise, it returns the successfully processed claims.
     *
     * @param token The JWT token to be validated. This is typically provided in the Authorization header of a request.
     * @param requiredGame The unique identifier of the game for which the token is being validated.
     * @return A {@link TokenValidationResult} containing an error message if validation fails, or the
     *         associated {@link JWTClaimsSet} if the token is valid. If validation fails, the claims will be null.
     */
    public TokenValidationResult validate(String token, String requiredGame) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();

            String error = validateToken(requiredGame, claimsSet, jwt);
            if (error != null) {
                return new TokenValidationResult(error, null);
            } else {
                return new TokenValidationResult(null, claimsSet);
            }
        } catch (ParseException | JOSEException e) {
            return new TokenValidationResult(INVALID_TOKEN, null);
        } catch (TokenSignatureVerifierException e) {
            return new TokenValidationResult(INTERNAL_ERROR, null);
        }
    }

    private String validateToken(String requiredGame, JWTClaimsSet claimsSet, SignedJWT jwt)
            throws ParseException, JOSEException {
        String issuer = claimsSet.getIssuer();
        PlatformInfo platformInfo = platformRegistry.getPlatformInfo(issuer);
        if (issuer == null) {
            return UNKNOWN_ISSUER;
        } else if (platformInfo == null) {
            return UNKNOWN_ISSUER;
        } else if (!verifySignature(jwt, platformInfo)) {
            return INVALID_TOKEN;
        } else if (isExpired(claimsSet)) {
            return EXPIRED_TOKEN;
        } else if (!isAudienceMatches(claimsSet)) {
            return INVALID_AUDIENCE;
        } else if (!isGameRequestMatches(claimsSet, requiredGame)) {
            return GAME_REQUEST_MISMATCH;
        } else if (!isGameRegisteredInPlatform(platformInfo, requiredGame)) {
            return GAME_NOT_ALLOWED_FOR_PLATFORM;
        } else {
            return null;
        }
    }
    private boolean verifySignature(SignedJWT jwt, PlatformInfo platformInfo) throws JOSEException, ParseException {
        return tokenSignatureVerifier.verifySignature(jwt, platformInfo);
    }

    private boolean isExpired(JWTClaimsSet claimsSet) {
        Date exp = claimsSet.getExpirationTime();
        return exp == null || exp.toInstant().isBefore(Instant.now());
    }

    private boolean isGameRequestMatches(JWTClaimsSet claimsSet, String requiredGame) {
        String gameClaim = (String) claimsSet.getClaim("game");
        if (gameClaim == null) return false;
        return requiredGame.equals(gameClaim);
    }

    private boolean isGameRegisteredInPlatform(PlatformInfo platformInfo, String requiredGame) {
        return platformInfo.registeredGames() != null && platformInfo.registeredGames().contains(requiredGame);
    }

    private boolean isAudienceMatches(JWTClaimsSet claimsSet) {
        return claimsSet.getAudience().contains("game-provider");
    }
}