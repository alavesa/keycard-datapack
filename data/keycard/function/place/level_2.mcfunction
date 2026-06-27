# Stand in the AIR block next to the (iron) door where you want the reader, then run this.
summon minecraft:item_display ~ ~1 ~ {Tags:["kc.reader","kc.reader_model"],item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":{strings:["keycard_reader"]}}},billboard:"fixed"}
summon minecraft:interaction ~ ~1 ~ {Tags:["kc.reader","kc.req2"],width:0.7f,height:0.7f,response:1b}
tellraw @s {"text":"[Keycards] Placed a Level 2 reader. Step away; right-click it with a keycard.","color":"aqua"}
