# Keycard models

Every keycard already carries a custom_model_data string (`keycard_1` ...
`keycard_5`, `keycard_omni`) - both the datapack give-functions and the
paper-plugin stamp it. The files here are the visual half:

- `assets/keycard/textures/item/keycard_*.png` - one 16x16 card per level
  (band color = the level's name color; omni gets a rainbow band). Repaint
  freely, keep the filenames.
- `assets/keycard/models/item/` - flat item models, one per card.
- `assets/minecraft/items/*_banner_pattern.json` - hooks each card onto its
  banner-pattern base item, with a vanilla fallback so ordinary banner
  patterns are untouched.

Defaults regenerate with `python3 tools/gen_models.py`. After any change,
rebuild the combined pack: `/Users/piia/Lab/tools/build-pack.sh` (prints the
new sha1 for server.properties).

Cards given BEFORE this pack existed have the hook already - they pick up
their model as soon as the client gets the updated scp_and_chemistry.zip.
