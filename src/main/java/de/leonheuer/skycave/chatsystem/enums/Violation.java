package de.leonheuer.skycave.chatsystem.enums;

public enum Violation {

    DISALLOWED_CHARS(Message.DISALLOWED_CHARS, "Verbotene Zeichen"),
    SPAM(Message.SPAM, "Zeichen-Spam"),
    SWEAR_WORDS(Message.SWEAR_WORDS, "Schimpfwörter"),
    IP_ADDRESS(Message.IP_ADDRESS, "IP-Adresse"),
    DOMAIN(Message.DOMAIN, "Domain"),
    SIMILAR(Message.SIMILAR, "Ähnliche Nachricht"),
    TOO_MANY(Message.TOO_MANY, "Zu viele Nachrichten"),
    ;

    private final Message message;
    private final String name;

    Violation(Message message, String name) {
        this.message = message;
        this.name = name;
    }

    public Message getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }
}
