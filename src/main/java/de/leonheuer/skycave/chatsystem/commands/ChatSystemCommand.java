package de.leonheuer.skycave.chatsystem.commands;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import de.leonheuer.skycave.chatsystem.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatSystemCommand implements CommandExecutor, TabCompleter {

    private final ChatSystem main;

    public ChatSystemCommand(ChatSystem main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0]) {
            case "reload" -> {
                if (main.reloadResources()) {
                    sender.sendMessage(Message.CHAT_SYSTEM_RELOAD_SUCCESS.getMessage().get());
                } else {
                    sender.sendMessage(Message.CHAT_SYSTEM_RELOAD_ERROR.getMessage().get());
                }
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Message.CHAT_SYSTEM_HELP_HEADER.getMessage().get(false));
        sender.sendMessage(Message.CHAT_SYSTEM_RELOAD_HELP.getMessage().get(false));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            arguments.add("reload");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
