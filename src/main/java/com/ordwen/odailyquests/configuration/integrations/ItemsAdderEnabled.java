package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.essentials.UseCustomFurnaceResults;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class ItemsAdderEnabled {

    private final ConfigurationFiles configurationFiles;

    public ItemsAdderEnabled(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static boolean isEnabled;
    private static boolean isLoaded;

    public static boolean isEnabled() {
        return isEnabled;
    }
    public static boolean isLoaded() {
        return isEnabled && isLoaded;
    }

    public static void setLoaded(boolean isLoaded) {
        ItemsAdderEnabled.isLoaded = isLoaded;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void loadItemsAdderEnabled() {
        final String path = "use_itemsadder";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
        if (isEnabled() && !Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            PluginLogger.warn("ItemsAdder is not installed on the server but the option is enabled in the config.");
            PluginLogger.warn("Disabling 'use_itemsadder' option, otherwise quests will not load.");
            isEnabled = false;
        }
        if (isEnabled) UseCustomFurnaceResults.setEnabled(true);
    }
}
