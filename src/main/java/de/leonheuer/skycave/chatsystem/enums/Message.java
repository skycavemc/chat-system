package de.leonheuer.skycave.chatsystem.enums;

import de.leonheuer.skycave.chatsystem.models.FormattableString;

public enum Message {

    // player warnings
    DISALLOWED_CHARS("&cDie Nachricht enthält verbotene Zeichen."),
    SPAM("&cDie Nachricht enrhält Zeichen-Spam."),
    SWEAR_WORDS("&cBitte achte auf deine Ausdrucksweise!"),
    IP_ADDRESS("&cDie Nachricht enthält eine IP-Adresse."),
    DOMAIN("&cDie Nachricht enthält eine Domain."),

    // staff messages
    STAFF_NOTIFY("&4CHAT: &c%player &7hat gesendet: &c%violation&7. &eOriginal-Nachricht: &7%original"),
    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public FormattableString getMessage() {
        return new FormattableString(message);
    }
}
