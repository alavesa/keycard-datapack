# Runs AS the player who clicked, positioned AT the reader. #req = required level.
# Access is granted if the main-hand keycard's level >= #req (Omni = 99 opens everything).
scoreboard players set #ok kc.var 0
execute if items entity @s weapon.mainhand *[minecraft:custom_data~{keycard:{lvl:99}}] run scoreboard players set #ok kc.var 1
execute if score #req kc.var matches ..5 if items entity @s weapon.mainhand *[minecraft:custom_data~{keycard:{lvl:5}}] run scoreboard players set #ok kc.var 1
execute if score #req kc.var matches ..4 if items entity @s weapon.mainhand *[minecraft:custom_data~{keycard:{lvl:4}}] run scoreboard players set #ok kc.var 1
execute if score #req kc.var matches ..3 if items entity @s weapon.mainhand *[minecraft:custom_data~{keycard:{lvl:3}}] run scoreboard players set #ok kc.var 1
execute if score #req kc.var matches ..2 if items entity @s weapon.mainhand *[minecraft:custom_data~{keycard:{lvl:2}}] run scoreboard players set #ok kc.var 1
execute if score #req kc.var matches ..1 if items entity @s weapon.mainhand *[minecraft:custom_data~{keycard:{lvl:1}}] run scoreboard players set #ok kc.var 1

execute if score #ok kc.var matches 1 run function keycard:grant
execute if score #ok kc.var matches 0 run function keycard:deny
