package de.leonheuer.skycave.chatsystem;

import de.leonheuer.skycave.chatsystem.listener.ChatListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatSystem extends JavaPlugin {

    public static final String PREFIX = "&e&l| &6Chat &8» ";

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
