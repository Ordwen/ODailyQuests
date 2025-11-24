package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.base.APluginFile;
import com.ordwen.odailyquests.tools.PluginLogger;

public class TotalRewardsFile extends APluginFile<ODailyQuests> {

    public TotalRewardsFile(ODailyQuests plugin) {
        super(plugin, "totalRewards.yml");
    }

    @Override
    protected void onLoadError(Exception e, boolean fileJustCreated) {
        PluginLogger.error("An error occurred while loading the total rewards file.");
        PluginLogger.error(e.getMessage());
    }

    @Override
    protected void onPostLoad(boolean fileJustCreated) {
        if (fileJustCreated) {
            PluginLogger.info("Total rewards file created.");
        }
        PluginLogger.fine("Total rewards file successfully loaded.");
    }
}
