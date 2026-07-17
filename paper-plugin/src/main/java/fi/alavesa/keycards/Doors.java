package fi.alavesa.keycards;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Shared iron-door open/close plumbing. The reader path (ReaderListener) and the keypad path
 * (KeypadListener) both open the SAME way - directly via block state, no redstone, then
 * auto-close after config's door-open-ticks - so both live here and neither reinvents it.
 */
final class Doors {

    private static final BlockFace[] HORIZONTAL =
        {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    private Doors() {}

    /** Normalise any half of an iron door to its BOTTOM block, or null if it is not a door. */
    static Block bottomOf(Block block) {
        if (block == null || block.getType() != Material.IRON_DOOR) return null;
        Door door = (Door) block.getBlockData();
        return door.getHalf() == Bisected.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
    }

    /** Add the door touching {@code block} (either half) to {@code out} as its bottom block. */
    static void collectDoor(Set<Block> out, Block block) {
        Block bottom = bottomOf(block);
        if (bottom != null) out.add(bottom);
    }

    /**
     * Given a set of door bottoms, add the mirrored leaf of any double door (same facing,
     * opposite hinge, one block to the side) - the other leaf never touches the reader/keypad.
     */
    static void expandDoublesInPlace(Set<Block> doorBottoms) {
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
    }

    /** Open every given door bottom, then close them all again after door-open-ticks. */
    static void openThenClose(Plugin plugin, Set<Block> doorBottoms) {
        int openTicks = plugin.getConfig().getInt("door-open-ticks", 30);
        for (Block bottom : doorBottoms) {
            setDoorOpen(bottom, true);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> setDoorOpen(bottom, false), openTicks);
        }
    }

    /**
     * Open (or close) a single door - and its double-door partner - then auto-close. Used by the
     * keypad path, which knows exactly which door it is bound to instead of scanning by adjacency.
     */
    static void openThenClose(Plugin plugin, Block anyHalf) {
        Block bottom = bottomOf(anyHalf);
        if (bottom == null) return;
        Set<Block> doorBottoms = new LinkedHashSet<>();
        doorBottoms.add(bottom);
        expandDoublesInPlace(doorBottoms);
        openThenClose(plugin, doorBottoms);
    }

    static void setDoorOpen(Block bottom, boolean open) {
        if (bottom.getType() != Material.IRON_DOOR) return;
        Door door = (Door) bottom.getBlockData();
        if (door.isOpen() == open) return;
        door.setOpen(open);
        bottom.setBlockData(door);
        bottom.getWorld().playSound(bottom.getLocation(),
            open ? Sound.BLOCK_IRON_DOOR_OPEN : Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 1.0f);
    }
}
