# One-time repair for readers placed before v0.9.1: pulls every reader's
# interaction box against its wall and shrinks it to the panel's size.
# Run once as an op: /function keycard:fix_boxes
execute as @e[type=interaction,tag=kc.reader,tag=kc.dir1] at @s align xyz run tp @s ~0.5 ~0.1875 ~0.15
execute as @e[type=interaction,tag=kc.reader,tag=kc.dir2] at @s align xyz run tp @s ~0.85 ~0.1875 ~0.5
execute as @e[type=interaction,tag=kc.reader,tag=kc.dir3] at @s align xyz run tp @s ~0.5 ~0.1875 ~0.85
execute as @e[type=interaction,tag=kc.reader,tag=kc.dir4] at @s align xyz run tp @s ~0.15 ~0.1875 ~0.5
execute as @e[type=interaction,tag=kc.reader] run data merge entity @s {width:0.55f,height:0.65f}
execute as @e[type=item_display,tag=kc.reader_model] run data modify entity @s item.components."minecraft:item_model" set value "keycard:reader"
tellraw @s {"text":"[Keycards] Reader hitboxes pulled against their walls and resized.","color":"aqua"}
