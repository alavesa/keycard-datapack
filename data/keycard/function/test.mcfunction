# Wiring test - fires the pulse of the nearest reader WITHOUT needing a keycard.
# Stand near the reader and run /function keycard:test. If the door doesn't open on the
# test pulse, the door/redstone isn't touching the reader block (see README on wiring).
scoreboard players set #found kc.var 0
execute as @e[type=interaction,tag=kc.reader,sort=nearest,limit=1,distance=..6] at @s run function keycard:test_fire
execute if score #found kc.var matches 0 run tellraw @s {"text":"[Keycards] No reader within 6 blocks.","color":"red"}
