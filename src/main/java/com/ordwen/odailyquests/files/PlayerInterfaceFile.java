package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerInterfaceFile {
    private static FileConfiguration playerInterface;
    private static File playerInterfaceFile;
    /**
     * Getting instance of main class.
     */
    private final ODailyQuests oDailyQuests;
    /**
     * Main class instance constructor.
     *
     * @param oDailyQuests main class.
     */
    public PlayerInterfaceFile(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Get the player interface file configuration.
     *
     * @return player interface file config
     */
    public static FileConfiguration getPlayerInterfaceFileConfiguration() {
        return playerInterface;
    }

    /**
     * Get the player interface file.
     *
     * @return player interface file
     */
    public static File getPlayerInterfaceFile() {
        return playerInterfaceFile;
    }

    /**
     * Init progression file.
     */
    public void loadPlayerInterfaceFile() {
        playerInterfaceFile = new File(oDailyQuests.getDataFolder(), "playerInterface.yml");

        if (!playerInterfaceFile.exists()) {
            oDailyQuests.saveResource("playerInterface.yml", false);
            PluginLogger.warn("Player interface file created (YAML).");
        }

        playerInterface = new YamlConfiguration();

        try {
            playerInterface.load(playerInterfaceFile);
        } catch (InvalidConfigurationException | IOException e) {
            PluginLogger.error("An error occurred on the load of the player interface file.");
            PluginLogger.error("Please inform the developer.");
            PluginLogger.error(e.getMessage());
        }
        PluginLogger.fine("Player interface file successfully loaded (YAML).");
    }
}

