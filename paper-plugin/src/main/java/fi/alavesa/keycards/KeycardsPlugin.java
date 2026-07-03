package fi.alavesa.keycards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class KeycardsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ReaderListener(this), this);
        getServer().getPluginManager().registerEvents(new CardMenu(), this);
        getLogger().info("Keycards plugin enabled - readers are now handled by the plugin.");
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
        CardMenu.open(player);
        return true;
    }
}
