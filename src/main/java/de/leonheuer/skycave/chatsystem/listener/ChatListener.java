package de.leonheuer.skycave.chatsystem.listener;

import de.leonheuer.skycave.chatsystem.enums.Violation;
import de.leonheuer.skycave.chatsystem.utils.NotificationUtils;
import de.leonheuer.skycave.chatsystem.utils.RegexUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    // list of all swear words to be filtered out
    private final String[] swearWords = new String[]{
            "Heil Hitler",
            "Hure",
            "Huso",
            "Bastard",
            "Fotze",
            "fick",
            "Schlampe",
            "Wichser",
            "Schwuchtel",
            "Spast",
            "Arsch",
            "Titte",
            "Nutte",
            "Schwanz",
            "suck my dick",
            "fuck",
            "horny",
            "slut",
            "slag",
            "boob",
            "pussy",
            "vagina",
            "faggot",
            "penis",
            "bugger",
            "cunt",
            "nigger",
            "nigga",
            "jerk",
            "anal",
            "wanker",
            "tosser",
            "cock",
            "whore",
            "bitch",
            "asshole",
            "twat",
            "sperm",
            "spunk",
            "testicle",
            "milf",
            "gilf",
            "retard",
            "anus",
            "prick",
    };

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        System.out.println(message);

        if (sender.hasPermission("skycave.chat.bypass.*")) {
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
        if (!sender.hasPermission("skycave.chat.bypass.swear")) {
            for (String word : swearWords) {
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
    }

}
