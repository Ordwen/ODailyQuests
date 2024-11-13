package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Synchronization {

    private static boolean isEnabled;
    private final ConfigurationFiles configurationFiles;

    public Synchronization(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public static boolean isSynchronised() {
        return isEnabled;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void load() {
        final String path = "synchronised_progression";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
    }
}
