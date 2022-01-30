package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import de.leonheuer.skycave.chatsystem.enums.Message;
import de.leonheuer.skycave.chatsystem.enums.Violation;
import de.leonheuer.skycave.chatsystem.utils.NotificationUtils;
import de.leonheuer.skycave.chatsystem.utils.RegexUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Locale;

public class ChatListener implements Listener {

    private final ChatSystem main;

    public ChatListener(ChatSystem main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        //String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        String message = event.getMessage();
        int words = message.split("\\s").length;

        if (sender.hasPermission("skycave.chat.bypass.*")) {
            return;
        }

        // block chat until cooldown has passed
        if (main.secondAfterLogin.contains(sender)) {
            event.setCancelled(true);
            sender.sendMessage(Message.WAIT_SECOND.getMessage().get());
            return;
        }

        // block chat until moved
        if (main.notMoved.contains(sender)) {
            event.setCancelled(true);
            sender.sendMessage(Message.NO_CHAT_UNTIL_MOVED.getMessage().get());
            return;
        }

        boolean bpChars = sender.hasPermission("skycave.chat.bypass.characters");
        boolean bpSpam = sender.hasPermission("skycave.chat.bypass.spam");

        // loop through the characters
        char cache = ' ';
        int count = 0;
        int lastWord = 0;
        for (char c : message.toCharArray()) {
            // check for disallowed unicode characters
            if (!bpChars) {
                if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN &&
                        Character.UnicodeBlock.of(c) != Character.UnicodeBlock.LATIN_1_SUPPLEMENT
                ) {
                    NotificationUtils.handleViolation(sender, Violation.DISALLOWED_CHARS, message);
                    event.setCancelled(true);
                    return;
                }
            }

            // check for spam
            if (!bpSpam) {
                // same character
                if (c == cache) {
                    count++;
                    if (count > 4) {
                        NotificationUtils.handleViolation(sender, Violation.SPAM, message);
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    count = 0;
                }
                cache = c;

                // too long words
                if (c == ' ') {
                    lastWord = 0;
                } else {
                    lastWord++;
                    if (lastWord > 40) {
                        // urls might be very long
                        if (!message.contains("www.") &&
                                !message.contains("https://") &&
                                !message.contains("http://")
                        ) {
                            NotificationUtils.handleViolation(sender, Violation.SPAM, message);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        // check for swear words
        YamlConfiguration wordFilter = main.getWordFilterConfig();
        if (wordFilter != null && !sender.hasPermission("skycave.chat.bypass.swear")) {
            for (String word : wordFilter.getStringList("block_words")) {
                if (StringUtils.containsIgnoreCase(message, word)) {
                    NotificationUtils.handleViolation(sender, Violation.SWEAR_WORDS, message);
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // check for ip addresses
        if (!sender.hasPermission("skycave.chat.bypass.ip")) {
            if (RegexUtils.matches(message,
                    "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}",
                    "80.82.215.68")
            ) {
                NotificationUtils.handleViolation(sender, Violation.IP_ADDRESS, message);
                event.setCancelled(true);
                return;
            }
        }

        // check for domains
        if (!sender.hasPermission("skycave.chat.bypass.domains")) {
            if (
                    RegexUtils.matches(message,
                            "(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\s?(\\.|,|dot|\\(dot\\)|punkt|\\(punkt\\))\\s?[A-Za-z]{2,6}",
                            "skycave.de", "skybee.me", "youtu.be", "youtube.com", "imgur.com", "twitch.tv", "gamepedia.com")
            ) {
                NotificationUtils.handleViolation(sender, Violation.DOMAIN, message);
                event.setCancelled(true);
                return;
            }
        }

        // grammar
        if (!sender.hasPermission("skycave.chat.bypass.grammar")) {
            message = StringUtils.capitalize(message);
            // 2nd letter lowercase if third is not uppercase
            if (message.length() >= 3 && !StringUtils.isAllUpperCase(message.substring(2, 3))) {
                message = message.charAt(0) + message.substring(1, 2).toLowerCase(Locale.GERMAN) + message.substring(2);
            }
            // dot at the end
            if (words >= 3 && message.length() >= 10 &&
                    !message.endsWith(".") &&
                    !message.endsWith("!") &&
                    !message.endsWith("?")
            ) {
                message = message + ".";
            }
        }

        //event.message(Component.text(message));
        event.setMessage(message);
    }

}
