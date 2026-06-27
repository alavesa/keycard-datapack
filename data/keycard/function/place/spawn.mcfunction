# Run AS the player. Raycasts from the eyes to the wall in front and mounts the reader on it.
# Required level was put in #reqp by place/level_N.
scoreboard players set #placed kc.var 0
scoreboard players set #steps kc.var 24
execute at @s anchored eyes positioned ^ ^ ^0 run function keycard:place/ray
execute if score #placed kc.var matches 0 run tellraw @s {"text":"[Keycards] No wall found ahead (within ~8 blocks). Stand facing the wall (next to the door) and try again.","color":"red"}
