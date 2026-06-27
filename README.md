# Keycard Datapack

Keycards and keycard readers for Minecraft — right-click a reader with a keycard of high
enough clearance and it opens the door like a button (a short redstone pulse). Built for
facility / SCP-style servers.

> **v0.1** — works, but expect to fine-tune reader placement and the resource-pack model
> hooks. Tested logic, not yet battle-tested in a live build.

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

1. Stand in the **air block right next to the (iron) door**, where the reader should be.
2. Run `/function keycard:place/level_1` … `level_5` (the required clearance).
   - This spawns a clickable hitbox + the reader 3D model at that spot.
3. Step away and **right-click the reader with a keycard**.
   - Granted → a redstone pulse (~1.5s) opens the adjacent door, with a green message + chime.
   - Denied → red message + buzz, nothing opens.

Misplaced one? Stand near it and run `/function keycard:remove_readers` (clears readers within 5 blocks).

### How the door opens

On a granted read, the reader briefly turns its **own (air) block into a redstone block**, which
powers the adjacent door, then turns it back to air — like a button press, and it never alters
the door's own placement/state. So: put the reader in an empty block touching the iron door.

## Resource pack (your 3D models)

This datapack tags items/displays with `custom_model_data` **string** ids (1.21.4+ model system):

- Keycards: `keycard_1` … `keycard_5`, `keycard_omni` (on each banner-pattern base item).
- Reader: the model is shown by an `item_display` holding `minecraft:paper` with
  `custom_model_data` string `keycard_reader`.

Point your resource pack's item-model definitions at those ids (or, since each keycard is a
unique base item, you can override the banner-pattern item models directly). If a model doesn't
show, that's the hook to check — the datapack logic doesn't depend on the models.

## Notes / known rough edges (v0.1)

- Readers spawn at your position +1 block up; reposition by re-placing (`remove_readers` first).
- Reader model orientation is default; align it in a later pass.
- The reader's block must be **air** for the pulse to fire (so it can't damage your build).
