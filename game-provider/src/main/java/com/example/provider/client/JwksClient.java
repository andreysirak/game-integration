package com.example.provider.client;

import com.example.provider.exception.JwksClientException;
import com.example.provider.model.CachedJwks;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JwksClient is responsible for retrieving JSON Web Key Sets (JWKS) from given URLs and caching them
 * for a specified time-to-live (TTL) to minimize repeated network requests.
 * This class uses an internal concurrent cache to store the fetched JWKS and associate it
 * with the corresponding URL. The cache is automatically invalidated after the defined TTL.
 * Subsequent requests for the same URL will trigger a new HTTP fetch if the cache is expired.
 * The primary functionality is to fetch and parse JWKS from a provided URL.
 */
@ApplicationScoped
public class JwksClient {

    private static final long TTL_SECONDS = 300;
    private final Map<String, CachedJwks> cache = new ConcurrentHashMap<>();

    /**
     * Fetches the JSON Web Key Set (JWKS) from the specified URL. If a cached version of the JWKS
     * exists and has not expired, it is returned. Otherwise, a network call is made to fetch the latest
     * JWKS, which is then cached for future use.
     *
     * @param jwksUrl the URL from which the JWKS should be fetched
     * @return the JSON Web Key Set (JWKS) fetched from the given URL
     * @throws JwksClientException if there is an error during the HTTP request or response processing
     * @throws ParseException if the fetched JWKS cannot be parsed
     */
    public JWKSet fetch(String jwksUrl) throws JwksClientException, ParseException {
        CachedJwks cached = cache.get(jwksUrl);
        if (cached != null && Instant.now().isBefore(cached.fetchedAt().plusSeconds(TTL_SECONDS))) {
            return cached.jwkSet();
        } else {
            JWKSet jwkSet = JWKSet.parse(getHttpResponse(jwksUrl).body());
            cache.put(jwksUrl, new CachedJwks(jwkSet, Instant.now()));
            return jwkSet;
        }
    }

    private HttpResponse<String> getHttpResponse(String jwksUrl) {
        HttpResponse<String> resp;
        try {
             resp = HttpClient.newHttpClient().send(createHttpRequest(jwksUrl), HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
             throw new JwksClientException();
        }
        if (resp.statusCode() != 200) {
            throw new JwksClientException("Failed to fetch JWKS, status=" + resp.statusCode());
        }
        return resp;
    }

    private HttpRequest createHttpRequest(String jwksUrl) {
        return HttpRequest.newBuilder()
                .uri(URI.create(jwksUrl))
                .GET()
                .build();
    }
}