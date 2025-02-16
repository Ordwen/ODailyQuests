package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class CustomFurnaceResults implements IConfigurable {

    private boolean isEnabled;

    private final ConfigurationFiles configurationFiles;

    public CustomFurnaceResults(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public void load() {
        final String path = "use_custom_furnace_results";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static CustomFurnaceResults getInstance() {
        return ConfigFactory.getConfig(CustomFurnaceResults.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }

    public static void setEnabled(boolean isEnabled) {
        getInstance().isEnabled = isEnabled;
    }
}
