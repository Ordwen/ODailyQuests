package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class SpawnerProgression implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public SpawnerProgression(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private boolean disabled = false;

    @Override
    public void load() {
        final String path = "disable_spawners_progression";
        disabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static SpawnerProgression getInstance() {
        return ConfigFactory.getConfig(SpawnerProgression.class);
    }

    public static boolean isSpawnersProgressionDisabled() {
        return getInstance().disabled;
    }
}
