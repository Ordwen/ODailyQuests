package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;

public class SafetyMode implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private boolean safetyModeEnabled;

    public SafetyMode(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        safetyModeEnabled = configurationFile.getConfig().getBoolean("safety_mode", true);
        Debugger.write("Safety mode is enabled. Plugin will try to prevent crashes.");
    }

    public boolean isSafetyModeEnabledInternal() {
        return safetyModeEnabled;
    }

    private static SafetyMode getInstance() {
        return ConfigFactory.getConfig(SafetyMode.class);
    }

    public static boolean isSafetyModeEnabled() {
        return getInstance().isSafetyModeEnabledInternal();
    }
}
