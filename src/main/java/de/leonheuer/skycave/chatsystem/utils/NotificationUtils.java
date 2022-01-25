package de.leonheuer.skycave.chatsystem.utils;

import de.leonheuer.skycave.chatsystem.enums.Message;
import de.leonheuer.skycave.chatsystem.enums.Violation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NotificationUtils {

    public static void handleViolation(@NotNull Player player, @NotNull Violation violation, @NotNull String original) {
        player.sendMessage(violation.getMessage().getMessage().get());
        notifyStaff(player, violation.getName(), original);
    }

    public static void notifyStaff(@NotNull Player player, @NotNull String violation, @NotNull String original) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("skycave.chat.staff")) {
                p.sendMessage(Message.STAFF_NOTIFY.getMessage()
                        .replace("%player", player.getName())
                        .replace("%violation", violation)
                        .replace("%original", original)
                        .get(false));
            }
        }
    }

}
