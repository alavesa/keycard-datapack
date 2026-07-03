# Keycard Datapack

Keycards and keycard readers for Minecraft — right-click a reader with a keycard of high
enough clearance and it opens the door like a button (a short redstone pulse). Built for
facility / SCP-style servers.

> **v0.4** — the card-swipe animation is in: on a granted read, a copy of the card you're
> holding slides down the reader face (~0.4s) with a soft swish, then vanishes. Purely visual —
> your inventory is never touched, so there is no dupe risk. Animation offsets are untested in
> game; if the card floats off the reader surface, the numbers to tweak are the `0.31` (distance
> from the wall) and `0.30`/`-0.20` (top/bottom of the slide) in `swipe.mcfunction` and
> `tick.mcfunction`.

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

1. **Face the wall next to the door** (within ~8 blocks). The reader raycasts to that wall and
   mounts on it, facing you.
2. Run `/function keycard:place/level_1` … `level_5` (the required clearance).
   - It places the 3D model, a clickable hitbox, and a **hidden wall button** (the redstone
     output) on the wall in front of you.
   - For the door to open, the wall block the reader sits on must be **next to the iron door**
     (the button powers that wall block, like any wall button).
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

## Card-swipe animation (v0.4)

On a **granted** read the reader shows a swipe: an `item_display` copy of the exact card in
your main hand (same model, via `SelectedItem`) appears at the top of the reader face and
slides down it over 8 ticks, holds briefly, and despawns (~0.8s total). Denied reads show no
swipe. The animation is visual-only — the real keycard never leaves your inventory.

## Notes / known rough edges

- Reader model orientation uses a basic Y-rotation per facing; the exact offset depends on how
  your model is authored — expect to tweak the quaternions in `place/build.mcfunction`.
- The reader/button block must be **air** with a **solid wall behind it** (so the button has
  support and can't damage your build).
- Reposition by `remove_readers` then placing again (and break any leftover hidden button).
