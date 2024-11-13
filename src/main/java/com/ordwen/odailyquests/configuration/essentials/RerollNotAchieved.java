package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class RerollNotAchieved {

    private static boolean rerollIfNotAchieved;
    private final ConfigurationFiles configurationFiles;

    public RerollNotAchieved(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public static boolean isRerollIfNotAchieved() {
        return rerollIfNotAchieved;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void load() {
        final String path = "reroll_only_if_not_achieved";
        rerollIfNotAchieved = configurationFiles.getConfigFile().getBoolean(path);
    }
}
