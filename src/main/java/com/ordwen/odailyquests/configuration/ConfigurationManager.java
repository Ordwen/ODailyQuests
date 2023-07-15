package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.*;
import com.ordwen.odailyquests.configuration.functionalities.*;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.NPCNames;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.AddDefault;
import com.ordwen.odailyquests.tools.TimeRemain;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

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
        final FileConfiguration configFile = configurationFiles.getConfigFile();
        final File file = configurationFiles.getFile();

        // essentials
        new Modes(configurationFiles).loadPluginModes();
        new Temporality(configurationFiles).loadTemporalitySettings();
        new TimeRemain().setupInitials();
        new QuestsAmount(configurationFiles).loadQuestsAmount();

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
        new ItemsAdderEnabled(configurationFiles).loadItemsAdderEnabled();

        // utils
        Synchronization.isSynchronised = configurationFiles.getConfigFile().getBoolean("synchronised_progression");

        // anti-glitch
        if (!configFile.contains("store_placed_blocks")) AddDefault.addDefaultConfigItem("store_placed_blocks", "true", configFile, file);
        if (!configFile.contains("store_broken_blocks")) AddDefault.addDefaultConfigItem("store_broken_blocks", "false", configFile, file);
        if (!configFile.contains("store_dropped_items")) AddDefault.addDefaultConfigItem("store_dropped_items", "false", configFile, file);

        final boolean storePlacedBlocks = configurationFiles.getConfigFile().getBoolean("store_placed_blocks");
        final boolean storeBrokenBlocks = configurationFiles.getConfigFile().getBoolean("store_broken_blocks");
        final boolean storeDroppedItems = configurationFiles.getConfigFile().getBoolean("store_dropped_items");

        Antiglitch.setStoreValues(storePlacedBlocks, storeBrokenBlocks, storeDroppedItems);
    }
}
