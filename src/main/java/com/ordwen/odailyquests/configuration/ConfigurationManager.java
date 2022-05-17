package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.configuration.functionalities.Actionbar;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.GlobalReward;
import com.ordwen.odailyquests.configuration.functionalities.Title;
import com.ordwen.odailyquests.configuration.integrations.NPCNames;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;

public class ConfigurationManager {

    private final ConfigurationFiles configurationFiles;

    public ConfigurationManager(ConfigurationFiles configurationFiles) {
        this.configurationFiles =  configurationFiles;
    }

    /**
     * Load all settings.
     */
    public void loadConfiguration() {
        // essentials
        new Modes(configurationFiles).loadPluginModes();
        new Temporality(configurationFiles).loadTemporalitySettings();

        // functionalities
        new Actionbar(configurationFiles).loadActionbar();
        new Title(configurationFiles).loadTitle();
        new DisabledWorlds(configurationFiles).loadDisabledWorlds();
        new GlobalReward(configurationFiles).initGlobalReward();

        // integrations
        new NPCNames(configurationFiles).loadNPCNames();

        // utils
        Synchronization.isSynchronised = configurationFiles.getConfigFile().getBoolean("synchronised_progression");
    }
}
