package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class AutoUpdater {

    private final ODailyQuests plugin;

    public AutoUpdater(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdate() {
        final String configVersion = plugin.getConfig().getString("version");
        final String currentVersion = plugin.getDescription().getVersion();

        if (configVersion == null | !configVersion.equals(currentVersion)) {
            PluginLogger.warn("It looks like you were using an older version of the plugin. Let's update your files!");

            // --------------
            // 2.1.0 -> 2.1.1
            // --------------

            // CONFIG

            final FileConfiguration configFile = new YamlConfiguration();
            final File file = new File(plugin.getDataFolder(), "config.yml");

            try {
                configFile.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }

            // Add use_itemsadder: false to config

            if (!configFile.contains("use_itemsadder")) {
                AddDefault.addDefaultConfigItem("use_itemsadder", "false", configFile, file);
                PluginLogger.warn("ItemsAdder support has been added to the config file.");
            }

            // Add progression message to config

            if (!configFile.contains("progression_message")) {
                AddDefault.addDefaultConfigItem("progression_message.enabled", true, configFile, file);
                AddDefault.addDefaultConfigItem("progression_message.message", "&a%player% &7has progressed in the quest &a%questName% &7(%progression%/%required%)", configFile, file);
                AddDefault.addDefaultConfigItem("progression_message.type", "ACTIONBAR", configFile, file);
                PluginLogger.warn("Progression message has been added to the config file.");
            }

            // --------------

            PluginLogger.fine("All files have been updated!");
        }
    }
}
