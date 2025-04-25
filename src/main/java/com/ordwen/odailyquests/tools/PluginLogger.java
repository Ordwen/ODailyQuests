package com.ordwen.odailyquests.tools;

import java.util.logging.Logger;

/**
 * Utility class for logging messages in the plugin.
 * <p>
 * This class provides various logging methods for different levels of logging, including information, warnings, errors, and fine-level logs.
 * The logs are directed to the console and are prefixed with the plugin's name "O'DailyQuests".
 * </p>
 */
public class PluginLogger {

    private PluginLogger() {}

    private static final Logger logger = Logger.getLogger("O'DailyQuests");

    /**
     * Logs an informational message to the console.
     *
     * @param msg the message to log.
     */
    public static void info(String msg) {
        logger.info(msg);
    }

    /**
     * Logs a warning message to the console.
     *
     * @param msg the message to log.
     */
    public static void warn(String msg) {
        logger.warning(msg);
    }

    /**
     * Logs an error message to the console.
     *
     * @param msg the message to log.
     */
    public static void error(String msg) {
        logger.severe(msg);
    }

    /**
     * Logs a fine-level message to the console, typically for debugging purposes.
     *
     * @param msg the message to log.
     */
    public static void fine(String msg) {
        logger.fine(msg);
    }

    /**
     * Displays an error message in the console when a quest cannot be loaded due to a configuration error.
     * <p>
     * This method formats and logs the details of the error, including the file name, quest index, the problematic parameter, and the reason for the error.
     * </p>
     *
     * @param fileName   the name of the file where the error occurred
     * @param questIndex the index of the quest in the file
     * @param parameter  the parameter that caused the error (can be {@code null} if not applicable)
     * @param reason     the reason for the error
     */
    public static void configurationError(String fileName, String questIndex, String parameter, String reason) {
        PluginLogger.error("-----------------------------------");
        PluginLogger.error("Invalid quest configuration detected.");
        PluginLogger.error("File : " + fileName);
        PluginLogger.error("Quest number : " + questIndex);
        PluginLogger.error("Reason : " + reason);

        if (parameter != null) {
            PluginLogger.error("Parameter : " + parameter);
        }

        PluginLogger.error("-----------------------------------");
    }
}