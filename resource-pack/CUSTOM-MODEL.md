# Keycard reader models

The cards themselves are vanilla banner patterns (on purpose). The READERS
are the custom part: a wall-mounted panel whose status light is colored by
the required level, so a glance tells you what card a door wants.

| model | light |
|---|---|
| `keycard_reader_1` | gray |
| `keycard_reader_2` | green |
| `keycard_reader_3` | aqua |
| `keycard_reader_4` | gold |
| `keycard_reader_5` | red |
| `keycard_reader_omni` | rainbow |
| `keycard_reader` | plain (readers placed before v0.9.0) |

- Textures: `assets/keycard/textures/block/` (reader_case, reader_dark,
  reader_light_*). Repaint freely, keep the filenames. The folder is
  auto-stitched into the blocks atlas - no atlas registration needed.
- Models: `assets/keycard/models/entity/`. The panel hangs on the wall at
  the north side of its block; the datapack rotates it per facing.
- The dispatch rides `minecraft:paper` together with the ID cards - the
  combined-pack build merges the cases automatically.

Defaults regenerate with `python3 tools/gen_models.py`. After any change:
`~/Lab/tools/build-pack.sh` (prints the sha1 for server.properties).

Readers placed before this version keep the plain gray model; re-place them
(or run the level's place function again) to get the colored light.
