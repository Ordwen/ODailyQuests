package com.ordwen.odailyquests.tools;

import java.util.logging.Logger;

public class PluginLogger {

    private static final Logger logger = org.bukkit.plugin.PluginLogger.getLogger("O'DailyQuests");

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warn(String msg) {
        logger.warning(msg);
    }

    public static void error(String msg) {
        logger.severe(msg);
    }

    public static void fine(String msg) {
        logger.fine(msg);
    }

    /**
     * Display an error message in the console when a quest cannot be loaded because of a configuration error.
     *
     * @param fileName   the name of the file where the error occurred
     * @param questIndex the index of the quest in the file
     * @param parameter  the parameter that caused the error
     * @param reason     the reason of the error
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
