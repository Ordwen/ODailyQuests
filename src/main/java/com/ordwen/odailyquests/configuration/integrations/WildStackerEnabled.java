package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class WildStackerEnabled {

    private static boolean isEnabled;
    private final ConfigurationFiles configurationFiles;

    public WildStackerEnabled(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    private static void setEnabled(final boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void loadWildStackerEnabled() {
        final String path = "use_wildstacker";
        setEnabled(configurationFiles.getConfigFile().getBoolean(path));

        if (isEnabled && Bukkit.getPluginManager().getPlugin("WildStacker") == null) {
            PluginLogger.error("WildStacker is enabled in the config but the plugin is not installed.");
            PluginLogger.error("Disabling WildStacker integration, otherwise KILL quests will not work properly.");
            setEnabled(false);
        }
    }
}
