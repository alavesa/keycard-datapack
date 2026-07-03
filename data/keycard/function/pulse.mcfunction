# Positioned AT a reader, #dir = its facing. Emits the ~1s redstone pulse INSIDE THE WALL:
# the wall block the reader is mounted on becomes a hidden redstone block (the original wall
# block is buffered at y=319 in the same column and restored by tick.mcfunction). Nothing
# appears in the air, and the power reaches everything touching the WALL block - including
# redstone on the far side of the wall.
#
# dir = the way the reader faces (toward the player); the wall is one block the OPPOSITE way.
execute if score #dir kc.var matches 1 positioned ~ ~ ~-1 run function keycard:pulse_wall
execute if score #dir kc.var matches 2 positioned ~1 ~ ~ run function keycard:pulse_wall
execute if score #dir kc.var matches 3 positioned ~ ~ ~1 run function keycard:pulse_wall
execute if score #dir kc.var matches 4 positioned ~-1 ~ ~ run function keycard:pulse_wall

# Reuse the pulse marker already at this reader (rapid re-fires extend the pulse; the wall
# is only buffered once thanks to the redstone_block guard in pulse_wall).
execute unless entity @e[type=marker,tag=kc.pulse,distance=..0.5] run summon minecraft:marker ~ ~ ~ {Tags:["kc.pulse"]}
scoreboard players operation @e[type=marker,tag=kc.pulse,distance=..0.5] kc.var2 = #dir kc.var
scoreboard players set @e[type=marker,tag=kc.pulse,distance=..0.5] kc.timer 20
