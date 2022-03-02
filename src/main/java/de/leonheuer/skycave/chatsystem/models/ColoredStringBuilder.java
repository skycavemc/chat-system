package de.leonheuer.skycave.chatsystem.models;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class ColoredStringBuilder {

    private String result;

    public ColoredStringBuilder(@NotNull String base) {
        result = base;
    }

    public ColoredStringBuilder replace(@NotNull String from, @NotNull String to) {
        result = result.replaceFirst(from, to);
        return this;
    }

    public ColoredStringBuilder replaceAll(@NotNull String from, @NotNull String to) {
        result = result.replaceAll(from, to);
        return this;
    }

    public String get() {
        result = ChatSystem.PREFIX + result;
        result = ChatColor.translateAlternateColorCodes('&', result);
        return result;
    }

    public String get(boolean prefix) {
        if (prefix) {
            result = ChatSystem.PREFIX + result;
        }
        result = ChatColor.translateAlternateColorCodes('&', result);
        return result;
    }

    public String get(boolean prefix, boolean formatted) {
        if (prefix) {
            result = ChatSystem.PREFIX + result;
        }
        if (formatted) {
            result = ChatColor.translateAlternateColorCodes('&', result);
        }
        return result;
    }

}
