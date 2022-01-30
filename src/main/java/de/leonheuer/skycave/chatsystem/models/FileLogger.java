package de.leonheuer.skycave.chatsystem.models;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple logger class that allows to log strings to a file.
 * Allows customized actions after the string has been logged.
 */
public class FileLogger {

    private final List<Consumer<String>> actions = new ArrayList<>();
    private String format;
    private File logFile;

    /**
     * Creates a new logger instance.
     * @param logFile The file to log to
     * @param actions The actions to perform after logging
     */
    @SafeVarargs
    public FileLogger(@NotNull File logFile, Consumer<String>... actions) {
        this.format = "[%timestamp] %output";
        this.logFile = logFile;
        this.actions.addAll(Arrays.stream(actions).toList());
    }

    /**
     * Creates a new logger instance with a custom format.
     * Placeholders for the format: %timestamp, %output
     * @param format The logging format
     * @param logFile The file to log to
     * @param actions The actions to perform after logging
     */
    @SafeVarargs
    public FileLogger(@NotNull String format, @NotNull File logFile, Consumer<String>... actions) {
        this.format = format;
        this.logFile = logFile;
        this.actions.addAll(Arrays.stream(actions).toList());
    }

    /**
     * Gets the logging format.
     * @return The logging format
     */
    @NotNull
    public String getFormat() {
        return format;
    }

    /**
     * Sets the logging format.
     * Placeholders for the format: %timestamp, %output
     * @param format The logging format
     */
    public void setFormat(@NotNull String format) {
        this.format = format;
    }

    /**
     * Gets the file to log to.
     * @return The log file
     */
    @NotNull
    public File getLogFile() {
        return logFile;
    }

    /**
     * Sets the file to log to. The file will be created if it does not exist.
     * @param logFile The log file
     */
    public void setLogFile(@NotNull File logFile) {
        this.logFile = logFile;
    }

    /**
     * Adds customized actions to perform after logging.
     * The actions should be provided as a consumer with 1 parameter.
     * When logging, the output string will be passed to the consumer.
     * @param actions The custom actions
     */
    @SafeVarargs
    public final void addActions(Consumer<String>... actions) {
        this.actions.addAll(Arrays.stream(actions).toList());
    }

    /**
     * Clears all the custom actions.
     */
    public void clearActions() {
        actions.clear();
    }

    /**
     * Logs the given output string to the log file and executes all custom actions.
     * @param output The output string to log
     */
    public void log(@NotNull String output) {
        try {
            FileWriter writer = new FileWriter(logFile);
            writer.write(output);
            writer.close();
            for (Consumer<String> c : actions) {
                c.accept(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the given output string to the log file and executes all custom actions.
     * Inserts a new line at the end of the log file.
     * @param output The output string to log
     */
    public void logln(@NotNull String output) {
        try {
            FileWriter writer = new FileWriter(logFile);
            writer.write(output + "\n");
            writer.close();
            for (Consumer<String> c : actions) {
                c.accept(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
