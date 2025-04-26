package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerInterfaceFile {

    private final ODailyQuests oDailyQuests;

    public PlayerInterfaceFile(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    private FileConfiguration config;
    private File file;

    /**
     * Init progression file.
     */
    public void loadPlayerInterfaceFile() {
        file = new File(oDailyQuests.getDataFolder(), "playerInterface.yml");

        if (!file.exists()) {
            oDailyQuests.saveResource("playerInterface.yml", false);
            PluginLogger.info("Player interface file created (YAML).");
        }

        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            PluginLogger.error("An error occurred on the load of the player interface file.");
            PluginLogger.error("Please inform the developer.");
            PluginLogger.error(e.getMessage());
        }
        PluginLogger.fine("Player interface file successfully loaded (YAML).");
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}

