# Runs AS a reader interaction entity: reads its facing tags into #dir.
# 1=south 2=west 3=north 4=east (the direction the reader faces, toward the player).
scoreboard players set #dir kc.var 1
execute if entity @s[tag=kc.dir2] run scoreboard players set #dir kc.var 2
execute if entity @s[tag=kc.dir3] run scoreboard players set #dir kc.var 3
execute if entity @s[tag=kc.dir4] run scoreboard players set #dir kc.var 4
