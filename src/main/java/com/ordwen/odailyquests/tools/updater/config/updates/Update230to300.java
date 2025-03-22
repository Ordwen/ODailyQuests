package com.ordwen.odailyquests.tools.updater.config.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.config.ConfigUpdater;

import java.io.IOException;

public class Update230to300 extends ConfigUpdater {

    private static final String TEMPORALITY_MODE = "temporality_mode";
    private static final String RENEW_INTERVAL = "renew_interval";

    public Update230to300(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {

        if (config.getString("storage_mode").equalsIgnoreCase("h2")) {
            config.set("storage_mode", "SQLite");

            try {
                config.save(configFile);
                PluginLogger.warn("For stability reasons, the storage mode has been changed from H2 to SQLite.");
                PluginLogger.warn("If you wish to migrate your data to SQLite, please use the converter (more information on the version changelog).");
            } catch (IOException e) {
                PluginLogger.error("Error while saving the configuration file.");
                PluginLogger.error(e.getMessage());
            }
        }

        setDefaultConfigItem("join_message_delay", 1.0, config, configFile);
        setDefaultConfigItem("use_nexo", false, config, configFile);
        setDefaultConfigItem("renew_time", "00:00", config, configFile);
        replaceTemporalityMode();

        updateVersion(version);
    }

    private void replaceTemporalityMode() {
        final int currentMode = config.getInt(TEMPORALITY_MODE);
        switch (currentMode) {
            case 2 -> setDefaultConfigItem(RENEW_INTERVAL, "7d", config, configFile);
            case 3 -> setDefaultConfigItem(RENEW_INTERVAL, "30d", config, configFile);
            default -> setDefaultConfigItem(RENEW_INTERVAL, "1d", config, configFile);
        }

        removeConfigItem(TEMPORALITY_MODE, config, configFile);
        parameterReplaced(TEMPORALITY_MODE, RENEW_INTERVAL);
    }
}
