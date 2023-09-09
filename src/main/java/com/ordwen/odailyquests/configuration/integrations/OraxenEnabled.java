package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.essentials.UseCustomFurnaceResults;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class OraxenEnabled {

    private final ConfigurationFiles configurationFiles;

    public OraxenEnabled(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }


    private static boolean isEnabled;
    private static boolean isLoaded;

    public static boolean isEnabled() {
        return isEnabled;
    }
    public static boolean isLoaded() {
        return isLoaded;
    }

    public static void setLoaded(boolean isLoaded) {
        OraxenEnabled.isLoaded = isLoaded;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void loadOraxenEnabled() {
        final String path = "use_oraxen";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
        if (isEnabled()) UseCustomFurnaceResults.setEnabled(true);
    }

}
