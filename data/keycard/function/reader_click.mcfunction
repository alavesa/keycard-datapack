# Runs AS a reader interaction entity, positioned AT it, after a right-click. Act once.
# NOTE: the interaction data is removed at the END - "on target" below needs it to find
# the player who actually clicked.

# Cooldown: while cooling down (kc.cool > 0, set after every handled click), swallow the
# click silently. The data remove is still needed, or this would re-trigger every tick.
scoreboard players add @s kc.cool 0
execute if score @s kc.cool matches 1.. run data remove entity @s interaction
execute if score @s kc.cool matches 1.. run return 0

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

# Start the ~1.5s anti-spam cooldown (applies to granted AND denied reads)
scoreboard players set @s kc.cool 30
