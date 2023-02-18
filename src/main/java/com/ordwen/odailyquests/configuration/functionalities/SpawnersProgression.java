package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class SpawnersProgression {

    private final ConfigurationFiles configurationFiles;

    public SpawnersProgression(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static boolean disabled = false;

    public void loadSpawnersProgression() {
        final String path = "disable_spawners_progression";
        disabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static boolean isSpawnersProgressionDisabled() {
        return disabled;
    }
}
