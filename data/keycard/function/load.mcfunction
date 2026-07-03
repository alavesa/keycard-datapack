# Scratch: #req = a reader's required level, #ok = granted?, #dir/#yaw = facing helpers
scoreboard objectives add kc.var dummy
# Stores a pulse marker's button facing so it can be un-pressed later
scoreboard objectives add kc.var2 dummy
# Countdown timer for the button pulse
scoreboard objectives add kc.timer dummy

tellraw @a {"text":"[Keycards v0.5] Loaded. give/level_1..5, place/level_1..5, test (fires the nearest reader without a card)","color":"aqua"}
