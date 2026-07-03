package fi.alavesa.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * The card management menu (/keycards): one row with all six card types - click a card to
 * get a copy. The custom InventoryHolder marks the menu so clicks in it are always ours.
 */
public final class CardMenu implements Listener {

    private static final class Holder implements InventoryHolder {
        private Inventory inventory;
        @Override public Inventory getInventory() { return inventory; }
    }

    public static void open(Player player) {
        Holder holder = new Holder();
        Inventory inv = Bukkit.createInventory(holder, 9, Component.text("Keycards", NamedTextColor.DARK_AQUA));
        holder.inventory = inv;
        int slot = 1;
        for (Cards.CardType type : Cards.ALL) {
            inv.setItem(slot++, Cards.build(type));
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;
        int level = Cards.levelOf(clicked);
        for (Cards.CardType type : Cards.ALL) {
            if (type.level() == level) {
                player.getInventory().addItem(Cards.build(type)).values()
                    .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
                player.sendActionBar(Component.text(type.display() + " added", NamedTextColor.AQUA));
                return;
            }
        }
    }
}
