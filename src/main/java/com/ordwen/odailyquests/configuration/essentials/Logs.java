package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Logs {

    private final ConfigurationFiles configurationFiles;

    private static boolean isEnabled;

    public Logs(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Check if the logs are disabled.
     */
    public void loadLogs() {
        isEnabled = !configurationFiles.getConfigFile().getBoolean("disable_logs");
    }

    /**
     * Check if the logs are enabled.
     * @return true if enabled.
     */
    public static boolean isEnabled() {
        return isEnabled;
    }
}
