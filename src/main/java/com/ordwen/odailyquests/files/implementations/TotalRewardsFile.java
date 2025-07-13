package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.APluginFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class TotalRewardsFile extends APluginFile {

    public TotalRewardsFile(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        file = new File(plugin.getDataFolder(), "totalRewards.yml");

        if (!file.exists()) {
            plugin.saveResource("totalRewards.yml", false);
            PluginLogger.info("Total rewards file created.");
        }

        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            PluginLogger.error("An error occurred while loading the total rewards file.");
            PluginLogger.error(e.getMessage());        }

        PluginLogger.info("Total rewards file successfully loaded.");
    }
}