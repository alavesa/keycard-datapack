# Keycard Datapack

Keycards and keycard readers for Minecraft — right-click a reader with a keycard of high
enough clearance and it opens the door like a button (a short redstone pulse). Built for
facility / SCP-style servers.

> **v0.2** — readers now snap to the block grid, face the way you're looking, and open doors
> through a hidden **button** (a real redstone pulse). Logic is written but not yet live-tested;
> expect to fine-tune model orientation and reader position. The card-swipe animation is not in
> yet (see "Planned").

## Keycards

Six clearance levels, each on its own base item (so your existing 3D models map per item):

| Level | Base item | custom_data | model id |
|------:|-----------|-------------|----------|
| 1 | `creeper_banner_pattern` | `{keycard:{lvl:1}}` | `keycard_1` |
| 2 | `skull_banner_pattern`   | `{keycard:{lvl:2}}` | `keycard_2` |
| 3 | `mojang_banner_pattern`  | `{keycard:{lvl:3}}` | `keycard_3` |
| 4 | `globe_banner_pattern`   | `{keycard:{lvl:4}}` | `keycard_4` |
| 5 | `piglin_banner_pattern`  | `{keycard:{lvl:5}}` | `keycard_5` |
| Omni | `flow_banner_pattern`  | `{keycard:{lvl:99}}` | `keycard_omni` |

Get one with `/function keycard:give/level_1` … `level_5`, or `/function keycard:give/omni`.

A keycard opens any reader whose required level is **≤** its own (Level 3 opens readers 1–3;
Omni opens everything).

## Readers

1. Stand in the **air block right next to the (iron) door**, with a **wall behind that block**,
   and **face the direction the reader should point**.
2. Run `/function keycard:place/level_1` … `level_5` (the required clearance).
   - The reader snaps to the block grid, is rotated to face you, and gets: the 3D model, a
     clickable hitbox, and a **hidden wall button** (the redstone output).
3. Step away and **right-click the reader with a keycard**.
   - Granted → the hidden button is pressed for ~1s (a real redstone pulse) → opens the wired
     door, with a green message + chime.
   - Denied → red message + buzz, nothing fires.

Misplaced one? Stand near it and run `/function keycard:remove_readers` (clears readers within
5 blocks; you may also need to break the leftover hidden button).

### How the door opens (button, not a redstone block)

The reader contains a real, hidden `stone_button` on the wall. A granted read sets it
`powered=true` for ~1s and then back to `powered=false` — **exactly like pressing a button**, so
it works with any redstone the builder wires up (iron doors, pistons, lamps…). It never alters
the door's own state. Put the reader in an empty block touching the door (or its redstone).

## Resource pack (your 3D models)

This datapack tags items/displays with `custom_model_data` **string** ids (1.21.4+ model system):

- Keycards: `keycard_1` … `keycard_5`, `keycard_omni` (on each banner-pattern base item).
- Reader: the model is shown by an `item_display` holding `minecraft:paper` with
  `custom_model_data` string `keycard_reader`.

Point your resource pack's item-model definitions at those ids (or, since each keycard is a
unique base item, you can override the banner-pattern item models directly). If a model doesn't
show, that's the hook to check — the datapack logic doesn't depend on the models.

## Planned (not in yet)

- **Card-swipe animation** — a keycard model sliding across the reader on a successful read.
  Held back on purpose: it's cosmetic, needs your card models + live timing, and temporarily
  removing/returning the real keycard risks item dupes. Will add as a visual-only swipe (no
  inventory removal) once placement + redstone are confirmed in game.

## Notes / known rough edges (v0.2)

- Reader model orientation uses a basic Y-rotation per facing; the exact offset depends on how
  your model is authored — expect to tweak the quaternions in `place/build.mcfunction`.
- The reader/button block must be **air** with a **solid wall behind it** (so the button has
  support and can't damage your build).
- Reposition by `remove_readers` then placing again (and break any leftover hidden button).
