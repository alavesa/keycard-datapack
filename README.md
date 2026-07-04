# Keycards — datapack + Paper plugin

[![Reviewed by PatchPilots](https://img.shields.io/badge/Reviewed%20by-PatchPilots-8A2BE2)](https://github.com/alavesa/patchpilots)

Keycards and keycard readers for Minecraft — right-click a reader with a keycard of high
enough clearance and the door opens. Built for facility / SCP-style servers.

> **v0.8** — the project is now a **datapack + Paper plugin pair**. The datapack handles
> placement, models and cards; the plugin handles everything that happens on a read: it opens
> the iron door **directly, with no block swaps of any kind** (no redstone block, no hidden
> button — nothing appears anywhere), plays configurable sounds, runs the swipe animation with
> the **real card leaving your hand**, and adds a `/keycards` card menu.

## Install

1. **Datapack** (any server or world): drop the `Keycards` folder into `<world>/datapacks/`
   and `/reload`.
2. **Plugin** (Paper/Spigot servers): drop `Keycards-x.y.z.jar` into the server's `plugins/`
   folder and restart. **The plugin is required for readers to do anything** — without it,
   clicking a reader is silent.

## Keycards

Six clearance levels, each on its own base item (so your existing 3D models map per item):

| Level | Base item | model id |
|------:|-----------|----------|
| 1 | `creeper_banner_pattern` | `keycard_1` |
| 2 | `skull_banner_pattern`   | `keycard_2` |
| 3 | `mojang_banner_pattern`  | `keycard_3` |
| 4 | `globe_banner_pattern`   | `keycard_4` |
| 5 | `piglin_banner_pattern`  | `keycard_5` |
| Omni | `flow_banner_pattern`  | `keycard_omni` |

Get one from the **`/keycards` menu** (click a card to receive it), or with
`/function keycard:give/level_1` … `level_5` / `omni`. Cards from either source are
identical — both are recognized by their `custom_model_data` string id.

A keycard opens any reader whose required level is **≤** its own (Level 3 opens readers 1–3;
Omni opens everything).

## Readers

1. **Face the wall next to the door** (within ~8 blocks) and run
   `/function keycard:place/level_1` … `level_5` (the required clearance). The reader mounts
   on the wall, facing you — a 3D model + clickable hitbox, nothing else.
2. Right-click the reader with a keycard:
   - **Granted** → your card visibly leaves your hand and swipes down the reader, the iron
     door(s) next to the reader or its wall swing open for ~1.5s and close again — via block
     state, so **no block is ever placed, swapped or restored**. Green actionbar + chime.
   - **Denied** → red actionbar + buzz.
   - The reader then ignores clicks for 1.5s (anti-spam cooldown).

The plugin finds **iron doors touching the reader block or the wall block behind it** (both
door halves are checked, double doors both open). No redstone wiring is needed for doors.

Misplaced a reader? Stand near it and run `/function keycard:remove_readers` (clears readers
within 5 blocks).

## Plugin configuration (`plugins/Keycards/config.yml`)

- `sounds.grant / deny / swipe` — namespaced sound keys with per-sound pitch. Point these at
  your resource pack's custom sounds (e.g. `keycards:reader.grant` from your pack's
  `sounds.json`) for fully custom reader audio.
- `door-open-ticks` — how long a granted read holds the door open (default 30 = 1.5s).
- `cooldown-ms` — reader anti-spam cooldown (default 1500).

## Card-swipe animation (real card, v0.8)

On a granted read the **actual card item is removed from your hand**, shown sliding down the
reader face (~0.4s), and returned to the same hotbar slot afterwards. Edge cases are covered —
the card is returned even if you log out, die, or the server reloads mid-swipe (on death it
joins your drops). This realism was deliberately NOT done in the datapack era: only a plugin
can guarantee the card is never lost.

## Resource pack (your 3D models)

Items/displays carry `custom_model_data` **string** ids (1.21.4+ model system):

- Keycards: `keycard_1` … `keycard_5`, `keycard_omni` (on each banner-pattern base item).
- Reader: an `item_display` holding `minecraft:paper` with string `keycard_reader`.

Point your resource pack's item-model definitions at those ids. If a model doesn't show,
that's the hook to check — the logic doesn't depend on the models.

## Building the plugin

```
cd paper-plugin && mvn package    # requires JDK 21; jar lands in target/
```

## Notes / known rough edges

- Reader model orientation uses a basic Y-rotation per facing; the exact offset depends on how
  your model is authored — expect to tweak the quaternions in `place/do_place.mcfunction` and
  the plugin's `SwipeAnimation.transform`.
- The reader needs an **air block with a solid wall behind it**.
- Readers placed by older versions keep working (same tags). Any leftover hidden stone button
  from ≤v0.7 placements can simply be broken — v0.8 never places buttons.
