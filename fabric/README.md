# dontdie for fabric 1.20.1

The Fabric port is a separate build under `fabric/`. The Forge build at the repository root stays unchanged.

## build and install

With JDK 17 installed, run from the repository root:

```sh
bash ./gradlew -p fabric build
```

Install `fabric/build/libs/dontdie-fabric-0.1.0+1.20.1.jar` with Fabric Loader 0.16.14 or newer and [Fabric API for Minecraft 1.20.1](https://modrinth.com/mod/fabric-api/versions?g=1.20.1). The `-sources.jar` is for development, and the root Forge jar cannot run on Fabric.

The server applies the penalties. Clients with the Fabric mod get the translated toast; clients without it get a chat message. Client-only installation cannot apply penalties on a remote server.

## config

`config/dontdie.json` is created when the server or single-player world starts. Restart the server after editing it. This is independent of Forge's TOML config.

```json
{
  "maxHits": 13,
  "maxHitDamage": 3,
  "minEffects": 1,
  "maxEffects": 3,
  "hungerLostOnDeath": 0,
  "items": [
    "minecraft:slowness|30%|60-180s",
    "minecraft:blindness|10%|30-60s",
    "minecraft:poison|3%|5-15s"
  ]
}
```

`items` contains effect IDs, chances, and duration ranges in seconds. Empty lists disable effects, and unknown effects are logged and skipped. Hits and effect attempts are capped at 1000 per respawn; damage is nonnegative, chances range from 0–100, and duration ranges must be ordered and fit a 32-bit tick count. Invalid config is logged and defaults are used without overwriting the file.

Previous hunger and saturation are restored on death. `hungerLostOnDeath` subtracts 0–20 food points (two per drumstick), floored at zero; saturation is capped at the remaining hunger. Spectators and returning alive from the End are skipped. Direct hits are nonlethal, and queued hits are discarded after logout, another respawn, or server shutdown.

## first aid

[First Aid's 1.20.1 API](https://github.com/ichttt/FirstAid/tree/1.20.x) relies on Forge capabilities, so its body-part integration stays in the Forge build. Fabric uses vanilla health checks. The unused Forge template item and creative tab are not included.
