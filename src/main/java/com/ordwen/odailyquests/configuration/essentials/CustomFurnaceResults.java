package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;

public class CustomFurnaceResults implements IConfigurable {

    private boolean isEnabled;

    private final ConfigurationFile configurationFile;

    public CustomFurnaceResults(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final String path = "use_custom_furnace_results";
        isEnabled = configurationFile.getConfigFile().getBoolean(path);
    }

    private static CustomFurnaceResults getInstance() {
        return ConfigFactory.getConfig(CustomFurnaceResults.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }

    public static void setEnabled(boolean isEnabled) {
        getInstance().isEnabled = isEnabled;
    }
}
