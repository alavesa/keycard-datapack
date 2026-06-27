# Runs AS the player, positioned AT the reader. Access denied (wrong/no keycard).
playsound minecraft:block.note_block.bass player @a[distance=..24] ~ ~ ~ 0.7 0.6
particle minecraft:angry_villager ~ ~ ~ 0.3 0.3 0.3 0.05 8
title @s actionbar {"text":"Access denied","color":"red"}
