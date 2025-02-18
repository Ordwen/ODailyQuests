package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;

public class Logs implements IConfigurable {

    private final ConfigurationFile configurationFile;

    private boolean isEnabled;

    public Logs(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        isEnabled = !configurationFile.getConfig().getBoolean("disable_logs");
    }

    public boolean isEnabledInternal() {
        return isEnabled;
    }

    private static Logs getInstance() {
        return ConfigFactory.getConfig(Logs.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabledInternal();
    }
}
