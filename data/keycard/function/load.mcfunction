# Scratch: #req = a reader's required level, #ok = granted?, #dir/#yaw = facing helpers
scoreboard objectives add kc.var dummy
# Stores a pulse marker's button facing so it can be un-pressed later
scoreboard objectives add kc.var2 dummy
# Countdown timer for the button pulse
scoreboard objectives add kc.timer dummy
# Per-reader anti-spam cooldown (ticks left until the reader accepts the next click)
scoreboard objectives add kc.cool dummy

tellraw @a {"text":"[Keycards v0.7] Loaded. give/level_1..5, place/level_1..5, test (fires the nearest reader without a card)","color":"aqua"}
