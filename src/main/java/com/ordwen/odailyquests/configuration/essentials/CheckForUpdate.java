package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;

public class CheckForUpdate implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private boolean enabled = true;

    public CheckForUpdate(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        enabled = configurationFile.getConfig().getBoolean("check_for_update", true);
    }

    private static CheckForUpdate getInstance() {
        return ConfigFactory.getConfig(CheckForUpdate.class);
    }

    public static boolean isCheckForUpdate() {
        return getInstance().enabled;
    }
}
