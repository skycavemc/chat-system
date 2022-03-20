package de.leonheuer.skycave.chatsystem.models;

import de.leonheuer.skycave.chatsystem.ChatSystem;
import de.leonheuer.skycave.chatsystem.enums.Message;
import de.leonheuer.skycave.chatsystem.enums.Violation;
import de.leonheuer.skycave.chatsystem.utils.NotificationUtils;
import de.leonheuer.skycave.chatsystem.utils.StringComparisonUtils;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ChatLikeEvent {
    
    private final ChatSystem main;
    private final Player player;
    private String message;
    private boolean cancelled;

    public ChatLikeEvent(ChatSystem main, Player player, String message, boolean cancelled) {
        this.main = main;
        this.player = player;
        this.message = message;
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public void checkMessage() {
        main.getChatLogger().logln(player.getName() + " > " + message);

        if (player.hasPermission("skycave.chat.bypass.*")) {
            return;
        }

        String[] words = message.split("\\s");
        YamlConfiguration config = main.getConfiguration();

        // block chat until cooldown has passed
        if (main.secondAfterLogin.contains(player)) {
            player.sendMessage(Message.WAIT_SECOND.getMessage().get());
            cancelled = true;
            return;
        }

        // block chat until moved
        if (main.notMoved.contains(player)) {
            player.sendMessage(Message.NO_CHAT_UNTIL_MOVED.getMessage().get());
            cancelled = true;
            return;
        }

        // block similar messages
        if (config != null && !player.hasPermission("skycave.chat.bypass.similar") &&
                main.lastMessage.containsKey(player) &&
                !StringComparisonUtils.containsIgnoreCase(config.getStringList("whitelist_similarity"), words[0])
        ) {
            NormalizedLevenshtein levenshtein = new NormalizedLevenshtein();
            double percentage = config.getInt("similarity_percentage") / 100.0;
            if (levenshtein.similarity(message, main.lastMessage.get(player)[0]) >= percentage ||
                    main.lastMessage.get(player)[1] != null &&
                            levenshtein.similarity(message, main.lastMessage.get(player)[1]) >= percentage
            ) {
                NotificationUtils.handleViolation(player, Violation.SIMILAR, message);
                cancelled = true;
                return;
            }
        }

        boolean bpChars = player.hasPermission("skycave.chat.bypass.characters");
        boolean bpSpam = player.hasPermission("skycave.chat.bypass.spam");

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
                    NotificationUtils.handleViolation(player, Violation.DISALLOWED_CHARS, message);
                    cancelled = true;
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
                        NotificationUtils.handleViolation(player, Violation.SPAM, message);
                        cancelled = true;
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
                            NotificationUtils.handleViolation(player, Violation.SPAM, message);
                            cancelled = true;
                            return;
                        }
                    }
                }
            }
        }

        // check for swear words
        if (config != null && !player.hasPermission("skycave.chat.bypass.swear")) {
            for (String word : config.getStringList("block_words")) {
                if (StringUtils.containsIgnoreCase(message, word)) {
                    NotificationUtils.handleViolation(player, Violation.SWEAR_WORDS, message);
                    cancelled = true;
                    return;
                }
            }
        }

        // check for ip addresses
        if (!player.hasPermission("skycave.chat.bypass.ip")) {
            if (StringComparisonUtils.matches(message,
                    "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}",
                    "80.82.215.68")
            ) {
                NotificationUtils.handleViolation(player, Violation.IP_ADDRESS, message);
                cancelled = true;
                return;
            }
        }

        // check for domains
        if (!player.hasPermission("skycave.chat.bypass.domains")) {
            if (
                    StringComparisonUtils.matches(message,
                            "(\\s+|^)[A-Za-z0-9-]{1,63}\\s*(\\.|,|dot|\\(dot\\)|punkt|\\(punkt\\)|-)\\s*(de|com|net|eu|bz|me|xyz)(\\s+|\\W|$)",
                            "skycave.de", "skybee.me", "youtu.be", "youtube.com", "imgur.com", "twitch.tv", "gamepedia.com")
            ) {
                NotificationUtils.handleViolation(player, Violation.DOMAIN, message);
                cancelled = true;
                return;
            }
        }

        // remove caps
        if (!player.hasPermission("skycave.chat.bypass.caps")) {
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
        if (!player.hasPermission("skycave.chat.bypass.grammar")) {
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

        if (!cancelled) {
            String[] lastMessages = main.lastMessage.get(player);
            if (lastMessages == null) {
                lastMessages = new String[]{message, null};
            } else {
                lastMessages[1] = lastMessages[0];
                lastMessages[0] = message;
            }
            main.lastMessage.put(player, lastMessages);
        }
    }
}
