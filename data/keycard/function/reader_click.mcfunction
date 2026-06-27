# Runs AS a reader interaction entity, positioned AT it, after it was right-clicked.
# Consume the interaction so we only react once per click.
data remove entity @s interaction

# Read this reader's required level from its tag into #req.
scoreboard players set #req kc.var 0
execute if entity @s[tag=kc.req1] run scoreboard players set #req kc.var 1
execute if entity @s[tag=kc.req2] run scoreboard players set #req kc.var 2
execute if entity @s[tag=kc.req3] run scoreboard players set #req kc.var 3
execute if entity @s[tag=kc.req4] run scoreboard players set #req kc.var 4
execute if entity @s[tag=kc.req5] run scoreboard players set #req kc.var 5

# Switch to the nearest player (the one who clicked), still positioned at the reader.
execute as @p[distance=..4] run function keycard:check
