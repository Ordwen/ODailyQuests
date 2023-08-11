package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class UseCustomFurnaceResults {

    private static boolean isEnabled;

    private final ConfigurationFiles configurationFiles;

    public UseCustomFurnaceResults(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public void loadUseCustomFurnaceResults() {
        final String path = "use_custom_furnace_results";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
    }
}
