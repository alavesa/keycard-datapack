# Scratch: #req = a reader's required level, #ok = granted?, #dir/#yaw = facing helpers
scoreboard objectives add kc.var dummy
# Stores a pulse marker's button facing so it can be un-pressed later
scoreboard objectives add kc.var2 dummy
# Countdown timer for the button pulse
scoreboard objectives add kc.timer dummy

tellraw @a {"text":"[Keycards v0.2] Loaded. /function keycard:give/level_1 ... ,  /function keycard:place/level_1 ...","color":"aqua"}
