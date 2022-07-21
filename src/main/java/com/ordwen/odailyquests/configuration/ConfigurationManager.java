package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.configuration.functionalities.*;
import com.ordwen.odailyquests.configuration.integrations.NPCNames;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.Material;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;

public class ConfigurationManager {

    private final ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public ConfigurationManager(ODailyQuests oDailyQuests) {
        this.configurationFiles =  oDailyQuests.getConfigurationFiles();
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
        new SpawnersProgression(configurationFiles).loadSpawnersProgression();
        new TakeItems(configurationFiles).loadTakeItems();

        // integrations
        new NPCNames(configurationFiles).loadNPCNames();
        new WildStackerEnabled(configurationFiles).loadWildStackerEnabled();

        // utils
        Synchronization.isSynchronised = configurationFiles.getConfigFile().getBoolean("synchronised_progression");
    }
}
