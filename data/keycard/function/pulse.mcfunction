# Positioned AT a reader, #dir = its facing. Presses the hidden wall button (powered=true)
# and starts/extends the ~1s release timer (tick.mcfunction un-presses it).
#
# MINECRAFT LIMITATION: a button set powered by command only updates ITS OWN neighbors -
# unlike a finger-press, the power does NOT push through the wall block. So the door (or
# redstone dust) must directly touch the READER block itself. Documented in the README.
execute if score #dir kc.var matches 1 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=south,powered=true]
execute if score #dir kc.var matches 2 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=west,powered=true]
execute if score #dir kc.var matches 3 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=north,powered=true]
execute if score #dir kc.var matches 4 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=east,powered=true]

# Reuse the pulse marker already at this reader (rapid re-swipes extend the pulse - two
# stacked markers would release the button early when the first one expired).
execute unless entity @e[type=marker,tag=kc.pulse,distance=..0.5] run summon minecraft:marker ~ ~ ~ {Tags:["kc.pulse"]}
scoreboard players operation @e[type=marker,tag=kc.pulse,distance=..0.5] kc.var2 = #dir kc.var
scoreboard players set @e[type=marker,tag=kc.pulse,distance=..0.5] kc.timer 20
