package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Debugger implements IConfigurable {

    private final ConfigurationFiles configurationFiles;
    private boolean debugMode;
    private File debugFile;

    public Debugger(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public void load() {
        debugMode = configurationFiles.getConfigFile().getBoolean("debug");
        if (debugMode) {
            loadDebugFile();
            PluginLogger.warn("Debug mode is enabled. This may cause performance issues.");
        }
    }

    public void loadDebugFile() {
        debugFile = new File(ODailyQuests.INSTANCE.getDataFolder(), "debug.yml");

        if (!debugFile.exists()) {
            ODailyQuests.INSTANCE.saveResource("debug.yml", false);
            PluginLogger.info("Debug file created (YAML).");
        }
    }

    public void writeInternal(String debugMessage) {
        if (debugMode) {
            final Date date = new Date();

            try (FileWriter writer = new FileWriter(debugFile, true)) {
                writer.write("[" + date + "] " + debugMessage);
                writer.write(System.lineSeparator());
            } catch (IOException e) {
                PluginLogger.error("An error happened on the write of the debug file.");
                PluginLogger.error("If the problem persists, contact the developer.");
                PluginLogger.error(e.getMessage());
            }
        }
    }

    private static Debugger getInstance() {
        return ConfigFactory.getConfig(Debugger.class);
    }

    public static void write(String debugMessage) {
        getInstance().writeInternal(debugMessage);
    }
}
