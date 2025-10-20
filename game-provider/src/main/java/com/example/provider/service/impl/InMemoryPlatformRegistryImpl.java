package com.example.provider.service.impl;

import com.example.provider.model.PlatformInfo;
import com.example.provider.service.PlatformRegistry;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

/**
 * Injectable in-memory registry for POC.
 * Two platforms registered for local testing.
 */
@ApplicationScoped
public class InMemoryPlatformRegistryImpl implements PlatformRegistry {

    private static final Map<String, PlatformInfo> registry = new HashMap<>();

    // populate statically (simple and predictable for the test task)
    static {
        registry.put("platform-A", new PlatformInfo(
                "platform-A",
                "http://localhost:8081/.well-known/jwks.json",
                new HashSet<>(Arrays.asList("game-roulette", "game-slots"))
        ));
        registry.put("platform-B", new PlatformInfo(
                "platform-B",
                "http://localhost:8081/.well-known/jwks.json",
                new HashSet<>(Collections.singletonList("game-slots"))
        ));
    }

    public InMemoryPlatformRegistryImpl() {
    }

    /**
     * Lookup a registered platform by id.
     */
    @Override
    public PlatformInfo getPlatformInfo(String platformId) {
        return registry.get(platformId);
    }
}