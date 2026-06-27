# Macro args: req, dir (1-4), q (rotation quaternion), bf (button facing). Runs AT the snapped block.
# Reader model (rotated to face the player):
$summon minecraft:item_display ~ ~ ~ {Tags:["kc.reader","kc.reader_model"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]}}},transformation:{left_rotation:[$(q)],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},billboard:"fixed"}
# Clickable hitbox (tagged with its required level + facing so the logic knows both):
$summon minecraft:interaction ~ ~ ~ {Tags:["kc.reader","kc.req$(req)","kc.dir$(dir)"],width:0.8f,height:0.8f,response:1b}
# Hidden wall button = the redstone output (only into air, so it can't destroy your build):
$setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=$(bf),powered=false] keep
