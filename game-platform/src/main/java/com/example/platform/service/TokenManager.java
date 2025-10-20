package com.example.platform.service;

import com.example.platform.exception.TokenManagerException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * The TokenManager class is responsible for generating and signing JSON Web Tokens (JWTs).
 * These tokens are created with certain claims such as issuer, subject, audience, game details,
 * and include metadata such as issue time and expiration time. The tokens are digitally signed
 * using RSA keys provided by the KeyManager.
 * The purpose of this class is to provide secure token generation, which can be used for
 * authentication or authorization purposes in the platform context.
 *
 */
@ApplicationScoped
public class TokenManager {

    private static final long DEFAULT_TTL_SECONDS = 20 * 60;

    private final KeyManager keyManager;

    /**
     * Constructor for the TokenManager class, which initializes the token generation and signing process
     * by depending on the provided KeyManager instance.
     *
     * @param keyManager the KeyManager instance to manage RSA keys used for signing the generated tokens.
     *                   Must not be null.
     */
    public TokenManager(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    /**
     * Generates and signs a JSON Web Token (JWT) containing specific claims such as player ID,
     * game ID, issuer, audience, issue time, expiration time, and a unique token ID. The token
     * is signed using the platform's RSA private key.
     *
     * @param playerId the identifier of the player for whom the token is being generated. Must not be null or empty.
     * @param gameId the identifier of the game session associated with the token. Must not be null or empty.
     * @return a signed and serialized JWT as a String.
     * @throws TokenManagerException if an error occurs during the signing process.
     */
    public String buildToken(String playerId, String gameId)  {
        try {
            Instant now = Instant.now();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer("platform-A")
                    .subject(playerId)
                    .audience("game-provider")
                    .claim("game", gameId)
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(DEFAULT_TTL_SECONDS)))
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(keyManager.getRsaJwk().getKeyID())
                    .type(JOSEObjectType.JWT)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);
            RSASSASigner signer = new RSASSASigner(keyManager.getRsaJwk().toPrivateKey());
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new TokenManagerException("Error while creating token");
        }
    }
}
