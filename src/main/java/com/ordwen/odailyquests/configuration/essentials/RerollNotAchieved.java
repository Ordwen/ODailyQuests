package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class RerollNotAchieved {

    private final ConfigurationFiles configurationFiles;
    private static boolean rerollIfNotAchieved;

    public RerollNotAchieved(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Check if WildStacker option is enabled in config.
     */
    public void load() {
        final String path = "reroll_only_if_not_achieved";
        rerollIfNotAchieved = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static boolean isRerollIfNotAchieved() {
        return rerollIfNotAchieved;
    }
}
