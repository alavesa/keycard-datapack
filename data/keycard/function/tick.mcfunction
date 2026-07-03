# 1) Any reader that was just right-clicked? Run its logic (positioned AT the reader).
execute as @e[type=interaction,tag=kc.reader] at @s if data entity @s interaction run function keycard:reader_click

# 1b) Tick down reader cooldowns (anti-spam)
scoreboard players remove @e[type=interaction,tag=kc.reader,scores={kc.cool=1..}] kc.cool 1

# 2) Expire pulses: restore the original wall block from the y=319 buffer. The marker sits
#    at the reader; kc.var2 = reader facing, so the wall is one block the opposite way.
scoreboard players remove @e[type=marker,tag=kc.pulse] kc.timer 1
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 1 positioned ~ ~ ~-1 run function keycard:pulse_end
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 2 positioned ~1 ~ ~ run function keycard:pulse_end
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 3 positioned ~ ~ ~1 run function keycard:pulse_end
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if score @s kc.var2 matches 4 positioned ~-1 ~ ~ run function keycard:pulse_end
kill @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}]

# 3) Card-swipe animation. One tick after spawn (timer hits 14, so the client already has the
#    start pose) merge in the end pose: same spot but lower, interpolated over 8 ticks. The
#    merge only replaces "translation"; rotation/scale set at spawn stay as-is. kc.var2 = facing.
scoreboard players remove @e[type=item_display,tag=kc.swipe] kc.timer 1
execute as @e[type=item_display,tag=kc.swipe,scores={kc.timer=14}] if score @s kc.var2 matches 1 run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{translation:[0.0f,-0.20f,0.31f]}}
execute as @e[type=item_display,tag=kc.swipe,scores={kc.timer=14}] if score @s kc.var2 matches 2 run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{translation:[-0.31f,-0.20f,0.0f]}}
execute as @e[type=item_display,tag=kc.swipe,scores={kc.timer=14}] if score @s kc.var2 matches 3 run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{translation:[0.0f,-0.20f,-0.31f]}}
execute as @e[type=item_display,tag=kc.swipe,scores={kc.timer=14}] if score @s kc.var2 matches 4 run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{translation:[0.31f,-0.20f,0.0f]}}
kill @e[type=item_display,tag=kc.swipe,scores={kc.timer=..0}]
