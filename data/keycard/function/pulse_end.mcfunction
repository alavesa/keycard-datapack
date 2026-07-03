# Positioned AT the wall block behind a reader, when its pulse expires. Restore the original
# wall block from the y=319 buffer and clear the buffer slot. Runs unconditionally so the
# wall comes back even if someone broke the redstone block mid-pulse.
clone ~ 319 ~ ~ 319 ~ ~ ~ ~
setblock ~ 319 ~ minecraft:air
