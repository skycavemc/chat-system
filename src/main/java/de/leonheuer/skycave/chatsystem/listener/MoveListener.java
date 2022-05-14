package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class MoveListener implements Listener {

    private final ChatSystem main;

    public MoveListener(ChatSystem main) {
        this.main = main;
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        main.getNotMovedList().remove(event.getPlayer());
    }

}
