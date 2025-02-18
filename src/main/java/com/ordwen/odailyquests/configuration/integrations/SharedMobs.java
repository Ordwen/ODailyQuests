package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;

public class SharedMobs implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private boolean isEnabled;

    public SharedMobs(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final String path = "shared_mobs";
        isEnabled = configurationFile.getConfig().getBoolean(path);
    }

    private static SharedMobs getInstance() {
        return ConfigFactory.getConfig(SharedMobs.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }
}
