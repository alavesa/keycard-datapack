package fi.alavesa.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles reader clicks. Readers are the datapack's interaction entities (tag kc.reader,
 * required level kc.req1..5, facing kc.dir1..4) - the plugin adds no entities of its own.
 * A granted read opens the iron door(s) touching the reader or its wall DIRECTLY via block
 * state: no redstone block, no button, nothing appears or gets swapped anywhere.
 */
public final class ReaderListener implements Listener {

    private final KeycardsPlugin plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public ReaderListener(KeycardsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onReaderClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Interaction reader)) return;
        Set<String> tags = reader.getScoreboardTags();
        if (!tags.contains("kc.reader")) return;
        event.setCancelled(true);

        // Anti-spam cooldown per reader
        long now = System.currentTimeMillis();
        Long until = cooldowns.get(reader.getUniqueId());
        if (until != null && now < until) return;
        cooldowns.put(reader.getUniqueId(), now + plugin.getConfig().getLong("cooldown-ms", 1500));

        Player player = event.getPlayer();
        int required = tagNumber(tags, "kc.req", 1);
        int dir = tagNumber(tags, "kc.dir", 1);
        ItemStack hand = player.getInventory().getItemInMainHand();
        int level = Cards.levelOf(hand);

        // other plugins (Doors) get first refusal - a cancelled event means
        // the listener owned the whole interaction (e.g. "Door is locked")
        KeycardSwipeEvent swipe = new KeycardSwipeEvent(player, reader, required, level, level >= required);
        plugin.getServer().getPluginManager().callEvent(swipe);
        if (swipe.isCancelled()) return;

        if (level >= required) {
            grant(player, reader, dir, hand);
        } else {
            deny(player, reader);
        }
    }

    private void grant(Player player, Interaction reader, int dir, ItemStack card) {
        Location loc = reader.getLocation();
        Msg.actionbar(player, Component.text("Access granted", NamedTextColor.GREEN));
        playConfigSound(loc, "grant");
        playConfigSound(loc, "swipe");

        if (!SwipeAnimation.isSwiping(player)) {
            SwipeAnimation.start(plugin, player, reader, dir, card);
        }
        openDoors(reader, dir);
    }

    private void deny(Player player, Interaction reader) {
        Msg.actionbar(player, Component.text("Access denied", NamedTextColor.RED));
        playConfigSound(reader.getLocation(), "deny");
    }

    private static final BlockFace[] HORIZONTAL =
        {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    /** Open every iron door touching the reader block or the wall block behind it, then close it. */
    private void openDoors(Interaction reader, int dir) {
        Block readerBlock = reader.getLocation().getBlock();
        Block wallBlock = readerBlock.getRelative(wallFace(dir));

        Set<Block> doorBottoms = new LinkedHashSet<>();
        for (Block base : new Block[]{readerBlock, wallBlock}) {
            for (BlockFace face : HORIZONTAL) {
                // Reader sits at eye height; check this Y and up to two below (door halves)
                collectDoor(doorBottoms, base.getRelative(face));
                collectDoor(doorBottoms, base.getRelative(face).getRelative(BlockFace.DOWN));
                collectDoor(doorBottoms, base.getRelative(face).getRelative(0, -2, 0));
            }
        }

        // Double doors: the OTHER leaf sits one block further and never touches the reader
        // or its wall, so expand every found door with its mirrored pair (same facing,
        // opposite hinge) before opening.
        for (Block bottom : doorBottoms.toArray(new Block[0])) {
            Door door = (Door) bottom.getBlockData();
            for (BlockFace face : HORIZONTAL) {
                Block other = bottom.getRelative(face);
                if (other.getType() != Material.IRON_DOOR) continue;
                Door otherDoor = (Door) other.getBlockData();
                if (otherDoor.getHalf() == Bisected.Half.BOTTOM
                        && otherDoor.getFacing() == door.getFacing()
                        && otherDoor.getHinge() != door.getHinge()) {
                    doorBottoms.add(other);
                }
            }
        }

        int openTicks = plugin.getConfig().getInt("door-open-ticks", 30);
        for (Block bottom : doorBottoms) {
            setDoorOpen(bottom, true);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> setDoorOpen(bottom, false), openTicks);
        }
    }

    private void collectDoor(Set<Block> out, Block block) {
        if (block.getType() != Material.IRON_DOOR) return;
        Door door = (Door) block.getBlockData();
        out.add(door.getHalf() == Bisected.Half.TOP ? block.getRelative(BlockFace.DOWN) : block);
    }

    private void setDoorOpen(Block bottom, boolean open) {
        if (bottom.getType() != Material.IRON_DOOR) return;
        Door door = (Door) bottom.getBlockData();
        if (door.isOpen() == open) return;
        door.setOpen(open);
        bottom.setBlockData(door);
        bottom.getWorld().playSound(bottom.getLocation(),
            open ? Sound.BLOCK_IRON_DOOR_OPEN : Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 1.0f);
    }

    /** The wall is one block the OPPOSITE way from the reader's facing. */
    private BlockFace wallFace(int dir) {
        return switch (dir) {
            case 2 -> BlockFace.EAST;   // reader faces west
            case 3 -> BlockFace.SOUTH;  // reader faces north
            case 4 -> BlockFace.WEST;   // reader faces east
            default -> BlockFace.NORTH; // reader faces south
        };
    }

    /** Parses e.g. kc.req3 / kc.req99 / kc.dir2 - any number after the prefix
     *  (the old 1..5 loop silently read omni readers, kc.req99, as level 1). */
    private int tagNumber(Set<String> tags, String prefix, int fallback) {
        for (String tag : tags) {
            if (tag.startsWith(prefix)) {
                try {
                    return Integer.parseInt(tag.substring(prefix.length()));
                } catch (NumberFormatException ignored) { }
            }
        }
        return fallback;
    }

    private void playConfigSound(Location loc, String key) {
        String sound = plugin.getConfig().getString("sounds." + key, "");
        if (sound == null || sound.isEmpty()) return;
        float pitch = (float) plugin.getConfig().getDouble("sounds." + key + "-pitch", 1.0);
        loc.getWorld().playSound(loc, sound, 0.8f, pitch);
    }

    // --- Never lose a mid-swipe card ---

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SwipeAnimation.returnCard(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        ItemStack card = SwipeAnimation.takePendingForDeath(event.getEntity());
        if (card != null) event.getDrops().add(card);
    }
}
