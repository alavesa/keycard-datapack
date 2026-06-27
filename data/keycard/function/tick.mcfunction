# 1) Any reader that was just right-clicked? Run its logic (positioned AT the reader).
execute as @e[type=interaction,tag=kc.reader] at @s if data entity @s interaction run function keycard:reader_click

# 2) Redstone "button" pulse: count down, and when a pulse expires turn the redstone back to air.
scoreboard players remove @e[type=marker,tag=kc.pulse] kc.timer 1
execute as @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}] at @s if block ~ ~ ~ minecraft:redstone_block run setblock ~ ~ ~ air
kill @e[type=marker,tag=kc.pulse,scores={kc.timer=..0}]
