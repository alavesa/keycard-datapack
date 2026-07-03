package fi.alavesa.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.List;

/**
 * The six keycard types. Cards are identified by their custom_model_data STRING id
 * (keycard_1 ... keycard_5, keycard_omni) - the same ids the datapack's give-functions
 * write, so cards given by either the datapack or this plugin are interchangeable.
 */
public final class Cards {

    public record CardType(String modelId, int level, String display, NamedTextColor color, Material base) {}

    public static final List<CardType> ALL = List.of(
        new CardType("keycard_1",    1,  "Keycard - Level 1", NamedTextColor.GRAY,         Material.CREEPER_BANNER_PATTERN),
        new CardType("keycard_2",    2,  "Keycard - Level 2", NamedTextColor.GRAY,         Material.SKULL_BANNER_PATTERN),
        new CardType("keycard_3",    3,  "Keycard - Level 3", NamedTextColor.GRAY,         Material.MOJANG_BANNER_PATTERN),
        new CardType("keycard_4",    4,  "Keycard - Level 4", NamedTextColor.GRAY,         Material.GLOBE_BANNER_PATTERN),
        new CardType("keycard_5",    5,  "Keycard - Level 5", NamedTextColor.GRAY,         Material.PIGLIN_BANNER_PATTERN),
        new CardType("keycard_omni", 99, "Keycard - Omni",    NamedTextColor.LIGHT_PURPLE, Material.FLOW_BANNER_PATTERN)
    );

    private Cards() {}

    /** Clearance level of the held item, or -1 if it is not a keycard. */
    public static int levelOf(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;
        ItemMeta meta = item.getItemMeta();
        for (String id : meta.getCustomModelDataComponent().getStrings()) {
            for (CardType c : ALL) {
                if (c.modelId().equals(id)) return c.level();
            }
        }
        return -1;
    }

    public static ItemStack build(CardType type) {
        ItemStack item = new ItemStack(type.base());
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text(type.display(), type.color()).decoration(TextDecoration.ITALIC, false));
        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(type.modelId()));
        meta.setCustomModelDataComponent(cmd);
        item.setItemMeta(meta);
        return item;
    }
}
