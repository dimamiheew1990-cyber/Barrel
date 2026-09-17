# Barrel Distortion

Client-only NeoForge mod for Minecraft **1.21.1**. It applies a lightweight barrel-distortion pass to the completed 3D level frame, tuned by default for an FOV of 110 degrees.

## Install

Copy `build/libs/barrel_distortion-1.21.1-1.0.0.jar` to the instance's `mods` folder. NeoForge 21.1.x is required; the mod is safe to omit on servers.

## Configuration

The first client launch writes `config/barrel_distortion-client.toml`.

* `enabled` — turns the pass on or off without restarting.
* `strength` — radial distortion strength (`0.085` by default; allowed range `0.0`–`0.25`).
* `fovReference` — the FOV the default strength is designed around (`110`).

The effect is intentionally run after the world, translucent objects, particles, and hand are rendered, but before Minecraft's GUI. This keeps menus and HUD text sharp and undistorted.

## Build

Use JDK 21 and Gradle 8.14.4 or newer. The repository deliberately contains
only text source files, so generate the standard Gradle wrapper locally before
the first build:

```bash
gradle wrapper --gradle-version 8.14.4 --distribution-type bin
./gradlew build
```

The mod jar is written to `build/libs/`; the reproducible source archive is written to `build/distributions/`.
