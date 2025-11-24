package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.base.APluginFile;
import com.ordwen.odailyquests.tools.PluginLogger;

public class ConfigurationFile extends APluginFile<ODailyQuests> {

    public ConfigurationFile(ODailyQuests plugin) {
        super(plugin, "config.yml");
    }

    @Override
    protected void onLoadError(Exception e, boolean fileJustCreated) {
        PluginLogger.error("An error occurred while loading the configuration file.");
        PluginLogger.error(e.getMessage());
    }

    @Override
    protected void onPostLoad(boolean fileJustCreated) {
        if (fileJustCreated) {
            PluginLogger.info("Configuration file created.");
        }
        PluginLogger.fine("Configuration file successfully loaded.");
    }
}
