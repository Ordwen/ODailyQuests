package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DebugFile {

    private final ODailyQuests oDailyQuests;

    public DebugFile(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    private static File debugFile;

    public static File getDebugFile() { return debugFile; }

    public void loadDebugFile() {

        debugFile = new File(oDailyQuests.getDataFolder(), "debug.yml");

        if (!debugFile.exists()) {
            oDailyQuests.saveResource("debug.yml", false);
        }
    }

    public static void addDebug(String debugMessage) {
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
