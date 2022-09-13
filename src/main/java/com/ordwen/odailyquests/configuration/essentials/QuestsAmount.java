package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.AddDefault;
import org.bukkit.configuration.file.FileConfiguration;

public class QuestsAmount {

    /*
    QuestsManager ( -> categorized)
    PlayerInterface
    + PlaceholderAPI
    + HolographicDisplays
    + AdminCommands ( -> complete)
    + PlayerQuests
     */

    private final ConfigurationFiles configurationFiles;

    public QuestsAmount(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static int questsAmount;

    private static int easyQuestsAmount;
    private static int mediumQuestsAmount;
    private static int hardQuestsAmount;

    public void loadQuestsAmount() {

        final FileConfiguration config = configurationFiles.getConfigFile();

        if (!config.contains("global_quests_amount")) AddDefault.addDefaultConfigItem("quests_item", 3, config, configurationFiles.getFile());
        if (!config.contains("easy_quests_amount")) AddDefault.addDefaultConfigItem("easy_quests_amount", 1, config, configurationFiles.getFile());
        if (!config.contains("medium_quests_amount")) AddDefault.addDefaultConfigItem("medium_quests_amount", 1, config, configurationFiles.getFile());
        if (!config.contains("hard_quests_amount")) AddDefault.addDefaultConfigItem("hard_quests_amount", 1, config, configurationFiles.getFile());

        if (Modes.getQuestsMode() == 1) {
            questsAmount = config.getInt("quests_amount");
        } else if (Modes.getQuestsMode() == 2) {
            easyQuestsAmount = config.getInt("easy_quests_amount");
            mediumQuestsAmount = config.getInt("medium_quests_amount");
            hardQuestsAmount = config.getInt("hard_quests_amount");

            questsAmount = easyQuestsAmount + mediumQuestsAmount + hardQuestsAmount;
        }
    }

    public static int getQuestsAmount() {
        return questsAmount;
    }

    public static int getEasyQuestsAmount() {
        return easyQuestsAmount;
    }

    public static int getMediumQuestsAmount() {
        return mediumQuestsAmount;
    }

    public static int getHardQuestsAmount() {
        return hardQuestsAmount;
    }
}
