package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class WildStackerEnabled {

    private final ConfigurationFiles configurationFiles;

    public WildStackerEnabled(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static boolean isEnabled;

    public static boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void loadWildStackerEnabled() {
        final String path = "use_wildstacker";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);

        if (Bukkit.getPluginManager().getPlugin("WildStacker") == null) {
            PluginLogger.error("WildStacker is enabled in the config but the plugin is not installed.");
            PluginLogger.error("Disabling WildStacker integration, otherwise KILL quests will not work properly.");
            isEnabled = false;
        }
    }
}
