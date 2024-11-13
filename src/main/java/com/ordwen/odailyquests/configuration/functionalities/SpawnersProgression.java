package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class SpawnersProgression {

    private static boolean disabled = false;
    private final ConfigurationFiles configurationFiles;

    public SpawnersProgression(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public static boolean isSpawnersProgressionDisabled() {
        return disabled;
    }

    public void loadSpawnersProgression() {
        final String path = "disable_spawners_progression";
        disabled = configurationFiles.getConfigFile().getBoolean(path);
    }
}
