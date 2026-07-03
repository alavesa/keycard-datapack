# Runs AS the player, positioned AT the reader. Access granted.
playsound minecraft:block.note_block.pling player @a[distance=..24] ~ ~ ~ 0.7 1.7
title @s actionbar {"text":"Access granted","color":"green"}

# Visual card-swipe: a copy of the held card slides down the reader face (inventory untouched)
function keycard:swipe

# Press the reader's hidden wall button for ~1s (see pulse.mcfunction for the wiring caveat)
function keycard:pulse
