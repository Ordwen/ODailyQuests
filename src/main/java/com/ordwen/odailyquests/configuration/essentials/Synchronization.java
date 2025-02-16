package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Synchronization implements IConfigurable {

    private final ConfigurationFiles configurationFiles;
    private boolean isEnabled;

    public Synchronization(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public void load() {
        final String path = "synchronised_progression";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static Synchronization getInstance() {
        return ConfigFactory.getConfig(Synchronization.class);
    }

    public static boolean isSynchronised() {
        return getInstance().isEnabled;
    }
}
