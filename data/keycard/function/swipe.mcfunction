# Runs AS the player, positioned at the reader (block center), on a GRANTED read.
# Visual-only card swipe: a copy of the held keycard appears at the top of the reader face
# and slides down it (tick.mcfunction drives the interpolation), then vanishes.
# The player's inventory is never touched - no removal, no dupe risk.

# Spawn the card just in front of the reader face (0.31 out toward the player), rotated to
# match the reader. dir: 1=south 2=west 3=north 4=east (same as the reader model).
execute if score #dir kc.var matches 1 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.swipe","kc.newswipe"],item:{id:"minecraft:paper",count:1},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.30f,0.31f],scale:[0.4f,0.4f,0.4f]},billboard:"fixed"}
execute if score #dir kc.var matches 2 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.swipe","kc.newswipe"],item:{id:"minecraft:paper",count:1},transformation:{left_rotation:[0f,0.7071f,0f,0.7071f],right_rotation:[0f,0f,0f,1f],translation:[-0.31f,0.30f,0f],scale:[0.4f,0.4f,0.4f]},billboard:"fixed"}
execute if score #dir kc.var matches 3 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.swipe","kc.newswipe"],item:{id:"minecraft:paper",count:1},transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.30f,-0.31f],scale:[0.4f,0.4f,0.4f]},billboard:"fixed"}
execute if score #dir kc.var matches 4 run summon minecraft:item_display ~ ~ ~ {Tags:["kc.swipe","kc.newswipe"],item:{id:"minecraft:paper",count:1},transformation:{left_rotation:[0f,-0.7071f,0f,0.7071f],right_rotation:[0f,0f,0f,1f],translation:[0.31f,0.30f,0f],scale:[0.4f,0.4f,0.4f]},billboard:"fixed"}

# Show the EXACT card that was swiped: copy the main-hand item (with all its components,
# including custom_model_data) onto the display.
data modify entity @e[type=item_display,tag=kc.newswipe,limit=1] item set from entity @s SelectedItem

# Remember the facing (kc.var2) and start the lifetime countdown; tick.mcfunction animates
# the slide one tick after spawn (so the client has the start pose first) and kills at 0.
scoreboard players operation @e[type=item_display,tag=kc.newswipe] kc.var2 = #dir kc.var
scoreboard players set @e[type=item_display,tag=kc.newswipe] kc.timer 16
tag @e[type=item_display,tag=kc.newswipe] remove kc.newswipe

# A soft swish to sell the swipe
playsound minecraft:item.armor.equip_leather player @a[distance=..16] ~ ~ ~ 0.8 1.4
