# Positioned AT the wall block behind a reader. Buffer the original wall block at y=319 in
# this column, then swap the wall to a redstone block. The guard makes overlapping pulses
# safe: if the wall is ALREADY a redstone block, neither buffer nor swap runs again (the
# buffer must keep holding the true original, or we would "restore" a redstone block).
execute unless block ~ ~ ~ minecraft:redstone_block run clone ~ ~ ~ ~ ~ ~ ~ 319 ~
execute unless block ~ ~ ~ minecraft:redstone_block run setblock ~ ~ ~ minecraft:redstone_block
