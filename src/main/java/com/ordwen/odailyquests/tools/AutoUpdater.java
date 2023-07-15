package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.files.QuestsFiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

            // Add use_itemsadder: false to config
            final FileConfiguration configFile = plugin.getConfig();
            final File file = new File(plugin.getDataFolder(), "config.yml");

            if (!configFile.contains("use_itemsadder")) {
                AddDefault.addDefaultConfigItem("use_itemsadder", "false", configFile, file);
            }

            // --------------

            PluginLogger.fine("All files have been updated!");
        }
    }
}
