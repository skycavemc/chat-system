package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class JoinLeaveListener implements Listener {

    private final ChatSystem main;

    public JoinLeaveListener(ChatSystem main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("skycave.chat.bypass.move")) {
            main.getNotMovedList().add(player);
        }
        if (!player.hasPermission("skycave.chat.bypass.wait")) {
            main.getLoginCooldownList().add(player);
            Bukkit.getScheduler().runTaskLater(main, () -> main.getLoginCooldownList().remove(player), 40);
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        handleLogout(event.getPlayer());
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        handleLogout(event.getPlayer());
    }

    private void handleLogout(Player player) {
        main.getNotMovedList().remove(player);
        main.getLoginCooldownList().remove(player);
        main.getLastMessageMap().remove(player);
        main.getLastMessageCountMap().remove(player);
    }

}
