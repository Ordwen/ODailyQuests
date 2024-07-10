package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Synchronization {

    private final ConfigurationFiles configurationFiles;
    private static boolean isEnabled;

    public Synchronization(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void load() {
        final String path = "synchronised_progression";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static boolean isSynchronised() {
        return isEnabled;
    }
}
