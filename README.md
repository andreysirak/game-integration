```markdown
# Game Integration POC (multi-module)

Repository layout (multi-module Maven):

- pom.xml (parent POM)
  - game-platform/  (Quarkus app — Identity Provider)
  - game-provider/  (Quarkus app — Game Provider)

How to run locally (from repo root)
- Start a module directly:
  - mvn -pl game-platform quarkus:dev
  - mvn -pl game-provider quarkus:dev

Notes
- Parent POM centralizes versions and plugin management.
- Each module keeps its own pom.xml and sources under ./<module-name>.
- You can build all modules with `mvn -T1C clean verify` from repo root.

This repo structure is ready for independent builds/deployments of each module (you can publish module artifacts or build container images per module and deploy them separately).
```