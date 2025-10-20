```markdown
# Game Platform (formerly platform-simulator) - POC

This service simulates a Platform acting as an Identity Provider (IdP).
It now includes a minimal player authentication flow (login/logout) and protected token issuance.

Port: 8081

Endpoints:
- POST /player/login
  - Body: { "username":"sertan", "password":"password" }
  - Response: 200 OK, sets cookie PLATFORM_SESS
- POST /player/logout
  - Invalidates PLATFORM_SESS
- POST /issue
  - Requires PLATFORM_SESS (cookie)
  - Body: { "gameId":"game-roulette" }
  - Response: { "token":"<RS256 JWT>" }
- GET /.well-known/jwks.json
  - Mock Trusted Authority and returns JWKS (public keys) for token verification

Test user:
- username: test
- password: test

How to run:
1. Start game-platform:
   mvn quarkus:dev -f game-platform/pom.xml
2. Use /player/login to authenticate, then call /issue 
```