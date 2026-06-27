# Runs AS the player, positioned AT the reader. Access granted.
playsound minecraft:block.note_block.pling player @a[distance=..24] ~ ~ ~ 0.7 1.7
particle minecraft:happy_villager ~ ~ ~ 0.3 0.3 0.3 0.05 8
title @s actionbar {"text":"Access granted","color":"green"}

# Open the door like a button: pulse a redstone block here for ~1.5s, but ONLY if this spot is
# air (so we never destroy a real block). The reader should sit in an air block next to the
# (iron) door. A marker counts the pulse down and turns it back to air (see tick).
execute if block ~ ~ ~ minecraft:air run setblock ~ ~ ~ minecraft:redstone_block
execute if block ~ ~ ~ minecraft:redstone_block run summon minecraft:marker ~ ~ ~ {Tags:["kc.pulse","kc.new"]}
scoreboard players set @e[type=marker,tag=kc.new] kc.timer 30
tag @e[type=marker,tag=kc.new] remove kc.new
