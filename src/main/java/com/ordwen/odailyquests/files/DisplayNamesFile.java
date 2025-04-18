package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class DisplayNamesFile {

    private final ODailyQuests oDailyQuests;

    public DisplayNamesFile(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    private FileConfiguration config;
    private File file;

    /**
     * Init display names file.
     */
    public void loadDisplayNamesFile() {
        file = new File(oDailyQuests.getDataFolder(), "displayNames.yml");

        if (!file.exists()) {
            oDailyQuests.saveResource("displayNames.yml", false);
            oDailyQuests.getLogger().info("Display names file created.");
        }

        config = oDailyQuests.getConfig();

        try {
            config.load(file);
        } catch (Exception e) {
            oDailyQuests.getLogger().severe("An error occurred while loading the display names file.");
            oDailyQuests.getLogger().severe("Please inform the developer.");
            oDailyQuests.getLogger().severe(e.getMessage());
        }
        oDailyQuests.getLogger().fine("Display names file successfully loaded.");
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
