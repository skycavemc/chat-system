package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import de.leonheuer.skycave.chatsystem.enums.Message;
import de.leonheuer.skycave.chatsystem.enums.Violation;
import de.leonheuer.skycave.chatsystem.utils.NotificationUtils;
import de.leonheuer.skycave.chatsystem.utils.RegexUtils;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
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
        String message = event.getMessage();
        main.getChatLogger().logln(sender.getName() + " > " + message);

        if (sender.hasPermission("skycave.chat.bypass.*")) {
            return;
        }

        String[] words = message.split("\\s");
        YamlConfiguration config = main.getConfiguration();

        // block chat until cooldown has passed
        if (main.secondAfterLogin.contains(sender)) {
            sender.sendMessage(Message.WAIT_SECOND.getMessage().get());
            event.setCancelled(true);
            return;
        }

        // block chat until moved
        if (main.notMoved.contains(sender)) {
            sender.sendMessage(Message.NO_CHAT_UNTIL_MOVED.getMessage().get());
            event.setCancelled(true);
            return;
        }

        // block similar messages
        if (config != null && !sender.hasPermission("skycave.chat.bypass.similar") &&
                main.lastMessage.containsKey(sender)
        ) {
            NormalizedLevenshtein levenshtein = new NormalizedLevenshtein();
            double percentage =  config.getInt("similarity_percentage") / 100.0;
            if (levenshtein.similarity(message, main.lastMessage.get(sender)[0]) >= percentage ||
                    main.lastMessage.get(sender)[1] != null &&
                            levenshtein.similarity(message, main.lastMessage.get(sender)[1]) >= percentage
            ) {
                NotificationUtils.handleViolation(sender, Violation.SIMILAR, message);
                event.setCancelled(true);
                return;
            }
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
                if (String.valueOf(c).equalsIgnoreCase(String.valueOf(cache))) {
                    if (count == 0) {
                        count = 2;
                    } else {
                        count++;
                    }
                    if (count > 3) {
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
        if (config != null && !sender.hasPermission("skycave.chat.bypass.swear")) {
            for (String word : config.getStringList("block_words")) {
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
                            "(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\s?(\\.|,|dot|\\(dot\\)|punkt|\\(punkt\\))\\s?(de|com|net|eu|bz|me|xyz)",
                            "skycave.de", "skybee.me", "youtu.be", "youtube.com", "imgur.com", "twitch.tv", "gamepedia.com")
            ) {
                NotificationUtils.handleViolation(sender, Violation.DOMAIN, message);
                event.setCancelled(true);
                return;
            }
        }

        // remove caps
        if (!sender.hasPermission("skycave.chat.bypass.caps")) {
            for (int i = 0; i < words.length; i++) {
                if (words[i].length() < 5) {
                    continue;
                }
                double uppercase = 0;
                for (char c : words[i].toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        uppercase++;
                    }
                }
                if (uppercase / words[i].length() > .8) {
                    words[i] = words[i].toLowerCase(Locale.GERMAN);
                }
            }
            message = String.join(" ", words);
        }

        // grammar
        if (!sender.hasPermission("skycave.chat.bypass.grammar")) {
            message = StringUtils.capitalize(message);

            // 2nd letter lowercase if third is not uppercase
            if (message.length() >= 3 &&
                    StringUtils.isAllUpperCase(message.substring(0, 2)) &&
                    Character.isLowerCase(message.charAt(2))
            ) {
                StringBuilder sb = new StringBuilder();
                message = sb
                        .append(message.charAt(0))
                        .append(Character.toLowerCase(message.charAt(1)))
                        .append(message.substring(2))
                        .toString();
            }

            // dot at the end
            if (words.length >= 3 && message.length() >= 10 &&
                    !message.endsWith(".") &&
                    !message.endsWith("!") &&
                    !message.endsWith("?")
            ) {
                message = message + ".";
            }
        }

        event.setMessage(message);
        if (!event.isCancelled()) {
            String[] lastMessages = main.lastMessage.get(sender);
            if (lastMessages == null) {
                lastMessages = new String[]{message, null};
            } else {
                lastMessages[1] = lastMessages[0];
                lastMessages[0] = message;
            }
            main.lastMessage.put(sender, lastMessages);
        }
    }

}
