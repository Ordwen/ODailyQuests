package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.configuration.functions.Actionbar;
import com.ordwen.odailyquests.configuration.functions.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functions.GlobalReward;
import com.ordwen.odailyquests.configuration.functions.Title;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class ConfigurationManager {

    private final ConfigurationFiles configurationFiles;

    public ConfigurationManager(ConfigurationFiles configurationFiles) {
        this.configurationFiles =  configurationFiles;
    }

    /**
     * Load all settings.
     */
    public void loadConfiguration() {
        new Actionbar(configurationFiles).loadActionbar();
        new Title(configurationFiles).loadTitle();
        new DisabledWorlds(configurationFiles).loadDisabledWorlds();
        new GlobalReward(configurationFiles).initGlobalReward();
    }
}
