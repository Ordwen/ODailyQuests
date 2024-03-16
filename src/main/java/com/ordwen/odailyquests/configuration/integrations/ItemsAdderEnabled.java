package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.essentials.UseCustomFurnaceResults;
import com.ordwen.odailyquests.files.ConfigurationFiles;

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
        if (isEnabled()) UseCustomFurnaceResults.setEnabled(true);
    }
}
