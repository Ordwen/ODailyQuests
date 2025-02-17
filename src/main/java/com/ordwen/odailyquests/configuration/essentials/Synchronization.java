package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;

public class Synchronization implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private boolean isEnabled;

    public Synchronization(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final String path = "synchronised_progression";
        isEnabled = configurationFile.getConfigFile().getBoolean(path);
    }

    private static Synchronization getInstance() {
        return ConfigFactory.getConfig(Synchronization.class);
    }

    public static boolean isSynchronised() {
        return getInstance().isEnabled;
    }
}
