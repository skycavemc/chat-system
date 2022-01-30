package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private final ChatSystem main;

    public MoveListener(ChatSystem main) {
        this.main = main;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        main.notMoved.remove(event.getPlayer());
    }

}
