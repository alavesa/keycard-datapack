# Runs AS the player, positioned AT the reader. Access granted.
playsound minecraft:block.note_block.pling player @a[distance=..24] ~ ~ ~ 0.7 1.7
particle minecraft:happy_villager ~ ~ ~ 0.3 0.3 0.3 0.05 8
title @s actionbar {"text":"Access granted","color":"green"}

# Press the reader's hidden wall button = a real redstone pulse, exactly like a button press.
execute if score #dir kc.var matches 1 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=south,powered=true]
execute if score #dir kc.var matches 2 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=west,powered=true]
execute if score #dir kc.var matches 3 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=north,powered=true]
execute if score #dir kc.var matches 4 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=east,powered=true]

# Release the button after ~1s. A marker remembers its facing (kc.var2) so tick can un-press it.
summon minecraft:marker ~ ~ ~ {Tags:["kc.pulse","kc.new"]}
scoreboard players operation @e[type=marker,tag=kc.new] kc.var2 = #dir kc.var
scoreboard players set @e[type=marker,tag=kc.new] kc.timer 20
tag @e[type=marker,tag=kc.new] remove kc.new
