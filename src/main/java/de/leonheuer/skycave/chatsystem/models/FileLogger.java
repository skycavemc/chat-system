package de.leonheuer.skycave.chatsystem.models;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logger class that allows to log strings to a file.
 * Allows customized actions after the string has been logged.
 */
public class FileLogger {

    private String format;
    private File logFile;

    /**
     * Creates a new logger instance.
     * @param logFile The file to log to
     */
    public FileLogger(@NotNull File logFile) {
        this.format = "[%timestamp] %output";
        this.logFile = logFile;
    }

    /**
     * Creates a new logger instance with a custom format.
     * Placeholders for the format: %timestamp, %output
     * @param format The logging format
     * @param logFile The file to log to
     */
    public FileLogger(@NotNull String format, @NotNull File logFile) {
        this.format = format;
        this.logFile = logFile;
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
     * Logs the given output string to the log file.
     * @param output The output string to log
     */
    public void log(@NotNull String output) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        output = format.replaceFirst("%output", output);
        output = output.replaceFirst("%timestamp", formatter.format(LocalDateTime.now()));
        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the given output string to the log file.
     * Inserts a new line at the end of the log file.
     * @param output The output string to log
     */
    public void logln(@NotNull String output) {
        log(output + System.getProperty("line.separator"));
    }
}
