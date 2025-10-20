package com.example.provider.model;

import com.nimbusds.jose.jwk.JWKSet;

import java.time.Instant;

/**
 * CachedJwks is a record that encapsulates a JSON Web Key Set (JWKS) along with the timestamp
 * at which it was retrieved. This record is primarily utilized for caching purposes in the context
 * of JWKS management.
 * The caching strategy enables efficient reuse of previously fetched JWKS, reducing the need for
 * repeated network calls while ensuring token validation remains performant within a specific time frame.
 * Components like the JwksClient use this record to store and manage JWKS associated with specific
 * URLs, ensuring that cached entries are checked for freshness before being utilized for cryptographic
 * operations such as signature validation.
 *
 * @param jwkSet   The JWKS object that contains cryptographic keys used for token validation.
 * @param fetchedAt The timestamp indicating when the JWKS was last retrieved.
 */
public record CachedJwks(JWKSet jwkSet, Instant fetchedAt) {
}