package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.base.APluginFile;
import com.ordwen.odailyquests.tools.PluginLogger;

public class PlayerInterfaceFile extends APluginFile<ODailyQuests> {

    public PlayerInterfaceFile(ODailyQuests plugin) {
        super(plugin, "playerInterface.yml");
    }

    @Override
    protected void onLoadError(Exception e, boolean fileJustCreated) {
        PluginLogger.error("An error occurred while loading the player interface file.");
        PluginLogger.error(e.getMessage());
    }

    @Override
    protected void onPostLoad(boolean fileJustCreated) {
        if (fileJustCreated) {
            PluginLogger.info("Player interface file created.");
        }
        PluginLogger.fine("Player interface file successfully loaded.");
    }
}
