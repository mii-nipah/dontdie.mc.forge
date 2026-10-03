# DontDie

A Minecraft 1.20.1 Forge mod that applies penalties after death.

## Hunger after death

On death respawn, DontDie restores the player's previous hunger level instead
of refilling the bar. `hungerLostOnDeath` in `config/dontdie-common.toml` controls
how many additional hunger points are lost:

- Default: `0` (preserve the pre-death hunger level).
- Range: `0` to `20`; two points equal one drumstick.
- Hunger cannot drop below zero. For example, 12 points before death and a
  penalty of 4 produce 8 points after respawning.

Pre-death saturation is preserved, capped at the remaining hunger level, so a
respawn does not grant free saturation. Exhaustion follows the normal respawn
behavior. Hunger restoration only runs for death clones on the server, not
End-return clones or spectator players. On a dedicated server, configure the
server's copy of `dontdie-common.toml`.

## Development

Use JDK 17. The Gradle wrapper downloads the required build dependencies.

```sh
./gradlew test build
```

Unit tests cover restoration, the configurable penalty, empty hunger,
saturation, and repeated deaths. For an in-game check, set a nonzero penalty,
reduce your hunger, die, and confirm the bar is restored minus the penalty.
Repeat with `keepInventory` enabled and with zero hunger. Return through the
End exit portal and confirm the hunger restoration does not run.
