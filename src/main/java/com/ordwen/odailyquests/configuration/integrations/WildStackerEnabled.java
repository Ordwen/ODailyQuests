package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.files.ConfigurationFiles;

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
    }
}
