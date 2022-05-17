package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerInterfaceFile {
    /**
     * Getting instance of main class.
     */
    private final ODailyQuests oDailyQuests;

    /**
     * Main class instance constructor.
     * @param oDailyQuests main class.
     */
    public PlayerInterfaceFile(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    private static FileConfiguration playerInterface;

    /**
     * Get the player interface file.
     * @return player interface file.
     */
    public static FileConfiguration getPlayerInterfaceFileConfiguration() {
        return playerInterface;
    }

    /**
     * Init progression file.
     */
    public void loadPlayerInterfaceFile() {
        File playerInterfaceFile = new File(oDailyQuests.getDataFolder(), "playerInterface.yml");

        if (!playerInterfaceFile.exists()) {
            oDailyQuests.saveResource("playerInterface.yml", false);
            PluginLogger.info(ChatColor.GREEN + "Player interface file created (YAML).");
        }

        playerInterface = new YamlConfiguration();

        try {
            playerInterface.load(playerInterfaceFile);
        } catch (InvalidConfigurationException | IOException e) {
            PluginLogger.info(ChatColor.RED + "An error occurred on the load of the player interface file.");
            PluginLogger.info(ChatColor.RED + "Please inform the developer.");
            e.printStackTrace();
        }
        PluginLogger.info(ChatColor.GREEN + "Player interface file successfully loaded (YAML).");
    }
}

