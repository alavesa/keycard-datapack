package fi.alavesa.keycards;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The "realistic" swipe: the REAL keycard leaves the player's hand for the duration of the
 * animation (an ItemDisplay slides it down the reader face) and is returned afterwards.
 * The pending-return map + quit/death/disable hooks in KeycardsPlugin guarantee the card
 * can never be lost - the reason this was a plugin job and not a datapack job.
 */
public final class SwipeAnimation {

    public record PendingReturn(int slot, ItemStack card) {}

    private static final Map<UUID, PendingReturn> PENDING = new ConcurrentHashMap<>();

    private SwipeAnimation() {}

    public static void start(Plugin plugin, Player player, Interaction reader, int dir, ItemStack card) {
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack shown = card.clone();
        shown.setAmount(1);

        // Take the real card out of the hand for the duration of the swipe
        PENDING.put(player.getUniqueId(), new PendingReturn(slot, card.clone()));
        player.getInventory().setItem(slot, null);

        Location center = reader.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5);
        boolean debug = plugin.getConfig().getBoolean("debug", true);

        // Spawn one tick AFTER the interact event (spawning inside the cancelled-interact
        // handler is the prime suspect for the display never reaching clients). Tag is
        // "kcp.swipe" so no datapack version, old or new, can touch it.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ItemDisplay display = reader.getWorld().spawn(center, ItemDisplay.class, d -> {
                d.setItemStack(shown);
                d.setBillboard(Display.Billboard.FIXED);
                d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
                d.setTransformation(transform(dir, 0.30f));
                d.addScoreboardTag("kcp.swipe");
            });

            // One tick later the client has the start pose; then interpolate down over 8 ticks
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (debug) {
                    plugin.getLogger().info("[swipe] display valid=" + display.isValid()
                        + " trackedBy=" + display.getTrackedBy().size() + " players, item="
                        + display.getItemStack().getType() + ", at " + display.getLocation().toVector());
                }
                if (!display.isValid()) {
                    plugin.getLogger().warning("[swipe] display was removed externally within 1 tick!");
                    return;
                }
                display.setInterpolationDelay(0);
                display.setInterpolationDuration(8);
                display.setTransformation(transform(dir, -0.20f));
            }, 1L);

            // End of animation: remove the visual, give the card back
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                display.remove();
                returnCard(player);
            }, 15L);
        }, 1L);
    }

    /** Give a pending card back (no-op if none). Safe to call from quit/death/disable hooks. */
    public static void returnCard(Player player) {
        PendingReturn pending = PENDING.remove(player.getUniqueId());
        if (pending == null) return;
        ItemStack inSlot = player.getInventory().getItem(pending.slot());
        if (inSlot == null || inSlot.getType().isAir()) {
            player.getInventory().setItem(pending.slot(), pending.card());
        } else {
            player.getInventory().addItem(pending.card()).values()
                .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
        }
    }

    /** A card is mid-swipe for this player (their card is out of the inventory right now). */
    public static boolean isSwiping(Player player) {
        return PENDING.containsKey(player.getUniqueId());
    }

    /** On death: hand the card to the death drops instead of the (about to be cleared) inventory. */
    public static ItemStack takePendingForDeath(Player player) {
        PendingReturn pending = PENDING.remove(player.getUniqueId());
        return pending == null ? null : pending.card();
    }

    public static void returnAll(Iterable<? extends Player> players) {
        for (Player p : players) returnCard(p);
    }

    /**
     * Card pose in front of the reader face. dir = the way the reader faces (1=south 2=west
     * 3=north 4=east), matching the datapack's kc.dir tags; the card floats 0.31 out of the
     * block center toward the player and slides from y +0.30 down to -0.20.
     */
    private static Transformation transform(int dir, float y) {
        Vector3f translation = switch (dir) {
            case 2 -> new Vector3f(-0.31f, y, 0f);
            case 3 -> new Vector3f(0f, y, -0.31f);
            case 4 -> new Vector3f(0.31f, y, 0f);
            default -> new Vector3f(0f, y, 0.31f);
        };
        // +180 versus the reader's facing: the card's FACE points at the
        // player standing in front of the reader, not away from them
        Quaternionf rotation = switch (dir) {
            case 2 -> new Quaternionf().rotationY((float) Math.toRadians(-90));
            case 3 -> new Quaternionf();
            case 4 -> new Quaternionf().rotationY((float) Math.toRadians(90));
            default -> new Quaternionf().rotationY((float) Math.toRadians(180));
        };
        return new Transformation(translation, rotation, new Vector3f(0.4f, 0.4f, 0.4f), new Quaternionf());
    }
}
