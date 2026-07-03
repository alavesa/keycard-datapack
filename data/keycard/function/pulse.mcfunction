# Positioned AT a reader, #dir = its facing. Emits the ~1s redstone pulse: the reader block
# becomes a REDSTONE BLOCK, which powers and updates ALL six surrounding blocks (door, dust,
# piston... anything touching the reader block reacts). tick.mcfunction restores the hidden
# button when the timer runs out.
#
# Why not a command-"pressed" button: a button state set by command only updates its own
# neighbors and never pushes power through the wall (Minecraft engine limitation) - in
# practice it failed to open doors. A real redstone block has no such quirk.
setblock ~ ~ ~ minecraft:redstone_block

# Reuse the pulse marker already at this reader (rapid re-swipes extend the pulse - two
# stacked markers would release the button early when the first one expired).
execute unless entity @e[type=marker,tag=kc.pulse,distance=..0.5] run summon minecraft:marker ~ ~ ~ {Tags:["kc.pulse"]}
scoreboard players operation @e[type=marker,tag=kc.pulse,distance=..0.5] kc.var2 = #dir kc.var
scoreboard players set @e[type=marker,tag=kc.pulse,distance=..0.5] kc.timer 20
