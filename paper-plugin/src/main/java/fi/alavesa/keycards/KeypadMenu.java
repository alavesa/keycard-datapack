package fi.alavesa.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * The keypad number pad (opens when a player right-clicks a keypad door): a chest GUI with
 * 0-9 buttons plus Clear and Enter, matching CardMenu's custom-InventoryHolder idiom (no anvil,
 * no chat prompt). Digits build up in the holder; Enter checks the code against KeypadStore and,
 * on a match, opens the door via the SAME Doors.openThenClose the swipe path uses.
 */
public final class KeypadMenu implements Listener {

    /** Max failed submissions before the door locks the player out for a while. */
    private static final int MAX_ATTEMPTS = 3;
    /** Digits the pad will accept before it stops growing (guards against absurd input). */
    private static final int MAX_DIGITS = 12;

    private final KeycardsPlugin plugin;
    private final KeypadStore store;

    public KeypadMenu(KeycardsPlugin plugin, KeypadStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    /** Marks a keypad inventory and carries the door + typed digits + failed-attempt count. */
    private static final class Holder implements InventoryHolder {
        private Inventory inventory;
        private final Block door;
        private final StringBuilder entered = new StringBuilder();
        private int attempts;
        private Holder(Block door) { this.door = door; }
        @Override public Inventory getInventory() { return inventory; }
    }

    // Slot layout in a 27-slot chest (3 rows). 1-9 across the top two rows, 0 / Clear / Enter
    // on the bottom, a masked display in the centre.
    private static final int[] DIGIT_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20}; // 1..9
    private static final int ZERO_SLOT = 22;
    private static final int CLEAR_SLOT = 21;
    private static final int ENTER_SLOT = 23;
    private static final int DISPLAY_SLOT = 4;

    public void open(Player player, Block door) {
        Holder holder = new Holder(door);
        Inventory inv = Bukkit.createInventory(holder, 27,
            Component.text("Keypad", NamedTextColor.DARK_AQUA));
        holder.inventory = inv;
        for (int i = 0; i < 9; i++) {
            inv.setItem(DIGIT_SLOTS[i], button(Material.PAPER, String.valueOf(i + 1), NamedTextColor.WHITE));
        }
        inv.setItem(ZERO_SLOT, button(Material.PAPER, "0", NamedTextColor.WHITE));
        inv.setItem(CLEAR_SLOT, button(Material.RED_DYE, "Clear", NamedTextColor.RED));
        inv.setItem(ENTER_SLOT, button(Material.LIME_DYE, "Enter", NamedTextColor.GREEN));
        refreshDisplay(holder);
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder holder)) return;
        event.setCancelled(true); // nothing in a keypad is ever picked up
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getRawSlot();
        if (slot == ENTER_SLOT) {
            submit(player, holder);
            return;
        }
        if (slot == CLEAR_SLOT) {
            holder.entered.setLength(0);
            refreshDisplay(holder);
            click(player, 1.0f);
            return;
        }
        int digit = digitForSlot(slot);
        if (digit < 0) return;
        if (holder.entered.length() < MAX_DIGITS) {
            holder.entered.append(digit);
            refreshDisplay(holder);
            click(player, 1.6f);
        }
    }

    private void submit(Player player, Holder holder) {
        String code = holder.entered.toString();
        Location loc = holder.door.getLocation().add(0.5, 0.5, 0.5);
        if (!code.isEmpty() && store.matches(holder.door, code)) {
            player.closeInventory();
            Msg.actionbar(player, Component.text("Access granted", NamedTextColor.GREEN));
            playConfigSound(loc, "grant");
            Doors.openThenClose(plugin, holder.door);
            return;
        }
        holder.attempts++;
        holder.entered.setLength(0);
        refreshDisplay(holder);
        Msg.actionbar(player, Component.text("Access denied", NamedTextColor.RED));
        playConfigSound(loc, "deny");
        if (holder.attempts >= MAX_ATTEMPTS) {
            player.closeInventory();
            Msg.actionbar(player, Component.text("Keypad locked - too many attempts", NamedTextColor.RED));
        }
    }

    /** The masked display shows one bullet per entered digit, never the digits themselves. */
    private void refreshDisplay(Holder holder) {
        int n = holder.entered.length();
        String masked = n == 0 ? "- - - -" : "●".repeat(n);
        holder.inventory.setItem(DISPLAY_SLOT,
            button(Material.NAME_TAG, masked, NamedTextColor.AQUA));
    }

    private int digitForSlot(int slot) {
        if (slot == ZERO_SLOT) return 0;
        for (int i = 0; i < 9; i++) {
            if (DIGIT_SLOTS[i] == slot) return i + 1;
        }
        return -1;
    }

    private static ItemStack button(Material material, String label, NamedTextColor color) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text(label, color).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    private void click(Player player, float pitch) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, pitch);
    }

    private void playConfigSound(Location loc, String key) {
        String sound = plugin.getConfig().getString("sounds." + key, "");
        if (sound == null || sound.isEmpty()) return;
        float pitch = (float) plugin.getConfig().getDouble("sounds." + key + "-pitch", 1.0);
        loc.getWorld().playSound(loc, sound, 0.8f, pitch);
    }
}
