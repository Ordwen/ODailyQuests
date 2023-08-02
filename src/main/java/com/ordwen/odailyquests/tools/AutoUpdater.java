package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AutoUpdater {

    private final ODailyQuests plugin;

    public AutoUpdater(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdate() {

        final FileConfiguration configFile = new YamlConfiguration();
        final File file = new File(plugin.getDataFolder(), "config.yml");

        try {
            configFile.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        final String configVersion = configFile.getString("version");
        if (configVersion == null) {
            PluginLogger.error("The 'version' field is missing from the config file. The auto updater cannot work without it.");
            return;
        }

        final String currentVersion = plugin.getDescription().getVersion();

        if (!configVersion.equals(currentVersion)) {
            PluginLogger.warn("It looks like you were using an older version of the plugin. Let's update your files!");

            // --------------
            // 2.1.0 -> 2.2.0
            // --------------

            // CONFIG

            // Add use_itemsadder: false to config

            if (!configFile.contains("use_itemsadder")) {
                AddDefault.addDefaultConfigItem("use_itemsadder", false, configFile, file);
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
            // 2.2.1 -> 2.2.2
            // --------------

            // CONFIG

            if (!configFile.contains("categories_rewards")) {
                // easy
                AddDefault.addDefaultConfigItem("categories_rewards.easy.enabled", true, configFile, file);
                AddDefault.addDefaultConfigItem("categories_rewards.easy.reward_type", "COMMAND", configFile, file);
                AddDefault.addDefaultConfigItem("categories_rewards.easy.commands", List.of("give %player% diamond 16"), configFile, file);

                // medium
                AddDefault.addDefaultConfigItem("categories_rewards.medium.enabled", true, configFile, file);
                AddDefault.addDefaultConfigItem("categories_rewards.medium.reward_type", "COMMAND", configFile, file);
                AddDefault.addDefaultConfigItem("categories_rewards.medium.commands", List.of("give %player% diamond 32"), configFile, file);

                // hard
                AddDefault.addDefaultConfigItem("categories_rewards.hard.enabled", true, configFile, file);
                AddDefault.addDefaultConfigItem("categories_rewards.hard.reward_type", "COMMAND", configFile, file);
                AddDefault.addDefaultConfigItem("categories_rewards.hard.commands", List.of("give %player% diamond 64"), configFile, file);
            }

            PluginLogger.fine("All files have been updated!");
        }
    }
}
