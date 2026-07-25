package fi.alavesa.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keypads: a numeric code that opens iron doors, independent of physical keycards. Since the
 * rehaul a keypad is a PLACED wall device with a custom model (see {@link KeypadManager}) that
 * opens whatever door(s) sit beside it - right-clicking the device opens {@link KeypadMenu}. The
 * legacy path (a code baked directly onto a door, right-click the door) still works. Codes live
 * hashed in {@link KeypadStore}; doors open via the same {@link Doors} plumbing the readers use.
 */
public final class KeypadListener implements Listener {

    /** A pending admin action captured until they click a door: bind with a code, or remove. */
    private record Pending(String code, boolean remove) {}

    private final KeycardsPlugin plugin;
    private final KeypadStore store;
    private final KeypadMenu menu;
    private final KeypadManager manager;
    private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

    public KeypadListener(KeycardsPlugin plugin, KeypadStore store, KeypadMenu menu, KeypadManager manager) {
        this.plugin = plugin;
        this.store = store;
        this.menu = menu;
        this.manager = manager;
    }

    /** Right-click a placed keypad device: open the pad; a correct code opens the doors beside it. */
    @EventHandler
    public void onKeypadClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Interaction pad)) return;
        Set<String> tags = pad.getScoreboardTags();
        if (!tags.contains(KeypadManager.TAG_KEYPAD)) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        Block keyBlock = pad.getLocation().getBlock();
        int dir = dirOf(tags);
        menu.open(player, keyBlock, () -> manager.openNearbyDoors(keyBlock, dir));
    }

    /** Keypad devices are indestructible - remove them with /keycards keypad remove. */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getScoreboardTags().contains(KeypadManager.TAG_KEYPAD)) {
            event.setCancelled(true);
        }
    }

    private int dirOf(Set<String> tags) {
        for (String t : tags) {
            if (t.startsWith("kc.dir")) {
                try { return Integer.parseInt(t.substring("kc.dir".length())); }
                catch (NumberFormatException ignored) { }
            }
        }
        return 1;
    }

    /** Called by the command handler: the admin's next door click binds this code. */
    void queueBind(Player admin, String code) {
        pending.put(admin.getUniqueId(), new Pending(code, false));
        Msg.actionbar(admin, Component.text("Right-click the door to bind the keypad", NamedTextColor.AQUA));
    }

    /** Called by the command handler: the admin's next door click unbinds it. */
    void queueRemove(Player admin) {
        pending.put(admin.getUniqueId(), new Pending(null, true));
        Msg.actionbar(admin, Component.text("Right-click the door to remove its keypad", NamedTextColor.AQUA));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null || clicked.getType() != Material.IRON_DOOR) return;

        Player player = event.getPlayer();
        Block door = Doors.bottomOf(clicked);
        if (door == null) return;

        // An admin mid-bind claims this click regardless of whether the door is a keypad yet.
        Pending action = pending.remove(player.getUniqueId());
        if (action != null) {
            event.setCancelled(true);
            if (action.remove()) {
                boolean removed = store.unbind(door);
                Msg.actionbar(player, removed
                    ? Component.text("Keypad removed", NamedTextColor.GREEN)
                    : Component.text("That door had no keypad", NamedTextColor.YELLOW));
            } else {
                store.bind(door, action.code());
                Msg.actionbar(player, Component.text("Keypad bound", NamedTextColor.GREEN));
            }
            return;
        }

        // Not an admin action: if this door is a keypad, open the number pad instead of the door.
        if (store.isKeypad(door)) {
            event.setCancelled(true);
            menu.open(player, door);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pending.remove(event.getPlayer().getUniqueId());
    }
}
