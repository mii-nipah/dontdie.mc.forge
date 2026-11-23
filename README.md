# dontdie

All supported versions build from the same checkout. Use `./gradlew build` with JDKs 17 and 21 available to Gradle.

| Target | Java | Mod JAR |
| --- | --- | --- |
| Forge 1.20.1 | 17 | `forge-1.20.1/build/libs/dontdie-forge-1.20.1-<version>.jar` |
| Fabric 1.20.1 | 17 | `fabric-1.20.1/build/libs/dontdie-fabric-1.20.1-<version>+1.20.1.jar` |
| Fabric 1.21.1 | 21 | `fabric-1.21.1/build/libs/dontdie-fabric-1.21.1-<version>+1.21.1.jar` |
| NeoForge 1.21.1 | 21 | `neoforge-1.21.1/build/libs/dontdie-neoforge-1.21.1-<version>.jar` |

Use `./gradlew :fabric-1.21.1:build` to build just Fabric 1.21.1. Each target also has a `runClient` and `runServer` task, for example `./gradlew :neoforge-1.21.1:runServer`.

Death penalties and translations live in `common/`. Fabric uses `config/dontdie.json` and requires Fabric API. Forge and NeoForge use `config/dontdie-common.toml`. Optional integration is available for [First Aid New](https://modrinth.com/mod/first-aid-new) on Forge 1.20.1, Fabric 1.21.1, and NeoForge 1.21.1, and for the original [First Aid](https://modrinth.com/mod/firstaid) on Forge 1.20.1. With either mod installed, penalty hits injure random body parts, but the hits themselves never kill or knock the player out; otherwise, dontdie works the same. Fabric 1.20.1 has no First Aid integration because no First Aid New build exists for it.
