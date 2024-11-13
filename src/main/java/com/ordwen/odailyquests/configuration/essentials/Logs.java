package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Logs {

    private static boolean isEnabled;
    private final ConfigurationFiles configurationFiles;

    public Logs(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Check if the logs are enabled.
     *
     * @return true if enabled.
     */
    public static boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Check if the logs are disabled.
     */
    public void loadLogs() {
        isEnabled = !configurationFiles.getConfigFile().getBoolean("disable_logs");
    }
}
