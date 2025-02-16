package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Logs implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    private boolean isEnabled;

    public Logs(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public void load() {
        isEnabled = !configurationFiles.getConfigFile().getBoolean("disable_logs");
    }

    public boolean isEnabledInternal() {
        return isEnabled;
    }

    public static Logs getInstance() {
        return ConfigFactory.getConfig(Logs.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabledInternal();
    }
}
