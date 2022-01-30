package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final ChatSystem main;

    public JoinListener(ChatSystem main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("skycave.chat.bypass.move")) {
            main.notMoved.add(player);
        }
        if (!player.hasPermission("skycave.chat.bypass.wait")) {
            main.secondAfterLogin.add(player);
            Bukkit.getScheduler().runTaskLater(main, () -> main.secondAfterLogin.remove(player), 40);
        }
    }

}
