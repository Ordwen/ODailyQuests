package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Debugger {

    private final ConfigurationFiles configurationFiles;
    private static boolean debugMode;

    public Debugger(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public void loadDebugMode() {
        debugMode = configurationFiles.getConfigFile().getBoolean("debug_mode");
    }

    public static boolean isDebugMode() {
        return debugMode;
    }
}
