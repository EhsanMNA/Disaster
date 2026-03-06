package me.ehsanmna.disaster.util;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A utility class for logging errors to text files in Spigot plugins.
 */
public class ErrorLogger {

    private final JavaPlugin plugin;
    private final String logFileName;
    private final File logFile;
    private final DateTimeFormatter timestampFormatter;

    /**
     * Constructor for ErrorLogger
     * @param plugin Your plugin instance
     */
    public ErrorLogger(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logFileName = "errors_log.txt";
        this.timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Initialize log file
        this.logFile = new File(plugin.getDataFolder(), logFileName);
        ensureLogFileExists();
    }

    /**
     * Constructor with custom log file name
     * @param plugin Your plugin instance
     * @param logFileName Custom name for the log file
     */
    public ErrorLogger(JavaPlugin plugin, String logFileName) {
        this.plugin = plugin;
        this.logFileName = logFileName.endsWith(".txt") ? logFileName : logFileName + ".txt";
        this.timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Initialize log file
        this.logFile = new File(plugin.getDataFolder(), this.logFileName);
        ensureLogFileExists();
    }

    /**
     * Ensures the log file and parent directories exist
     */
    private void ensureLogFileExists() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            if (!logFile.exists()) {
                logFile.createNewFile();
                writeHeader();
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create error log file: " + e.getMessage());
        }
    }

    /**
     * Writes a header to the log file on first creation
     */
    private void writeHeader() {
        try (FileWriter writer = new FileWriter(logFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            printWriter.println("=".repeat(60));
            printWriter.println("Error Log - " + plugin.getName() + " v" + plugin.getDescription().getVersion());
            printWriter.println("Created: " + getCurrentTimestamp());
            printWriter.println("=".repeat(60));
            printWriter.println();

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write header to error log: " + e.getMessage());
        }
    }

    /**
     * Logs an exception with a custom message
     * @param message Custom message describing the error
     * @param exception The exception to log
     */
    public void logError(String message, Exception exception) {
        logToFile(message, exception);
        logToConsole(message, exception);
    }

    /**
     * Logs an exception with plugin prefix
     * @param exception The exception to log
     */
    public void logError(Exception exception) {
        logError("[" + plugin.getName() + "] An error occurred", exception);
    }

    /**
     * Logs a custom error message without an exception
     * @param message The error message to log
     */
    public void logError(String message) {
        try (FileWriter writer = new FileWriter(logFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            printWriter.println("[" + getCurrentTimestamp() + "] " + message);
            printWriter.println();

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write to error log: " + e.getMessage());
        }

        plugin.getLogger().severe(message);
    }

    /**
     * Logs error to the text file
     */
    private void logToFile(String message, Exception exception) {
        try (FileWriter writer = new FileWriter(logFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            printWriter.println("=".repeat(60));
            printWriter.println("[" + getCurrentTimestamp() + "] " + message);
            printWriter.println("-".repeat(40));
            printWriter.println("Exception: " + exception.getClass().getName());
            printWriter.println("Message: " + exception.getMessage());
            printWriter.println();
            printWriter.println("Stack Trace:");

            // Write stack trace
            for (StackTraceElement element : exception.getStackTrace()) {
                printWriter.println("    at " + element.toString());
            }

            // Write cause if exists
            Throwable cause = exception.getCause();
            if (cause != null) {
                printWriter.println();
                printWriter.println("Caused by: " + cause.getClass().getName());
                printWriter.println("Cause Message: " + cause.getMessage());
                for (StackTraceElement element : cause.getStackTrace()) {
                    printWriter.println("    at " + element.toString());
                }
            }

            printWriter.println("=".repeat(60));
            printWriter.println();

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write to error log: " + e.getMessage());
        }
    }

    /**
     * Logs error to console
     */
    private void logToConsole(String message, Exception exception) {
        plugin.getLogger().severe(message);
        plugin.getLogger().severe("Exception: " + exception.getClass().getName());
        plugin.getLogger().severe("Message: " + exception.getMessage());

        // Log first few lines of stack trace to console
        plugin.getLogger().severe("Stack Trace (see error_log.txt for full trace):");
        for (int i = 0; i < Math.min(5, exception.getStackTrace().length); i++) {
            plugin.getLogger().severe("    at " + exception.getStackTrace()[i].toString());
        }
    }

    /**
     * Clears the entire error log file
     * @return true if successful, false otherwise
     */
    public boolean clearLog() {
        try (PrintWriter writer = new PrintWriter(logFile)) {
            writer.print("");
            writeHeader();
            return true;
        } catch (FileNotFoundException e) {
            plugin.getLogger().severe("Failed to clear error log: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the log file as a File object
     * @return The log file
     */
    public File getLogFile() {
        return logFile;
    }

    /**
     * Gets the current timestamp formatted
     * @return Formatted timestamp string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(timestampFormatter);
    }

    /**
     * Gets the size of the log file in bytes
     * @return File size in bytes
     */
    public long getLogFileSize() {
        return logFile.length();
    }

    /**
     * Gets the size of the log file in human-readable format
     * @return Human-readable file size
     */
    public String getLogFileSizeFormatted() {
        long size = getLogFileSize();
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        else return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }

    /**
     * Reads and returns the entire log file content
     * @return Log content as string, or empty string if error occurs
     */
    public String readLog() {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to read error log: " + e.getMessage());
        }

        return content.toString();
    }

    /**
     * Gets the last N lines from the log file
     * @param lines Number of lines to retrieve
     * @return Last N lines as string
     */
    public String getLastLines(int lines) {
        try {
            RandomAccessFile file = new RandomAccessFile(logFile, "r");
            StringBuilder builder = new StringBuilder();
            long fileLength = file.length() - 1;

            file.seek(fileLength);

            for (int i = 0; i < lines; i++) {
                long currentPosition = file.getFilePointer();

                if (currentPosition == 0) {
                    break;
                }

                // Move back and read characters until newline
                while (currentPosition > 0) {
                    file.seek(--currentPosition);
                    char c = (char) file.readByte();
                    if (c == '\n') {
                        break;
                    }
                }

                if (currentPosition == 0) {
                    file.seek(0);
                }

                String line = file.readLine();
                if (line != null) {
                    builder.insert(0, line + "\n");
                }

                file.seek(currentPosition);
            }

            file.close();
            return builder.toString();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to read last lines from error log: " + e.getMessage());
            return "";
        }
    }
}