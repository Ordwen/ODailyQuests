package com.ordwen.odailyquests.tools.updater.config.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.config.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;

public class Update230to300 extends ConfigUpdater {

    private static final String TEMPORALITY_MODE = "temporality_mode";
    private static final String RENEW_INTERVAL = "renew_interval";

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

        setDefaultConfigItem("join_message_delay", 1.0, config, configFile);
        setDefaultConfigItem("use_nexo", false, config, configFile);
        setDefaultConfigItem("renew_time", "00:00", config, configFile);
        setDefaultConfigItem("check_for_update", true, config, configFile);
        setDefaultConfigItem("player_data_load_delay", 0.5, config, configFile);
        setDefaultConfigItem("use_rosestacker", false, config, configFile);

        // as prefix is now used, set it empty for servers that already customized their messages
        setDefaultConfigItem("prefix", "", config, configFile);

        replaceTemporalityMode();
        replaceQuestsAmount();
        replaceInterfaceNames();
        replaceNPCNames();
        renameQuestFiles();

        updateVersion(version);
    }

    private void replaceTemporalityMode() {
        final int currentMode = config.getInt(TEMPORALITY_MODE);
        switch (currentMode) {
            case 2 -> setDefaultConfigItem(RENEW_INTERVAL, "7d", config, configFile);
            case 3 -> setDefaultConfigItem(RENEW_INTERVAL, "30d", config, configFile);
            default -> setDefaultConfigItem(RENEW_INTERVAL, "1d", config, configFile);
        }

        removeConfigItem(TEMPORALITY_MODE, config, configFile);
        parameterReplaced(TEMPORALITY_MODE, RENEW_INTERVAL);
    }

    private void replaceQuestsAmount() {
        final int currentMode = config.getInt("quests_mode");

        if (currentMode == 1) {
            final int globalAmount = config.getInt("global_quests_amount");
            setDefaultConfigItem("quests_per_category.global", globalAmount, config, configFile);
        } else {
            final int easyAmount = config.getInt("easy_quests_amount");
            final int mediumAmount = config.getInt("medium_quests_amount");
            final int hardAmount = config.getInt("hard_quests_amount");

            if (easyAmount > 0) setDefaultConfigItem("quests_per_category.easy", easyAmount, config, configFile);
            if (mediumAmount > 0) setDefaultConfigItem("quests_per_category.medium", mediumAmount, config, configFile);
            if (hardAmount > 0) setDefaultConfigItem("quests_per_category.hard", hardAmount, config, configFile);
        }

        removeConfigItem("quests_mode", config, configFile);
        removeConfigItem("global_quests_amount", config, configFile);
        removeConfigItem("easy_quests_amount", config, configFile);
        removeConfigItem("medium_quests_amount", config, configFile);
        removeConfigItem("hard_quests_amount", config, configFile);
    }

    private void replaceInterfaceNames() {
        final String[] oldInterfaceNames = {"global_quests", "easy_quests", "medium_quests", "hard_quests"};
        final String[] newInterfaceNames = {"global", "easy", "medium", "hard"};

        final ConfigurationSection section = config.getConfigurationSection("interfaces");
        if (section == null) {
            PluginLogger.error("Interfaces section is missing in the configuration file. Disabling.");
            return;
        }

        int i = 0;
        for (String interfaceName : oldInterfaceNames) {
            final String inventoryName = section.getString(interfaceName + ".inventory_name");
            final String emptyItem = section.getString(interfaceName + ".empty_item");

            setDefaultConfigItem("interfaces." + newInterfaceNames[i] + ".inventory_name", inventoryName, config, configFile);
            setDefaultConfigItem("interfaces." + newInterfaceNames[i] + ".empty_item", emptyItem, config, configFile);

            removeConfigItem("interfaces." + interfaceName, config, configFile);
            parameterReplaced("interfaces." + interfaceName, "interfaces." + newInterfaceNames[i]);

            i++;
        }
    }

    private void replaceNPCNames() {
        final String[] oldNPCNames = {"name_player", "name_global", "name_easy", "name_medium", "name_hard"};
        final String[] newNPCNames = {"player", "global", "easy", "medium", "hard"};

        int i = 0;
        for (String NPCName : oldNPCNames) {
            final String name = config.getString("npcs." + NPCName);

            setDefaultConfigItem("npcs." + newNPCNames[i], name, config, configFile);
            removeConfigItem("npcs." + NPCName, config, configFile);

            parameterReplaced(NPCName, newNPCNames[i]);

            i++;
        }
    }

    /**
     * Renames all quest files in the "quests" folder by removing the "Quests" suffix.
     */
    private void renameQuestFiles() {
        final File questsFolder = new File(ODailyQuests.INSTANCE.getDataFolder(), "quests");
        if (!questsFolder.exists() || !questsFolder.isDirectory()) {
            PluginLogger.warn("Quests folder does not exist or is not a directory.");
            return;
        }

        final File[] files = questsFolder.listFiles();
        if (files == null) {
            PluginLogger.warn("No files found in the quests directory.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith("Quests.yml")) {
                final String newName = file.getName().replace("Quests", "");
                final File newFile = new File(questsFolder, newName);

                if (file.renameTo(newFile)) {
                    PluginLogger.warn("Renamed " + file.getName() + " to " + newFile.getName());
                } else {
                    PluginLogger.error("Failed to rename " + file.getName());
                }
            }
        }
    }
}
