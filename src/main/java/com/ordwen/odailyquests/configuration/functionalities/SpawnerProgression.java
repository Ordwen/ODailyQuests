package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;

public class SpawnerProgression implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public SpawnerProgression(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean disabled = false;

    @Override
    public void load() {
        final String path = "disable_spawners_progression";
        disabled = configurationFile.getConfig().getBoolean(path);
    }

    private static SpawnerProgression getInstance() {
        return ConfigFactory.getConfig(SpawnerProgression.class);
    }

    public static boolean isSpawnersProgressionDisabled() {
        return getInstance().disabled;
    }
}
