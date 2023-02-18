package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.configuration.file.FileConfiguration;

public class QuestsAmount {

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

        if (Modes.getQuestsMode() == 1) {
            questsAmount = config.getInt("global_quests_amount");
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
