# Keycard Datapack

Keycards and keycard readers for Minecraft — right-click a reader with a keycard of high
enough clearance and it opens the door like a button (a short redstone pulse). Built for
facility / SCP-style servers.

> **v0.7** — the redstone pulse is now **hidden inside the wall**: for ~1s the wall block the
> reader is mounted on becomes a redstone block (the original wall is buffered and restored
> automatically — furnaces/chests included). Nothing appears in mid-air anymore, and wiring is
> the natural way: **the door/redstone must touch the WALL block the reader sits on** — the
> far side of the wall works too.

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
   - **The iron door (or your redstone dust) must directly touch the WALL block the reader
     is mounted on** — mount the reader on the wall right beside the doorway. Redstone on the
     far side of that wall block works too (hidden wiring, like a real reader).
3. Step away and **right-click the reader with a keycard**.
   - Granted → the wall block behind the reader becomes a hidden redstone block for ~1s
     (original wall restored automatically) → opens the wired door, green message + chime.
   - Denied → red message + buzz, nothing fires.
   - Either way the reader then ignores clicks for **1.5s** (anti-spam cooldown).

**Test the wiring without a card:** stand near the reader and run `/function keycard:test` —
it fires the pulse of the nearest reader as if a card had been granted. If the door doesn't
open on a test pulse, the door isn't touching the reader block.

Misplaced one? Stand near it and run `/function keycard:remove_readers` (clears readers within
5 blocks; you may also need to break the leftover hidden button).

### How the door opens — and the one wiring rule

A granted read swaps the **wall block behind the reader** into a real `minecraft:redstone_block`
for ~1s. The original wall block is copied to a buffer at **y=319 in the same column** first
and cloned back when the pulse ends — any block, chests and furnaces included, comes back
exactly as it was. A redstone block powers all six blocks touching it with proper updates, so
doors, dust, pistons and lamps touching the wall block all react — including on the far side
of the wall. The door's own state is never touched.

**The rule: whatever should react (door, dust, piston…) must be directly adjacent to the wall
block the reader is mounted on.** The hidden button also remains as a manual override —
hand-pressing it works like any wall button.

Honest limitations of the wall-swap:
- If the wall's far side is visible (a 1-block wall between rooms), that block face flashes
  red for ~1s during a pulse. Cosmetic; use a 2-thick wall there if it bothers you.
- The buffer lives at y=319 in the wall's column — don't build at y=319 directly above a
  reader wall, and two readers mounted on the same x/z column can conflict if swiped in the
  same second.
- Designed for the Overworld (y=319 doesn't exist in the Nether/End).

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
