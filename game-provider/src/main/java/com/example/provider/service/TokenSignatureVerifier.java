package com.example.provider.service;

import com.example.provider.client.JwksClient;
import com.example.provider.exception.JwksClientException;
import com.example.provider.exception.TokenSignatureVerifierException;
import com.example.provider.model.PlatformInfo;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

/**
 * TokenSignatureVerifier is responsible for verifying the cryptographic signature
 * of signed JSON Web Tokens (JWTs) using public keys fetched from a JSON Web Key Set (JWKS).
 * This class leverages a JwksClient to retrieve the necessary JWKS for a given platform
 * and uses those keys to validate the signature of a received token. It supports retrieving
 * keys by the Key ID (kid) present in the token's header and falls back to using the
 * first available key in the key set if no matching key is found.
 */
@ApplicationScoped
public class TokenSignatureVerifier {

    private final JwksClient jwksClient;

    /**
     * Constructs a new TokenSignatureVerifier instance to verify JSON Web Token (JWT) signatures
     * using public keys fetched from a JSON Web Key Set (JWKS).
     *
     * @param jwksClient the JwksClient instance
     */
    public TokenSignatureVerifier(JwksClient jwksClient) {
        this.jwksClient = jwksClient;
    }

    /**
     * Verifies the cryptographic signature of a SignedJWT using the RSASSA-PKCS1-v1_5 signature algorithm.
     * The method retrieves the appropriate RSA public key from the specified platform's JSON Web Key Set (JWKS)
     * to perform the signature verification.
     *
     * @param jwt the signed JSON Web Token (JWT) that needs to be verified
     * @param platformInfo the platform information containing the URL to fetch the JWKS and other related data
     * @return true if the signature is valid, false otherwise
     * @throws TokenSignatureVerifierException if there is an error in retrieving or processing the JWKS
     * @throws ParseException if the JWT parsing fails
     * @throws JOSEException if the signature verification process encounters an error
     */
    public boolean verifySignature(SignedJWT jwt, PlatformInfo platformInfo) throws TokenSignatureVerifierException, ParseException, JOSEException {
        try {
            RSAKey rsa = (RSAKey) selectJwk(jwt, platformInfo);
            RSAPublicKey pub = rsa.toRSAPublicKey();
            return jwt.verify(new RSASSAVerifier(pub));
        } catch (JwksClientException e) {
            throw new TokenSignatureVerifierException();
        }
    }

    private JWK selectJwk(SignedJWT jwt, PlatformInfo platformInfo) throws ParseException {
        JWKSet jwkSet = jwksClient.fetch(platformInfo.jwksUrl());
        String kid = jwt.getHeader().getKeyID();
        JWK jwk = (kid != null) ? jwkSet.getKeyByKeyId(kid) : null;
        if (jwk == null && !jwkSet.getKeys().isEmpty()) {
            jwk = jwkSet.getKeys().get(0);
        }
        return jwk;
    }

}
