package de.leonheuer.skycave.chatsystem.utils;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import de.leonheuer.skycave.chatsystem.enums.Message;
import de.leonheuer.skycave.chatsystem.enums.Violation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class NotificationUtils {

    private static final ChatSystem main = JavaPlugin.getPlugin(ChatSystem.class);

    /**
     * Warns the player of a violation they committed in their chat message and notifies all staff members.
     * @param player The player to warn
     * @param violation The violation the player has committed
     * @param original The original chat message
     */
    public static void handleViolation(@NotNull Player player, @NotNull Violation violation, @NotNull String original) {
        player.sendMessage(violation.getMessage().getMessage().get());
        notifyStaff(player, violation.getName(), original);
    }

    /**
     * Notifies all staff members of a violation a player has committed in their chat message.
     * @param player The player who violated a rule
     * @param violation The violation
     * @param original The original chat message
     */
    public static void notifyStaff(@NotNull Player player, @NotNull String violation, @NotNull String original) {
        String message = Message.STAFF_NOTIFY.getMessage()
                .replace("%player", player.getName())
                .replace("%violation", violation)
                .replace("%original", original)
                .get(false);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("skycave.chat.staff")) {
                p.sendMessage(message);
            }
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }

}
