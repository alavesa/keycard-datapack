package fi.alavesa.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public final class KeycardsPlugin extends JavaPlugin implements TabCompleter {

    private KeypadStore keypadStore;
    private KeypadListener keypadListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        keypadStore = new KeypadStore(this);
        KeypadMenu keypadMenu = new KeypadMenu(this, keypadStore);
        keypadListener = new KeypadListener(this, keypadStore, keypadMenu);
        getServer().getPluginManager().registerEvents(new ReaderListener(this), this);
        getServer().getPluginManager().registerEvents(new CardMenu(), this);
        getServer().getPluginManager().registerEvents(keypadMenu, this);
        getServer().getPluginManager().registerEvents(keypadListener, this);
        // onTabComplete lives on this plugin; wire it to the command explicitly (JavaPlugin
        // registers itself as executor but not as the tab completer).
        if (getCommand("keycards") != null) {
            getCommand("keycards").setTabCompleter(this);
        }
        getLogger().info("Keycards plugin enabled - readers and keypads are now handled by the plugin.");
    }

    @Override
    public void onDisable() {
        // If anyone is mid-swipe during a shutdown/reload, hand their card back first
        SwipeAnimation.returnAll(getServer().getOnlinePlayers());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (!player.hasPermission("keycards.admin")) {
            player.sendMessage(Component.text("You do not have permission.", NamedTextColor.RED));
            return true;
        }

        // /keycards keypad set <code> | /keycards keypad remove - bound by the NEXT door click
        if (args.length >= 1 && args[0].equalsIgnoreCase("keypad")) {
            if (!player.hasPermission("keycards.keypad")) {
                player.sendMessage(Component.text("You do not have permission.", NamedTextColor.RED));
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("set")) {
                String code = args[2];
                if (!code.matches("\\d+")) {
                    player.sendMessage(Component.text("Code must be digits only.", NamedTextColor.RED));
                    return true;
                }
                keypadListener.queueBind(player, code);
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("remove")) {
                keypadListener.queueRemove(player);
                return true;
            }
            player.sendMessage(Component.text("Usage: /keycards keypad set <code> | /keycards keypad remove",
                NamedTextColor.YELLOW));
            return true;
        }

        // Bare /keycards - the card menu
        CardMenu.open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player) || !player.hasPermission("keycards.keypad")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return prefixed(args[0], List.of("keypad"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("keypad")) {
            return prefixed(args[1], List.of("set", "remove"));
        }
        return Collections.emptyList();
    }

    private static List<String> prefixed(String typed, List<String> options) {
        String lower = typed.toLowerCase();
        return options.stream().filter(o -> o.startsWith(lower)).toList();
    }
}
