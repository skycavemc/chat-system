package de.leonheuer.skycave.chatsystem.enums;

import de.leonheuer.skycave.chatsystem.models.ColoredStringBuilder;

public enum Message {

    // player warnings
    DISALLOWED_CHARS("&cDie Nachricht enthält verbotene Zeichen."),
    SPAM("&cDie Nachricht enthält Zeichen-Spam."),
    SWEAR_WORDS("&cBitte achte auf deine Ausdrucksweise!"),
    IP_ADDRESS("&cDie Nachricht enthält eine IP-Adresse."),
    DOMAIN("&cDie Nachricht enthält eine Domain."),
    NO_CHAT_UNTIL_MOVED("&7Du kannst erst chatten sobald du dich bewegst."),
    WAIT_SECOND("&cBitte warte eine Sekunde, bevor du eine Nachricht sendest."),
    SIMILAR("&cDeine Nachricht ähnelt deiner vorherigen."),
    TOO_MANY("&cDu sendest zu viele Nachrichten innerhalb kurzer Zeit."),

    // chatsystem command
    CHAT_SYSTEM_HELP_HEADER("&8~~~~ &2Befehlshilfe für das ChatSystem &8~~~~"),
    CHAT_SYSTEM_RELOAD_HELP("&e/chatsystem reload &8- &7&oLädt die Konfiguration neu"),
    CHAT_SYSTEM_RELOAD_SUCCESS("&aDie Konfiguration wurde neugeladen."),
    CHAT_SYSTEM_RELOAD_ERROR("&cBeim Neuladen trat ein Fehler auf. Siehe Konsole für weitere Informationen."),

    // chat clear command
    CHAT_CLEAR_BROADCAST("&cDer Chat wurde von &4%player &cgeleert!"),

    // staff messages
    STAFF_NOTIFY("&4CHAT: &c%player &7hat gesendet: &c%violation&7. &eOriginal-Nachricht: &7%original"),
    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public ColoredStringBuilder getMessage() {
        return new ColoredStringBuilder(message);
    }
}
