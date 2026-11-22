# dontdie

All supported versions build from the same checkout. Use `./gradlew build` with JDKs 17 and 21 available to Gradle.

| Target | Java | Mod JAR |
| --- | --- | --- |
| Forge 1.20.1 | 17 | `forge-1.20.1/build/libs/dontdie-forge-1.20.1-<version>.jar` |
| Fabric 1.20.1 | 17 | `fabric-1.20.1/build/libs/dontdie-fabric-1.20.1-<version>+1.20.1.jar` |
| Fabric 1.21.1 | 21 | `fabric-1.21.1/build/libs/dontdie-fabric-1.21.1-<version>+1.21.1.jar` |
| NeoForge 1.21.1 | 21 | `neoforge-1.21.1/build/libs/dontdie-neoforge-1.21.1-<version>.jar` |

Use `./gradlew :fabric-1.21.1:build` to build just Fabric 1.21.1. Each target also has a `runClient` and `runServer` task, for example `./gradlew :neoforge-1.21.1:runServer`.

Death penalties and translations live in `common/`. Fabric uses `config/dontdie.json` and requires Fabric API. Forge and NeoForge use `config/dontdie-common.toml`. First Aid integration is available on Forge 1.20.1.
