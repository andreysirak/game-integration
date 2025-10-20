package com.example.platform.service;

import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.*;

class KeyManagerTest {

    @Test
    void testInitSetsRsaJwk() throws Exception {
        KeyManager keyManager = new KeyManager();
        keyManager.init();
        RSAKey rsaJwk = keyManager.getRsaJwk();

        assertNotNull(rsaJwk, "RSA JWK should not be null after initialization");
        assertNotNull(rsaJwk.getKeyID(), "Key ID must be generated");
        assertNotNull(rsaJwk.toPrivateKey(), "Private key must be generated");
        assertNotNull(rsaJwk.toPublicKey(), "Public key must be generated");
        assertInstanceOf(RSAPrivateKey.class, rsaJwk.toPrivateKey(), "Private key should be an instance of RSAPrivateKey");
        assertInstanceOf(RSAPublicKey.class, rsaJwk.toPublicKey(), "Public key should be an instance of RSAPublicKey");
    }
}