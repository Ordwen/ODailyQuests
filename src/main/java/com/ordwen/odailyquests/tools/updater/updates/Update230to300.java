package com.ordwen.odailyquests.tools.updater.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.ConfigUpdater;

import java.io.IOException;

public class Update230to300 extends ConfigUpdater {

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

        updateVersion(version);
    }
}
