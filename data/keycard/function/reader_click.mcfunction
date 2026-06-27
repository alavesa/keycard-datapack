# Runs AS a reader interaction entity, positioned AT it, after a right-click. Act once.
data remove entity @s interaction

# Required level -> #req
scoreboard players set #req kc.var 0
execute if entity @s[tag=kc.req1] run scoreboard players set #req kc.var 1
execute if entity @s[tag=kc.req2] run scoreboard players set #req kc.var 2
execute if entity @s[tag=kc.req3] run scoreboard players set #req kc.var 3
execute if entity @s[tag=kc.req4] run scoreboard players set #req kc.var 4
execute if entity @s[tag=kc.req5] run scoreboard players set #req kc.var 5

# Facing -> #dir (so grant/tick can press/release the right-facing button)
scoreboard players set #dir kc.var 1
execute if entity @s[tag=kc.dir2] run scoreboard players set #dir kc.var 2
execute if entity @s[tag=kc.dir3] run scoreboard players set #dir kc.var 3
execute if entity @s[tag=kc.dir4] run scoreboard players set #dir kc.var 4

# Switch to the nearest player (the clicker), still positioned at the reader.
execute as @p[distance=..4] run function keycard:check
