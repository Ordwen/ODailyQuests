package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.*;
import com.ordwen.odailyquests.configuration.functionalities.*;
import com.ordwen.odailyquests.configuration.functionalities.progression.ActionBar;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressionMessage;
import com.ordwen.odailyquests.configuration.functionalities.progression.Title;
import com.ordwen.odailyquests.configuration.functionalities.rewards.CategoriesRewards;
import com.ordwen.odailyquests.configuration.functionalities.rewards.GlobalReward;
import com.ordwen.odailyquests.configuration.integrations.*;
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
        new Commands(configurationFiles).loadPluginCommands();
        new Temporality(configurationFiles).loadTemporalitySettings();
        new TimeRemain().setupInitials();
        new QuestsAmount(configurationFiles).loadQuestsAmount();
        new UseCustomFurnaceResults(configurationFiles).loadUseCustomFurnaceResults();
        new Logs(configurationFiles).loadLogs();

        // functionalities
        new ActionBar(configurationFiles).loadActionbar();
        new Title(configurationFiles).loadTitle();
        new DisabledWorlds(configurationFiles).loadDisabledWorlds();
        new GlobalReward(configurationFiles).initGlobalReward();
        new CategoriesRewards(configurationFiles).initCategoriesRewards();
        new SpawnersProgression(configurationFiles).loadSpawnersProgression();
        new TakeItems(configurationFiles).loadTakeItems();
        new ProgressionMessage(configurationFiles).loadProgressionMessage();
        new ProgressBar(configurationFiles).loadProgressBar();

        // integrations
        new NPCNames(configurationFiles).loadNPCNames();
        new WildStackerEnabled(configurationFiles).loadWildStackerEnabled();
        new ItemsAdderEnabled(configurationFiles).loadItemsAdderEnabled();
        new OraxenEnabled(configurationFiles).loadOraxenEnabled();
        new SharedMobs(configurationFiles).load();

        // utils
        new Synchronization(configurationFiles).load();
        new RerollNotAchieved(configurationFiles).load();

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
