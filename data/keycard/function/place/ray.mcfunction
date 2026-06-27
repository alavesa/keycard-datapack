# Recursive raycast along the player's look direction (executor stays the player).
scoreboard players remove #steps kc.var 1
execute if score #steps kc.var matches ..0 run return 0
# Stop if we somehow entered a solid block.
execute unless block ~ ~ ~ #keycard:passable run return 0
# If the block just ahead is a wall, mount the reader HERE (the last air before it) and stop.
execute unless block ^ ^ ^0.4 #keycard:passable run function keycard:place/do_place
execute unless block ^ ^ ^0.4 #keycard:passable run return 0
# Otherwise keep stepping forward.
execute positioned ^ ^ ^0.4 run function keycard:place/ray
