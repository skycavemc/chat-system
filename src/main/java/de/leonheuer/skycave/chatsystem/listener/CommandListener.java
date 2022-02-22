package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import de.leonheuer.skycave.chatsystem.models.ChatLikeEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    private final ChatSystem main;

    public CommandListener(ChatSystem main) {
        this.main = main;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        YamlConfiguration config = main.getConfiguration();

        if (config != null && !e.getPlayer().hasPermission("skycave.chat.bypass.commands")) {
            // remove slash and slit the arguments
            String[] args = e.getMessage().substring(1).split("\s");
            // check if command should be handled as chat
            if (config.getStringList("chat_commands").contains(args[0])) {
                // message without first argument
                String message = String.join(" ", args).substring(args[0].length() + 1);
                ChatLikeEvent event = new ChatLikeEvent(main, e.getPlayer(), message, e.isCancelled());
                event.checkMessage();
                e.setMessage("/" + args[0] + " " + event.getMessage());
                e.setCancelled(event.isCancelled());
            }
        }
    }

}
