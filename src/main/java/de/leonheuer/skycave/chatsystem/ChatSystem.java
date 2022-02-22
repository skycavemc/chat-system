package de.leonheuer.skycave.chatsystem;

import de.leonheuer.skycave.chatsystem.commands.ChatSystemCommand;
import de.leonheuer.skycave.chatsystem.commands.ClearChatCommand;
import de.leonheuer.skycave.chatsystem.listener.ChatListener;
import de.leonheuer.skycave.chatsystem.listener.CommandListener;
import de.leonheuer.skycave.chatsystem.listener.JoinListener;
import de.leonheuer.skycave.chatsystem.listener.MoveListener;
import de.leonheuer.skycave.chatsystem.models.FileLogger;
import de.leonheuer.skycave.chatsystem.utils.FileUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ChatSystem extends JavaPlugin {

    public static final String PREFIX = "&e&l| &6Chat &8» ";
    public static final String CONFIG_VERSION = "1.0";
    public final List<Player> notMoved = new ArrayList<>();
    public final List<Player> secondAfterLogin = new ArrayList<>();
    public final HashMap<Player, String[]> lastMessage = new HashMap<>();
    private FileLogger chatLogger;
    private YamlConfiguration config = null;

    @Override
    public void onEnable() {
        try {
            checkConfigVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new CommandListener(this), this);
        pm.registerEvents(new JoinListener(this), this);
        pm.registerEvents(new MoveListener(this), this);

        registerCommand("chatsystem", new ChatSystemCommand(this));
        registerCommand("clearchat", new ClearChatCommand());

        chatLogger = new FileLogger(new File(getDataFolder(), "chat.log"));
    }

    /**
     * Reloads all configurations of the plugin.
     * Copies resources of the plugin in the data folder if they are missing.
     * @return Whether reloading succeeded.
     */
    public boolean reloadResources() {
        boolean succeeded;
        if (!getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
        }
        succeeded = FileUtils.copyResource(this, "config.yml");
        if (succeeded) {
            config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        }
        return succeeded;
    }

    private void checkConfigVersion() throws IOException {
        if (reloadResources() && config != null) {
            String ver = config.getString("config_version");

            if (ver == null) {
                config.set("config_version", CONFIG_VERSION);
                config.save(new File(getDataFolder(), "config.yml"));
                return;
            }

            if (!ver.equals(CONFIG_VERSION)) {
                Files.move(getDataFolder().toPath().resolveSibling("config.yml"),
                        getDataFolder().toPath().resolveSibling("config_old.yml"));
                if (reloadResources() && config != null) {
                    config.set("config_version", CONFIG_VERSION);
                    getLogger().info("A backup named \"config_old.yml\" has been created.");
                    config.save(new File(getDataFolder(), "config.yml"));
                }
            }
        }
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = this.getCommand(command);
        if (cmd == null) {
            getLogger().severe("The command /" + command + " has no entry in the plugin.yml.");
            return;
        }
        cmd.setExecutor(executor);
    }

    @Nullable
    public YamlConfiguration getConfiguration() {
        return config;
    }

    public FileLogger getChatLogger() {
        return chatLogger;
    }

}
