```markdown
# Game Provider (POC)

- Port: 8080
- API-only: POST /{gameId}/play
  - Supports session-based flow:
    - Accepts Authorization: Bearer <RS256 id_token> (Platform-issued).
  - Validates: signature (JWKS), aud, exp, game claim, and platform->game allowlist.

Platform registration (in-memory) for testing purposes
- platform-A -> JWKS at http://localhost:8081/.well-known/jwks.json allowedGames: [game-roulette, game-slots]
- platform-B -> same JWKS URL (for POC) allowedGames: [game-slots]

How to test
1. Start game-platform (port 8081)
   mvn -pl modules/game-platform quarkus:dev
2. Start game-provider (port 8080)
   mvn -pl modules/game-provider quarkus:dev
3. Simulate Platform posting token to Game Provider to create session:
   - POST http://localhost:8081/issue
     Body: { "gameId":"game-roulette" }
