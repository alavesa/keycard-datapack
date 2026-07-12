# Runs AS the player, positioned at the air block against the wall. Mounts the reader there,
# facing the player. #reqp = required level. dir/bf: 1=south 2=west 3=north 4=east (reader's
# facing, toward the player). The wall is in the look direction. No button is placed - all
# reader logic (doors, sounds, swipe) is handled by the companion Keycards plugin.
scoreboard players set #placed kc.var 1
execute store result score #yaw kc.var run data get entity @s Rotation[0] 1
scoreboard players set #dir kc.var 1
execute if score #yaw kc.var matches -45..44 run scoreboard players set #dir kc.var 3
execute if score #yaw kc.var matches 45..134 run scoreboard players set #dir kc.var 4
execute if score #yaw kc.var matches 135..180 run scoreboard players set #dir kc.var 1
execute if score #yaw kc.var matches -180..-135 run scoreboard players set #dir kc.var 1
execute if score #yaw kc.var matches -134..-45 run scoreboard players set #dir kc.var 2

# dir 1 = faces south
execute if score #dir kc.var matches 1 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model","kc.newdisp"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]},"minecraft:item_model":"keycard:reader"}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 1 align xyz positioned ~0.5 ~0.1875 ~0.15 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir1","kc.newint"],width:0.55f,height:0.65f,response:1b}
# dir 2 = faces west
execute if score #dir kc.var matches 2 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model","kc.newdisp"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]},"minecraft:item_model":"keycard:reader"}},transformation:{left_rotation:[0f,0.7071f,0f,0.7071f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 2 align xyz positioned ~0.85 ~0.1875 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir2","kc.newint"],width:0.55f,height:0.65f,response:1b}
# dir 3 = faces north
execute if score #dir kc.var matches 3 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model","kc.newdisp"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]},"minecraft:item_model":"keycard:reader"}},transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 3 align xyz positioned ~0.5 ~0.1875 ~0.85 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir3","kc.newint"],width:0.55f,height:0.65f,response:1b}
# dir 4 = faces east
execute if score #dir kc.var matches 4 align xyz positioned ~0.5 ~0.5 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model","kc.newdisp"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]},"minecraft:item_model":"keycard:reader"}},transformation:{left_rotation:[0f,-0.7071f,0f,0.7071f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
execute if score #dir kc.var matches 4 align xyz positioned ~0.15 ~0.1875 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.dir4","kc.newint"],width:0.55f,height:0.65f,response:1b}

# Tag the new reader with its required level (from #reqp)
execute if score #reqp kc.var matches 1 run tag @e[type=interaction,tag=kc.newint] add kc.req1
execute if score #reqp kc.var matches 2 run tag @e[type=interaction,tag=kc.newint] add kc.req2
execute if score #reqp kc.var matches 3 run tag @e[type=interaction,tag=kc.newint] add kc.req3
execute if score #reqp kc.var matches 4 run tag @e[type=interaction,tag=kc.newint] add kc.req4
execute if score #reqp kc.var matches 5 run tag @e[type=interaction,tag=kc.newint] add kc.req5
execute if score #reqp kc.var matches 99 run tag @e[type=interaction,tag=kc.newint] add kc.req99
tag @e[type=interaction,tag=kc.newint] remove kc.newint

# The model knows its level too: the item_model component points straight
# at the per-level definition (keycard:reader_1..5 / _omni) - immune to
# resource-pack dispatch collisions/shadowing
execute if score #reqp kc.var matches 1 run data modify entity @e[type=item_display,tag=kc.newdisp,limit=1] item.components."minecraft:item_model" set value "keycard:reader_1"
execute if score #reqp kc.var matches 2 run data modify entity @e[type=item_display,tag=kc.newdisp,limit=1] item.components."minecraft:item_model" set value "keycard:reader_2"
execute if score #reqp kc.var matches 3 run data modify entity @e[type=item_display,tag=kc.newdisp,limit=1] item.components."minecraft:item_model" set value "keycard:reader_3"
execute if score #reqp kc.var matches 4 run data modify entity @e[type=item_display,tag=kc.newdisp,limit=1] item.components."minecraft:item_model" set value "keycard:reader_4"
execute if score #reqp kc.var matches 5 run data modify entity @e[type=item_display,tag=kc.newdisp,limit=1] item.components."minecraft:item_model" set value "keycard:reader_5"
execute if score #reqp kc.var matches 99 run data modify entity @e[type=item_display,tag=kc.newdisp,limit=1] item.components."minecraft:item_model" set value "keycard:reader_omni"
tag @e[type=item_display,tag=kc.newdisp] remove kc.newdisp

tellraw @s {"text":"[Keycards] Reader mounted on the wall in front of you. Right-click it with a keycard.","color":"aqua"}
