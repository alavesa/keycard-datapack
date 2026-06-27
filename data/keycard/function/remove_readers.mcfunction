# Removes all keycard readers (model + clickable hitbox) within 5 blocks. Run if you misplaced one.
kill @e[tag=kc.reader,distance=..5]
tellraw @s {"text":"[Keycards] Removed readers within 5 blocks.","color":"aqua"}
