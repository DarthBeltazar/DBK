# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

DBK (Darth Beltazar's kit) is a client-side [Meteor Client](https://meteorclient.com) addon for Minecraft 1.21.4 (Fabric), based on the meteor-addon-template. It requires Meteor Client and the Baritone API at runtime.

## Commands

On Windows use `gradlew.bat` (or `./gradlew` from Git Bash). The shell's default `JAVA_HOME` may point to JDK 8, which Gradle rejects; point it at a JDK 21 (e.g. `JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot" ./gradlew build`).

- Build the mod jar: `./gradlew build` → output in `build/libs/`
- Launch Minecraft with the addon (Fabric Loom dev client, runs in `run/`): `./gradlew runClient`
- Regenerate IDE run configs / sources: `./gradlew genSources`

There is no test suite and no linter; `./gradlew build` (what CI runs on push and PRs) is the only automated check. Formatting follows `.editorconfig` (4-space indent, 2 for JSON/YAML).

## Key facts

- **Mappings are official Mojang (mojmap)**, not Yarn — use Mojang class names (`mc.level`, `mc.gui`, `BossHealthOverlay`, `net.minecraft.world.level.block.Blocks`, etc.). The project was recently migrated from Yarn, so older Meteor examples online may use Yarn names that won't compile here.
- Versions (Minecraft, Fabric loader, mod version) live in `gradle.properties`. Meteor and Baritone are pulled from maven.meteordev.org as `meteor-client:<minecraft_version>-SNAPSHOT` and `baritone:<minecraft_version>-SNAPSHOT` (Meteor's Baritone build, which includes `baritone.api`), so bumping `minecraft_version` also changes both dependencies. That Baritone jar nests `nether-pathfinder` without declaring it in its pom, so it is added separately as `runtimeOnly` from babbaj's Maven; without it `runClient` crashes with `NoClassDefFoundError: dev/babbaj/pathfinder/NetherPathfinder`.
- Java 21 toolchain.

## Architecture

- `Addon.java` is the Meteor entrypoint (declared under `entrypoints.meteor` in `fabric.mod.json`). It registers the `DBK` category and every module. **New modules must be added to `Addon.onInitialize()`** and to the module list in `README.md`.
- `modules/` — each feature is a Meteor `Module` subclass using `Addon.DBK` as its category, with settings built via Meteor's `SettingGroup`/`*Setting.Builder` API and logic in `@EventHandler` methods (Orbit event bus, e.g. `TickEvent.Post`, render events).
- Baritone-driven modules (`WoodMine`, `PointsAutoWalk`) go through `BaritoneAPI.getProvider().getPrimaryBaritone()`, check it for null in `onActivate()` (erroring and toggling off if missing), and stop their Baritone process in `onDeactivate()`.
- `utils/BoxHighlightSettings` is an abstract `Module` base that supplies shared render settings (fill/edge color, shape mode) for modules that draw block boxes (`EnoughLight`, `DBKAirPlace`, `PointsAutoWalk`).
- Mixins: `mixins.dbk.json` → package `com.darthbeltazar.dbk.mixin`; register new mixins in its `"client"` array (the mod is client-only). Pattern used: a mixin implements an interface from `interfaces/` to expose private game state, with methods prefixed `dbk$` (private helpers marked `@Unique`), and a static helper in `utils/` casts the game object to that interface. Example: `RaidBossBarMixin` (on `BossHealthOverlay`) implements `IRaidCheck`; `RaidHelper.isRaidActive()` is what modules like `AutoBottle` call. It matches the `event.minecraft.raid*` translation key, falling back to "raid"/"рейд" in plain-text names.
