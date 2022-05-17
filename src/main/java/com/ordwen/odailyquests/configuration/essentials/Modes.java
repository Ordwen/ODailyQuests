package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Modes {

    private final ConfigurationFiles configurationFiles;

    public Modes(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static int questsMode;
    private static int timestampMode;

    public void loadPluginModes() {
        questsMode = configurationFiles.getConfigFile().getInt("quests_mode");
        timestampMode = configurationFiles.getConfigFile().getInt("timestamp_mode");
    }

    /**
     * Get quests mode.
     * @return plugin mode.
     */
    public static int getQuestsMode() {
        return questsMode;
    }

    /**
     * Get timestamp mode.
     * @return plugin mode.
     */
    public static int getTimestampMode() {
        return timestampMode;
    }

}

