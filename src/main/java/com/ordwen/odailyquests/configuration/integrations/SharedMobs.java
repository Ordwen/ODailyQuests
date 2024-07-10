package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class SharedMobs {

    private final ConfigurationFiles configurationFiles;

    public SharedMobs(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }


    private static boolean isEnabled;

    public static boolean isEnabled() {
        return isEnabled;
    }

    private static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void load() {
        final String path = "shared_mobs";
        setEnabled(configurationFiles.getConfigFile().getBoolean(path));
    }
}
