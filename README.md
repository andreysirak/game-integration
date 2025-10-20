# Game Integration POC (multi-module)

## Repository Layout
Multi-module Maven structure:
- pom.xml (parent POM)
    - game-platform/  (Quarkus app — Game Platform/Identity Provider)
    - game-provider/  (Quarkus app — Game Provider)

## Local Setup
How to run and test locally (from repo root).
Start each module directly:

  - mvn -pl game-platform quarkus:dev
  - mvn -pl game-provider quarkus:dev


**NOTE:** The following test cases are described using Postman tool, but feel free to use any other tool you prefer. It's just a simple API :)

## Use Case 1: Authenticated Player Login Flow

1. Login to Game-Platform
   ```http
   POST http://localhost:8081/player/login
   Body: {"username":"test","password":"test"}
   ```

2. Verify Response:
    - Status: 200
    - Body: `{"username": "test", "status": "ok"}`
    - Cookie: `PLATFORM_SESS` with value is returned

3. Get Game Token
   ```http
   POST http://localhost:8081/issue
   Body: {"gameId":"game-roulette"}
   ```
   *Note: Include session cookie in request*

4. Verify Token Response:
    - Status: 200
    - Body: `{"token": "your.new_ready_to_use_shiny.token"}`

5. Access Game Provider
   ```http
   POST http://localhost:8080/game-roulette/play
   ```
   Authorization:
    - Type: Bearer Token
    - Token: *use previously received token*

6. Verify Game Access:
    - Status: 200
    - Body:
      ```json
      {
        "gameId": "game-roulette",
        "status": "ok"
      }
      ```

## Use Case 2: non-authenticated call is made against our game, access is denied.

1. Attempt Game Access Without Token
   ```http
   POST http://localhost:8080/game-roulette/play
   ```
   *No token provided*

2. Verify Unauthorized Response:
    - Status: 401
    - Body:
      ```json
      {
        "error": "missing_token",
        "message": "Authorization header missing"
      }
      ```

## Use Case 3: a game is not allowed for a specific Platform, access is denied, even though Player was
successfully authenticated against that Platform.

1. Login to Game-Platform
   ```http
   POST http://localhost:8081/player/login
   Body: {"username":"test","password":"test"}
   ```

2. Verify Login Response:
    - Status: 200
    - Body: `{"username": "test", "status": "ok"}`
    - Cookie: `PLATFORM_SESS` with value is returned

3. Request Token for Unauthorized Game
   ```http
   POST http://localhost:8081/issue
   Body: {"gameId":"this-game-is-not-allowed-for-our-game-provider-but-we-try-anyways"}
   ```
   *Include session cookie in request*

4. Verify Token Response:
    - Status: 200
    - Body: `{"token": "your.new_ready_to_use_shiny.token"}`

5. Attempt Unauthorized Game Access
   ```http
   POST http://localhost:8080/this-game-is-not-allowed-for-our-game-provider-but-we-try-anyways/play
   ```
   *Include bearer token in Authorization header*

6. Verify Forbidden Response:
    - Status: 403
    - Body:
      ```json
      {
        "error": "game_not_allowed_for_platform",
        "message": "game_not_allowed_for_platform"
      }
      ```

```