# Runs AS a reader interaction entity, positioned AT it, after a right-click. Act once.
# NOTE: the interaction data is removed at the END - "on target" below needs it to find
# the player who actually clicked.

# Required level -> #req
scoreboard players set #req kc.var 0
execute if entity @s[tag=kc.req1] run scoreboard players set #req kc.var 1
execute if entity @s[tag=kc.req2] run scoreboard players set #req kc.var 2
execute if entity @s[tag=kc.req3] run scoreboard players set #req kc.var 3
execute if entity @s[tag=kc.req4] run scoreboard players set #req kc.var 4
execute if entity @s[tag=kc.req5] run scoreboard players set #req kc.var 5

# Facing -> #dir (so grant/tick can press/release the right-facing button)
function keycard:read_dir

# Switch to the player who ACTUALLY clicked (not just the nearest one - matters in
# multiplayer), still positioned at the reader. Then clear the click so we act once.
execute on target run function keycard:check
data remove entity @s interaction
