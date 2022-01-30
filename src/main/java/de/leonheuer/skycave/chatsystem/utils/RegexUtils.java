package de.leonheuer.skycave.chatsystem.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    /**
     * Gets a list of all substrings from the source that match the provided regex.
     * @param source The string to search in
     * @param regex The regular expression to find the matches
     * @return A list of all matches found
     */
    public static List<String> getMatches(@NotNull String source, @NotNull String regex) {
        List<String> output = new ArrayList<>();
        Matcher matcher = Pattern.compile(regex).matcher(source);
        while (matcher.find()) {
            output.add(matcher.group());
        }
        return output;
    }

    /**
     * Checks if the string contains at least one match with the given regex.
     * Ignored strings will not be counted if they are one of the matches.
     * @param source The string to check
     * @param regex The regular expression to scan the string for
     * @param ignore The strings to be excluded from counting. Should be a substring of the source.
     * @return Whether the amount of matches is at least 1.
     */
    public static boolean matches(@NotNull String source, @NotNull String regex, @NotNull String... ignore) {
        List<String> matches = getMatches(source, regex);
        List<String> exclude = Arrays.stream(ignore).toList();
        if (!exclude.isEmpty()) {
            matches.removeIf(exclude::contains);
        }
        return matches.size() > 0;
    }

}
