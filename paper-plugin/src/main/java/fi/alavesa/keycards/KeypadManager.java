package fi.alavesa.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The rehauled keypad: a PLACED wall device with a custom model (like the keycard readers)
 * instead of an invisible code baked onto a door. {@link #place} mounts an ItemDisplay (the
 * keypad model) plus an Interaction hitbox on the wall the admin faces, and stores the code in
 * {@link KeypadStore} keyed by the keypad's own block. Entering the right code opens whatever
 * iron door(s) sit NEXT TO the keypad - the same adjacency scan the readers use - so one keypad
 * can serve a door (or double door) beside it, and a keypad no longer needs to BE the door.
 */
public final class KeypadManager {

    public static final String TAG_KEYPAD = "kc.keypad";       // on both the display and the hitbox
    public static final String TAG_KEYPAD_MODEL = "kc.keypad_model";

    private static final BlockFace[] HORIZONTAL =
        {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    private final KeycardsPlugin plugin;
    private final KeypadStore store;

    public KeypadManager(KeycardsPlugin plugin, KeypadStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    /** Mount a keypad on the wall the admin is looking at, facing them, bound to {@code code}.
     *  Returns an error string, or null on success. */
    public String place(Player admin, String code) {
        RayTraceResult ray = admin.rayTraceBlocks(6);
        if (ray == null || ray.getHitBlock() == null || ray.getHitBlockFace() == null) {
            return "No wall ahead (within 6 blocks). Face the wall next to the door and try again.";
        }
        BlockFace face = ray.getHitBlockFace();
        if (face == BlockFace.UP || face == BlockFace.DOWN) {
            return "Keypads mount on a wall, not a floor or ceiling.";
        }
        Block wall = ray.getHitBlock();
        Block air = wall.getRelative(face);          // the empty block against the wall, player side
        if (air.getType().isSolid()) return "No room on that wall - the spot is blocked.";
        if (store.isKeypad(air)) return "There's already a keypad there.";

        int dir = dirForFace(face);                  // keypad faces the player (out of the wall)
        Location center = air.getLocation().add(0.5, 0.5, 0.5);

        air.getWorld().spawn(center, ItemDisplay.class, d -> {
            d.setPersistent(true);
            d.setBillboard(Display.Billboard.FIXED);
            d.setBrightness(new Display.Brightness(10, 15));
            d.setTransformation(new Transformation(
                new Vector3f(0, 0, 0), quatForDir(dir),
                new Vector3f(1f, 1f, 1f), new Quaternionf()));
            d.addScoreboardTag(TAG_KEYPAD);
            d.addScoreboardTag(TAG_KEYPAD_MODEL);
            d.setItemStack(keypadItem());
        });
        // Hitbox nudged a touch out of the wall toward the player so it's easy to click.
        Location hit = center.clone().add(face.getModX() * 0.25, face.getModY() * 0.25, face.getModZ() * 0.25);
        air.getWorld().spawn(hit, Interaction.class, i -> {
            i.setInteractionWidth(0.6f);
            i.setInteractionHeight(0.8f);
            i.setPersistent(true);
            i.setResponsive(true);
            i.addScoreboardTag(TAG_KEYPAD);
            i.addScoreboardTag("kc.dir" + dir);
        });

        store.bind(air, code);
        return null;
    }

    /** Remove the keypad the admin is looking at (and its stored code). Returns true if one went. */
    public boolean removeLookedAt(Player admin) {
        Block air = null;
        RayTraceResult ents = admin.rayTraceEntities(6);
        if (ents != null && ents.getHitEntity() instanceof Interaction it
            && it.getScoreboardTags().contains(TAG_KEYPAD)) {
            air = it.getLocation().getBlock();
        }
        if (air == null) {
            RayTraceResult ray = admin.rayTraceBlocks(6);
            if (ray == null || ray.getHitBlock() == null || ray.getHitBlockFace() == null) return false;
            air = ray.getHitBlock().getRelative(ray.getHitBlockFace());
        }
        boolean any = false;
        for (var e : air.getWorld().getNearbyEntities(air.getLocation().add(0.5, 0.5, 0.5), 0.9, 0.9, 0.9)) {
            if (e.getScoreboardTags().contains(TAG_KEYPAD)) { e.remove(); any = true; }
        }
        if (store.unbind(air)) any = true;
        return any;
    }

    /** Open every iron door touching the keypad block or the wall behind it, then auto-close. */
    public void openNearbyDoors(Block keypadBlock, int dir) {
        Block wallBlock = keypadBlock.getRelative(wallFace(dir));
        Set<Block> doorBottoms = new LinkedHashSet<>();
        for (Block base : new Block[]{keypadBlock, wallBlock}) {
            for (BlockFace face : HORIZONTAL) {
                Doors.collectDoor(doorBottoms, base.getRelative(face));
                Doors.collectDoor(doorBottoms, base.getRelative(face).getRelative(BlockFace.DOWN));
                Doors.collectDoor(doorBottoms, base.getRelative(face).getRelative(0, -2, 0));
            }
        }
        Doors.expandDoublesInPlace(doorBottoms);
        Doors.openThenClose(plugin, doorBottoms);
    }

    // --- helpers ------------------------------------------------------------

    private ItemStack keypadItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(NamespacedKey.fromString("keycard:keypad"));
        item.setItemMeta(meta);
        return item;
    }

    /** dir toward the player, from the wall face they clicked: 1=south 2=west 3=north 4=east. */
    private int dirForFace(BlockFace face) {
        return switch (face) {
            case WEST -> 2;
            case NORTH -> 3;
            case EAST -> 4;
            default -> 1;   // SOUTH
        };
    }

    /** The wall is one block the OPPOSITE way from the keypad's facing (matches the readers). */
    static BlockFace wallFace(int dir) {
        return switch (dir) {
            case 2 -> BlockFace.EAST;   // faces west
            case 3 -> BlockFace.SOUTH;  // faces north
            case 4 -> BlockFace.WEST;   // faces east
            default -> BlockFace.NORTH; // faces south
        };
    }

    /** Display Y-rotation quaternions, matching the reader datapack's per-direction values. */
    private Quaternionf quatForDir(int dir) {
        return switch (dir) {
            case 2 -> new Quaternionf(0f, 0.7071f, 0f, 0.7071f);
            case 3 -> new Quaternionf(0f, 1f, 0f, 0f);
            case 4 -> new Quaternionf(0f, -0.7071f, 0f, 0.7071f);
            default -> new Quaternionf(0f, 0f, 0f, 1f);
        };
    }

}
