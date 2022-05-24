package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.AddDefault;

public class SpawnersProgression {

    private final ConfigurationFiles configurationFiles;

    public SpawnersProgression(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static boolean disabled = false;

    public void loadSpawnersProgression() {
        String path = "disable_spawners_progression";
        if (configurationFiles.getConfigFile().contains(path)) {
            disabled = configurationFiles.getConfigFile().getBoolean(path);
        } else AddDefault.addDefaultConfigItem(path, false, configurationFiles.getConfigFile(), configurationFiles.getFile());
    }

    public static boolean isSpawnersProgressionDisabled() {
        return disabled;
    }
}
