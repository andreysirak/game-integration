package com.example.platform.service;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Generates an RSA keypair on startup and exposes the RSAKey + JWKSet for JWKS endpoint.
 */
@ApplicationScoped
public class KeyManager {

    private RSAKey rsaJwk;

    @PostConstruct
    void init() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair kp = gen.generateKeyPair();
        RSAPublicKey pub = (RSAPublicKey) kp.getPublic();
        RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();

        String kid = UUID.randomUUID().toString();
        rsaJwk = new RSAKey.Builder(pub)
                .privateKey(priv)
                .keyID(kid)
                .build();
    }

    public RSAKey getRsaJwk() {
        return rsaJwk;
    }

    public JWKSet getJwkSet() {
        return new JWKSet(rsaJwk.toPublicJWK());
    }
}