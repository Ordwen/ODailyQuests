package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.base.APluginFile;
import com.ordwen.odailyquests.tools.PluginLogger;

public class ProgressionFile extends APluginFile<ODailyQuests> {

    public ProgressionFile(ODailyQuests plugin) {
        super(plugin, "progression.yml");
    }

    @Override
    protected void onLoadError(Exception e, boolean fileJustCreated) {
        PluginLogger.error("An error occurred while loading the progression file.");
        PluginLogger.error(e.getMessage());
    }

    @Override
    protected void onPostLoad(boolean fileJustCreated) {
        if (fileJustCreated) {
            PluginLogger.info("Progression file created.");
        }
        PluginLogger.fine("Progression file successfully loaded (YAML).");
    }
}
