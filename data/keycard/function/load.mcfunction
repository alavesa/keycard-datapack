# Scratch values: #req = a reader's required level, #ok = was access granted
scoreboard objectives add kc.var dummy
# Countdown timer for the redstone "button" pulse on each reader
scoreboard objectives add kc.timer dummy

tellraw @a {"text":"[Keycards v0.1] Loaded. /function keycard:give/level_1 ... ,  /function keycard:place/level_1 ...","color":"aqua"}
