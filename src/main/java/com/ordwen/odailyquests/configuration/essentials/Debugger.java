package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Debugger {

    private final ODailyQuests oDailyQuests;
    private static boolean debugMode;
    private static File debugFile;

    public Debugger(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    public void loadDebugMode() {
        debugMode = oDailyQuests.getConfigurationFiles().getConfigFile().getBoolean("debug_mode");
        if (debugMode) {
            loadDebugFile();
            PluginLogger.warn("Debug mode is enabled. This may cause performance issues.");
        }
    }

    public void loadDebugFile() {

        debugFile = new File(oDailyQuests.getDataFolder(), "debug.yml");

        if (!debugFile.exists()) {
            oDailyQuests.saveResource("debug.yml", false);
            PluginLogger.info("Debug file created (YAML).");
        }
    }

    public static void addDebug(String debugMessage) {
        if (debugMode) {
            final Date date = new Date();

            try {
                final FileWriter writer = new FileWriter(debugFile, true);
                writer.write("[" + date + "] " + debugMessage);
                writer.write(System.lineSeparator());
                writer.close();
            } catch (IOException e) {
                PluginLogger.error("An error happened on the write of the debug file.");
                PluginLogger.error("If the problem persists, contact the developer.");
                e.printStackTrace();
            }
        }
    }
}
