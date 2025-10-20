# Game Platform - POC

This service simulates a Gaming Platform acting as an Identity Provider (IdP).
It now includes a minimal player authentication flow (login/logout) and protected token issuance.

## Configuration
Port: `8081`

## Endpoints

### Authentication
- **POST** `/player/login`
    - Body: `{ "username":"username", "password":"password" }`
    - Response: `200 OK`, sets cookie `PLATFORM_SESS`
- **POST** `/player/logout`
    - Invalidates `PLATFORM_SESS`

### Token Management
- **POST** `/issue`
    - Requires `PLATFORM_SESS` (cookie)
    - Body: `{ "gameId":"game-roulette" }`
    - Response: `{ "token":"<RS256 JWT>" }`

### Public Keys
- **GET** `/.well-known/jwks.json`
    - Mock Trusted Authority and returns JWKS (public keys) for token verification

## Test Credentials
- **Username**: `test`
- **Password**: `test`

## Documentation
For running and testing instructions, see:
- `game-integration/README.md`
