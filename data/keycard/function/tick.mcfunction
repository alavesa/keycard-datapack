# 1) Any reader that was just right-clicked? Run its logic (positioned AT the reader).
execute as @e[type=interaction,tag=kc.reader] at @s if data entity @s interaction run function keycard:reader_click

# 2) Release expired button pulses (un-press the hidden wall button, facing from kc.var2).
scoreboard players remove @e[type=marker,tag=kc.pulse] kc.timer 1
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 1 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=south,powered=false]
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 2 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=west,powered=false]
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 3 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=north,powered=false]
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 4 run setblock ~ ~ ~ minecraft:stone_button[face=wall,facing=east,powered=false]
kill @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}]
