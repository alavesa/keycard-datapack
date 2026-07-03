# Runs AS the nearest reader interaction entity, positioned AT it. Fires a test pulse.
scoreboard players set #found kc.var 1
function keycard:read_dir
function keycard:pulse
playsound minecraft:block.note_block.pling player @a[distance=..16] ~ ~ ~ 0.7 1.7
tellraw @a[distance=..8] {"text":"[Keycards] Test pulse fired (~1s). If the door didn't open, it is not touching the reader block - move the door/redstone so it touches the block the reader sits in.","color":"aqua"}
