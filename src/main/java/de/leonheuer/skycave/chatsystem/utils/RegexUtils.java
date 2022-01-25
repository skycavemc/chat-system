package de.leonheuer.skycave.chatsystem.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static List<String> getMatches(@NotNull String source, @NotNull String regex) {
        List<String> output = new ArrayList<>();
        Matcher matcher = Pattern.compile(regex).matcher(source);
        while (matcher.find()) {
            output.add(matcher.group());
        }
        return output;
    }

    public static boolean matches(@NotNull String source, @NotNull String regex, @NotNull String... ignore) {
        List<String> matches = getMatches(source, regex);
        List<String> exclude = Arrays.stream(ignore).toList();
        if (!exclude.isEmpty()) {
            matches.removeIf(exclude::contains);
        }
        return matches.size() > 0;
    }

}
