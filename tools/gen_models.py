#!/usr/bin/env python3
"""Keycard READER models for the scp_and_chemistry pack.

The cards themselves stay vanilla banner patterns (by request) - what gets a
custom look is the wall-mounted reader: an SCP:CB-style panel with a status
light colored by the required level, so a glance tells you what card a door
wants. Levels 1-5 plus the omni tier, and a plain gray model for readers
placed before levels were stamped on the display.

Textures live in textures/block/ (auto-stitched into the blocks atlas - no
atlas registration needed). The datapack writes keycard_reader_<n> onto each
reader's item display; the dispatch here rides minecraft:paper together with
the ID cards - the pack build merges the cases.

Run from the repo root:  python3 tools/gen_models.py
"""
import json, os, struct, zlib

LIGHTS = {
    "1":    (168, 168, 172),
    "2":    (92, 190, 92),
    "3":    (80, 200, 220),
    "4":    (248, 176, 40),
    "5":    (212, 62, 62),
    "omni": None,  # rainbow
}
RAINBOW = [(212, 62, 62), (248, 176, 40), (240, 224, 70),
           (92, 190, 92), (80, 200, 220), (190, 110, 230)]

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

root = os.path.join(os.path.dirname(__file__), "..", "resource-pack", "assets")
tex = os.path.join(root, "keycard", "textures", "block")

# the shared case: brushed dark metal with a beveled edge
case = [[(52, 55, 60, 255)] * 16 for _ in range(16)]
for i in range(16):
    case[0][i] = case[15][i] = (74, 78, 84, 255)
    case[i][0] = case[i][15] = (74, 78, 84, 255)
for y in range(3, 13, 3):
    for x in range(2, 14):
        case[y][x] = (46, 49, 54, 255)
png(os.path.join(tex, "reader_case.png"), case)
png(os.path.join(tex, "reader_dark.png"), [[(24, 26, 30, 255)] * 16 for _ in range(16)])

# one light texture per tier (16x16 solid glow; omni = rainbow bands)
for name, color in LIGHTS.items():
    if color:
        light = [[tuple(color) + (255,)] * 16 for _ in range(16)]
    else:
        light = [[tuple(RAINBOW[x * len(RAINBOW) // 16]) + (255,) for x in range(16)]
                 for _ in range(16)]
    png(os.path.join(tex, f"reader_light_{name}.png"), light)
png(os.path.join(tex, "reader_light_plain.png"), [[(140, 144, 150, 255)] * 16 for _ in range(16)])

# the reader: a wall panel (wall at north, face toward the player at south)
def reader_model(light):
    return {
        "textures": {
            "particle": "keycard:block/reader_case",
            "case": "keycard:block/reader_case",
            "dark": "keycard:block/reader_dark",
            "light": f"keycard:block/reader_light_{light}",
        },
        "elements": [
            {  # body
                "from": [5, 3, 0], "to": [11, 13, 2],
                "faces": {f: {"texture": "#case"} for f in
                          ("north", "south", "east", "west", "up", "down")}
            },
            {  # status light near the top of the face
                "from": [6, 10.5, 2], "to": [10, 12, 2.6],
                "shade": False,
                "faces": {f: {"texture": "#light"} for f in
                          ("south", "east", "west", "up", "down")}
            },
            {  # swipe slot
                "from": [6.5, 4.5, 2], "to": [9.5, 5.5, 2.4],
                "faces": {f: {"texture": "#dark"} for f in
                          ("south", "east", "west", "up", "down")}
            },
        ],
        "display": {"fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0],
                              "scale": [1, 1, 1]}}
    }

models = os.path.join(root, "keycard", "models", "entity")
os.makedirs(models, exist_ok=True)
cases = []
for name in list(LIGHTS) + ["plain"]:
    model_id = "keycard_reader_" + name if name != "plain" else "keycard_reader"
    with open(os.path.join(models, model_id + ".json"), "w") as f:
        json.dump(reader_model(name), f, indent=2)
    print(os.path.join(models, model_id + ".json"))
    cases.append({"when": model_id,
                  "model": {"type": "minecraft:model",
                            "model": "keycard:entity/" + model_id}})

# item DEFINITIONS for the item_model component (assets/keycard/items/):
# the datapack stamps item_model=keycard:reader_<n> onto each display, which
# resolves through these regardless of what any other pack does to paper.json
items_dir = os.path.join(root, "keycard", "items")
os.makedirs(items_dir, exist_ok=True)
for name in list(LIGHTS) + ["plain"]:
    model_id = "keycard_reader_" + name if name != "plain" else "keycard_reader"
    def_id = "reader_" + name if name != "plain" else "reader"
    with open(os.path.join(items_dir, def_id + ".json"), "w") as f:
        json.dump({"model": {"type": "minecraft:model",
                             "model": "keycard:entity/" + model_id}}, f, indent=2)
    print(os.path.join(items_dir, def_id + ".json"))

# dispatch on paper - merged with the ID card's paper.json at pack build time
dispatch_dir = os.path.join(root, "minecraft", "items")
os.makedirs(dispatch_dir, exist_ok=True)
with open(os.path.join(dispatch_dir, "paper.json"), "w") as f:
    json.dump({"model": {
        "type": "minecraft:select",
        "property": "minecraft:custom_model_data",
        "cases": cases,
        "fallback": {"type": "minecraft:model", "model": "minecraft:item/paper"}}},
        f, indent=2)
print("dispatch + models done")
