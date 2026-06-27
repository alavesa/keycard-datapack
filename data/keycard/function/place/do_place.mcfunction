# Runs AS the player, positioned at the air block against the wall. Mounts the reader there,
# facing the player. #reqp = required level. dir/bf: 1=south 2=west 3=north 4=east (reader's
# facing, toward the player). The wall is in the look direction; the hidden button attaches to it.
scoreboard players set #placed kc.var 1
execute store result score #yaw kc.var run data get entity @s Rotation[0] 1
scoreboard players set #dir kc.var 1
execute if score #yaw kc.var matches -45..44 run scoreboard players set #dir kc.var 3
execute if score #yaw kc.var matches 45..134 run scoreboard players set #dir kc.var 4
execute if score #yaw kc.var matches 135..180 run scoreboard players set #dir kc.var 1
execute if score #yaw kc.var matches -180..-135 run scoreboard players set #dir kc.var 1
execute if score #yaw kc.var matches -134..-45 run scoreboard players set #dir kc.var 2

# dir 1 = faces south
execute if score #dir kc.var matches 1 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]}}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 1 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir1","kc.newint"],width:0.8f,height:0.8f,response:1b}
execute if score #dir kc.var matches 1 align xyz run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=south,powered=false] keep
# dir 2 = faces west
execute if score #dir kc.var matches 2 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]}}},transformation:{left_rotation:[0f,0.7071f,0f,0.7071f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 2 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir2","kc.newint"],width:0.8f,height:0.8f,response:1b}
execute if score #dir kc.var matches 2 align xyz run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=west,powered=false] keep
# dir 3 = faces north
execute if score #dir kc.var matches 3 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]}}},transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 3 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir3","kc.newint"],width:0.8f,height:0.8f,response:1b}
execute if score #dir kc.var matches 3 align xyz run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=north,powered=false] keep
# dir 4 = faces east
execute if score #dir kc.var matches 4 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]}}},transformation:{left_rotation:[0f,-0.7071f,0f,0.7071f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 4 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir4","kc.newint"],width:0.8f,height:0.8f,response:1b}
execute if score #dir kc.var matches 4 align xyz run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=east,powered=false] keep

# Tag the new reader with its required level (from #reqp)
execute if score #reqp kc.var matches 1 run tag @e[type=interaction,tag=kc.newint] add kc.req1
execute if score #reqp kc.var matches 2 run tag @e[type=interaction,tag=kc.newint] add kc.req2
execute if score #reqp kc.var matches 3 run tag @e[type=interaction,tag=kc.newint] add kc.req3
execute if score #reqp kc.var matches 4 run tag @e[type=interaction,tag=kc.newint] add kc.req4
execute if score #reqp kc.var matches 5 run tag @e[type=interaction,tag=kc.newint] add kc.req5
tag @e[type=interaction,tag=kc.newint] remove kc.newint

tellraw @s {"text":"[Keycards] Reader mounted on the wall in front of you. Right-click it with a keycard.","color":"aqua"}
