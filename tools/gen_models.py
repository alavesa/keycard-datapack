#!/usr/bin/env python3
"""Keycard models + textures for the scp_and_chemistry pack.

The datapack's give-functions and the paper-plugin already stamp every card
with a custom_model_data string (keycard_1 ... keycard_5, keycard_omni);
this script provides the other half: one 16x16 card texture per level, the
item models, and the dispatch files that hook them onto the banner-pattern
base items (with vanilla fallbacks, so ordinary banner patterns stay
untouched).

Run from the repo root:  python3 tools/gen_models.py
"""
import json, os, struct, zlib

# level -> (band color, base item)  - colors follow the item_name colors
CARDS = {
    "keycard_1":    ((168, 168, 172), "creeper_banner_pattern"),
    "keycard_2":    ((92, 190, 92),   "skull_banner_pattern"),
    "keycard_3":    ((80, 200, 220),  "mojang_banner_pattern"),
    "keycard_4":    ((248, 176, 40),  "globe_banner_pattern"),
    "keycard_5":    ((212, 62, 62),   "piglin_banner_pattern"),
    "keycard_omni": (None,            "flow_banner_pattern"),  # rainbow band
}
RAINBOW = [(212, 62, 62), (248, 176, 40), (240, 224, 70),
           (92, 190, 92), (80, 200, 220), (190, 110, 230)]
BODY = (232, 233, 238, 255)
EDGE = (60, 62, 70, 255)
CHIP = (208, 172, 60, 255)

def png(path, px):
    h, w = len(px), len(px[0])
    rows = b"".join(b"\x00" + b"".join(bytes(p) for p in line) for line in px)
    def chunk(tag, data):
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data))
    data = (b"\x89PNG\r\n\x1a\n"
            + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
            + chunk(b"IDAT", zlib.compress(rows, 9)) + chunk(b"IEND", b""))
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(data)
    print(path)

def card(band, level):
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for y in range(3, 13):                      # card body, portrait-ish
        for x in range(2, 14):
            px[y][x] = BODY
    for x in range(2, 14):                      # outline
        px[3][x] = px[12][x] = EDGE
    for y in range(3, 13):
        px[y][2] = px[y][13] = EDGE
    px[3][2] = px[3][13] = px[12][2] = px[12][13] = (0, 0, 0, 0)  # rounded corners
    for y in (4, 5):                            # the colored band
        for x in range(3, 13):
            color = band if band else RAINBOW[(x - 3) * len(RAINBOW) // 10]
            px[y][x] = tuple(color) + (255,)
    for y in (7, 8):                            # chip
        for x in (4, 5):
            px[y][x] = CHIP
    pips = 6 if level == 99 else level          # level pips along the bottom
    color = tuple(band) + (255,) if band else (90, 90, 100, 255)
    for i in range(min(pips, 6)):
        px[10][4 + i * (1 if pips > 4 else 2)] = color
    return px

root = os.path.join(os.path.dirname(__file__), "..", "resource-pack", "assets")
LEVELS = {"keycard_1": 1, "keycard_2": 2, "keycard_3": 3,
          "keycard_4": 4, "keycard_5": 5, "keycard_omni": 99}

for name, (band, base) in CARDS.items():
    png(os.path.join(root, "keycard", "textures", "item", name + ".png"),
        card(band, LEVELS[name]))
    model_dir = os.path.join(root, "keycard", "models", "item")
    os.makedirs(model_dir, exist_ok=True)
    with open(os.path.join(model_dir, name + ".json"), "w") as f:
        json.dump({"parent": "minecraft:item/generated",
                   "textures": {"layer0": "keycard:item/" + name}}, f, indent=2)
    dispatch_dir = os.path.join(root, "minecraft", "items")
    os.makedirs(dispatch_dir, exist_ok=True)
    with open(os.path.join(dispatch_dir, base + ".json"), "w") as f:
        json.dump({"model": {
            "type": "minecraft:select",
            "property": "minecraft:custom_model_data",
            "cases": [{"when": name,
                       "model": {"type": "minecraft:model",
                                 "model": "keycard:item/" + name}}],
            "fallback": {"type": "minecraft:model",
                         "model": "minecraft:item/" + base}}}, f, indent=2)
print("models + dispatches done")
