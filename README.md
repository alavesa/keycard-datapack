# Keycard Datapack

Keycards and keycard readers for Minecraft — right-click a reader with a keycard of high
enough clearance and it opens the door like a button (a short redstone pulse). Built for
facility / SCP-style servers.

> **v0.6** — the pulse is now a real **redstone block** in the reader block for ~1s: it powers
> and updates ALL six blocks around the reader (a command-"pressed" button couldn't reliably
> power anything — engine limitation). Also new: a **1.5s anti-spam cooldown** per reader, and
> the villager particles are gone.

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
   - **The iron door (or your redstone dust) must directly touch the reader block itself** —
     the block the reader model sits in. Touching only the wall behind it is NOT enough (see
     the wiring section below for why). Easiest layout: mount the reader on the wall right
     beside the doorway, at the door's upper-half height.
3. Step away and **right-click the reader with a keycard**.
   - Granted → the reader block becomes a redstone block for ~1s (powers everything touching
     it) → opens the wired door, with a green message + chime and a red glow on the reader.
   - Denied → red message + buzz, nothing fires.
   - Either way the reader then ignores clicks for **1.5s** (anti-spam cooldown).

**Test the wiring without a card:** stand near the reader and run `/function keycard:test` —
it fires the pulse of the nearest reader as if a card had been granted. If the door doesn't
open on a test pulse, the door isn't touching the reader block.

Misplaced one? Stand near it and run `/function keycard:remove_readers` (clears readers within
5 blocks; you may also need to break the leftover hidden button).

### How the door opens — and the one wiring rule

A granted read swaps the reader block into a real `minecraft:redstone_block` for ~1s, then
restores the hidden button. A redstone block powers **all six blocks touching it** with proper
block updates — doors, dust, pistons, lamps all react, and the door's own state is never
touched.

**The rule: whatever should react (door, dust, piston…) must be directly adjacent to the
reader block.** Power still does not travel through the wall behind the reader — if you need
redstone further away, put a dust/repeater in a block touching the reader and wire on from
there. (Earlier versions "pressed" the hidden button by command; that never worked, because a
command-set button state doesn't update its neighbors the way a hand-press does. The hidden
button remains as a manual override — hand-pressing it still works like any wall button.)

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
