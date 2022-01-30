package de.leonheuer.skycave.chatsystem.commands;

import de.leonheuer.skycave.chatsystem.enums.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Collection<? extends Player> recipients = Bukkit.getOnlinePlayers();
        if (sender instanceof Player) {
            recipients.remove(sender);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("skycave.chat.bypass.clear")) {
                for (int i = 0; i < 300; i++) {
                    p.sendMessage("");
                }
            }
        }
        Bukkit.broadcast(Component.text(
                Message.CHAT_CLEAR_BROADCAST.getMessage().replace("%player", sender.getName()).get())
        );
        return true;
    }

}
