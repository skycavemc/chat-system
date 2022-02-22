package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import de.leonheuer.skycave.chatsystem.models.ChatLikeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final ChatSystem main;

    public ChatListener(ChatSystem main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        ChatLikeEvent event = new ChatLikeEvent(main, e.getPlayer(), e.getMessage(), e.isCancelled());
        event.checkMessage();
        e.setMessage(event.getMessage());
        e.setCancelled(event.isCancelled());
    }

}
