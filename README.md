# DBK (Darth Beltazar's kit)

A meteor addon for minecraft 1.21.4 (based on https://github.com/MeteorDevelopment/meteor-addon-template)

## How to use

- drag to mods folder (meteor and baritone api required; for Baritone air place see [below](#baritone-air-place))

## Modules

### AutoBottle
Automatically drinks ominous bottles for raid farming
### AutoFirework
Automatically uses fireworks to boost elytra flight
### DBKAirPlace
Places blocks in air (DBK needs because AipPlace already exist in Meteor client).
Its `baritone-air-place` setting lets Baritone place blocks in the air too, see [Baritone air place](#baritone-air-place).
### EnoughLight
Highlights mob spawn places (by light level and blocks around)
### PointsAutoWalk
Automatically goes to specified points using a baritone
### WoodMine
Mine wood using a baritone

## Baritone air place

With it, Baritone places blocks straight into the air, with nothing to place against, the same way DBKAirPlace does:
- **pathing** (`#goto`, `#path`, `#tunnel`, PointsAutoWalk, ...): bridges are built without sneaking and placing backwards, and it can step up onto a block with nothing next to it;
- **building** (`#build`, `#sel fill`, `#sel replace`, ...): floating blocks of a schematic or selection get placed from any spot within reach, where plain Baritone gets stuck.

The server has to accept air placement (same as for DBKAirPlace itself).

### Installation

This needs [DBK's Baritone fork](https://github.com/DarthBeltazar/baritone/tree/dbk-airplace) instead of Meteor's Baritone.

1. Download `baritone-unoptimized-fabric-<version>.jar` from the [fork's releases](https://github.com/DarthBeltazar/baritone/releases) and put it in the mods folder.
   Use the **unoptimized** jar: the standalone one is obfuscated, and Meteor needs Baritone's real class names.
2. Remove Meteor's Baritone jar (`baritone-meteor`) from the mods folder: both contain the same `baritone` packages.

To build it yourself instead (JDK 21), the jar ends up in `fabric/build/libs/`:
```
git clone -b dbk-airplace https://github.com/DarthBeltazar/baritone.git
cd baritone
./gradlew :fabric:build
```
A new release is published by pushing a `v<version>-dbk.<n>` tag in the fork (e.g. `v1.13.1-dbk.2`), see its `.github/workflows/dbk_release.yml`.

DBK still works with Meteor's Baritone, just without this feature: turning the setting on then prints a warning.

### Usage

1. Enable the **DBK-air-place** module.
2. In its **Baritone** group, tick **baritone-air-place**.
3. Use Baritone as usual (`#goto ~ ~ ~20`, `#sel fill stone`, ...).

Untick the setting (or turn the module off) and Baritone is back to normal right away: the path it is following gets re-planned without air placing.

Under the hood the setting toggles the fork's own `airPlace` Baritone setting, so `#airPlace true` / `#airPlace false` work too, even without DBK.
While the module is on, the checkbox wins over the command.

### Blocks it uses

- For bridges and steps, Baritone first uses `acceptableThrowawayItems` (dirt, cobblestone, netherrack, stone by default).
  If there are none, it takes any other full block from the inventory, except falling blocks (sand, gravel, concrete powder)
  and blocks with contents (chests, shulker boxes, furnaces, ...). That includes valuable ones like diamond blocks, so keep them out of the hotbar.
- For building, it uses the blocks the schematic asks for, as usual.

### Tips

- Placements are spaced by Baritone's `rightClickSpeed` setting (4 ticks by default), raise it if the server kicks you for placing too fast.
- The block only appears once the server confirms it, so with high ping Baritone waits a bit at the edge (sneaking, so it won't fall).
- Parkour jumps with a block placed mid-air are not air placed.

### Development

`./gradlew runClient` loads the fork automatically if it has been built next to this repo (`../baritone`), otherwise Meteor's Baritone.
After changing the fork, rebuild it with `./gradlew :fabric:build` there.
