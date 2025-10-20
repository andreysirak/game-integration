# Game Provider (POC)

## Configuration
- Port: 8080
- API-only: POST `/{gameId}/play`
    - Accepts `Authorization: Bearer <RS256 id_token>` (Platform-issued)
    - Validates: signature (JWKS), aud, exp, game claim, and platform->game allowlist

## Platform Registration
*(in-memory for testing purposes)*

| Platform | JWKS URL | Allowed Games |
|----------|----------|---------------|
| platform-A | http://localhost:8081/.well-known/jwks.json | game-roulette, game-slots |
| platform-B | http://localhost:8081/.well-known/jwks.json | game-slots |

## Testing
- See `game-integration/README.md`
