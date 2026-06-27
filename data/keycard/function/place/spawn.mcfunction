# Macro arg: req (required level). Run AS the player.
# Snaps to the block grid, faces the player's cardinal direction, and builds the reader there.
execute store result score #yaw kc.var run data get entity @s Rotation[0] 1
# 1=south 2=west 3=north 4=east
scoreboard players set #dir kc.var 1
execute if score #yaw kc.var matches 45..134 run scoreboard players set #dir kc.var 2
execute if score #yaw kc.var matches 135..180 run scoreboard players set #dir kc.var 3
execute if score #yaw kc.var matches -180..-135 run scoreboard players set #dir kc.var 3
execute if score #yaw kc.var matches -134..-45 run scoreboard players set #dir kc.var 4
$execute if score #dir kc.var matches 1 align xyz positioned ~0.5 ~1 ~0.5 run function keycard:place/build {req:"$(req)",dir:"1",q:"0f,0f,0f,1f",bf:"south"}
$execute if score #dir kc.var matches 2 align xyz positioned ~0.5 ~1 ~0.5 run function keycard:place/build {req:"$(req)",dir:"2",q:"0f,0.7071f,0f,0.7071f",bf:"west"}
$execute if score #dir kc.var matches 3 align xyz positioned ~0.5 ~1 ~0.5 run function keycard:place/build {req:"$(req)",dir:"3",q:"0f,1f,0f,0f",bf:"north"}
$execute if score #dir kc.var matches 4 align xyz positioned ~0.5 ~1 ~0.5 run function keycard:place/build {req:"$(req)",dir:"4",q:"0f,-0.7071f,0f,0.7071f",bf:"east"}
tellraw @s {"text":"[Keycards] Reader placed (snapped to grid, facing you). Step away and right-click it with a keycard.","color":"aqua"}
