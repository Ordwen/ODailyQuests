package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class HologramsFile {

    /**
     * Getting instance of main class.
     */
    private final ODailyQuests oDailyQuests;

    /**
     * Main class instance constructor.
     * @param oDailyQuests main class.
     */
    public HologramsFile(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    private File hologramsFile;
    private FileConfiguration holograms;

    /**
     * Get the configuration file.
     * @return config file.
     */
    public FileConfiguration getHologramsFileConfiguration() {
        return this.holograms;
    }

    /**
     * Get the file.
     * @return file.
     */
    public File getHologramsFile() { return this.hologramsFile; }

    /**
     * Init progression file.
     */
    public void loadHologramsFile() {
        hologramsFile = new File(oDailyQuests.getDataFolder(), "holograms.yml");

        if (!hologramsFile.exists()) {
            oDailyQuests.saveResource("holograms.yml", false);
            PluginLogger.info(ChatColor.GREEN + "Holograms file created (YAML).");
        }

        holograms = new YamlConfiguration();

        try {
            holograms.load(hologramsFile);
        } catch (InvalidConfigurationException | IOException e) {
            PluginLogger.info(ChatColor.RED + "An error occurred on the load of the holograms file.");
            PluginLogger.info(ChatColor.RED + "Please inform the developer.");
            e.printStackTrace();
        }
        PluginLogger.info(ChatColor.GREEN + "Holograms file successfully loaded (YAML).");
    }

}
