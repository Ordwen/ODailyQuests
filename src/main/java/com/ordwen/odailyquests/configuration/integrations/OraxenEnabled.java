package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.essentials.UseCustomFurnaceResults;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class OraxenEnabled {

    private static boolean isEnabled;
    private static boolean isLoaded;
    private final ConfigurationFiles configurationFiles;
    public OraxenEnabled(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static boolean isLoaded() {
        return isEnabled && isLoaded;
    }

    public static void setLoaded(final boolean isLoaded) {
        OraxenEnabled.isLoaded = isLoaded;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void loadOraxenEnabled() {
        final String path = "use_oraxen";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
        if (isEnabled && !Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            PluginLogger.warn("Oraxen is not installed on the server but the option is enabled in the config.");
            PluginLogger.warn("Disabling 'use_oraxen' option, otherwise quests will not load.");
            isEnabled = false;
        }
        if (isEnabled) UseCustomFurnaceResults.setEnabled(true);
    }
}
