package de.leonheuer.skycave.chatsystem;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.leonheuer.skycave.chatsystem.listener.ChatListener;
import de.leonheuer.skycave.chatsystem.listener.JoinListener;
import de.leonheuer.skycave.chatsystem.listener.MoveListener;
import de.leonheuer.skycave.chatsystem.utils.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ChatSystem extends JavaPlugin {

    public static final String PREFIX = "&e&l| &6Chat &8» ";
    public final List<Player> notMoved = new ArrayList<>();
    public final List<Player> secondAfterLogin = new ArrayList<>();
    public final HashMap<Player, String> lastMessage = new HashMap<>();
    public final HashMap<Player, Integer> messageCount = new HashMap<>();
    private YamlConfiguration wordFilterConfig = null;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new JoinListener(this), this);
        pm.registerEvents(new MoveListener(this), this);

        if (FileUtils.copyResource(this, "word_filter.yml")) {
            wordFilterConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "word_filter.yml"));
        }
    }

    @Nullable
    public YamlConfiguration getWordFilterConfig() {
        return wordFilterConfig;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
