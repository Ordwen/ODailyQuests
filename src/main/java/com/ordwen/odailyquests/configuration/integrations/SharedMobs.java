package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class SharedMobs implements IConfigurable {

    private final ConfigurationFiles configurationFiles;
    private boolean isEnabled;

    public SharedMobs(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public void load() {
        final String path = "shared_mobs";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static SharedMobs getInstance() {
        return ConfigFactory.getConfig(SharedMobs.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }
}
