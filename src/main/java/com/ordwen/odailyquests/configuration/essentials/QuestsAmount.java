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

    /**
     * Get the amount of quests by category.
     * @param name category name.
     * @return amount of quests by category.
     */
    public static int getQuestsAmountByCategory(String name) {
        switch (name) {
            case "easyQuests" -> {
                return easyQuestsAmount;
            }
            case "mediumQuests" -> {
                return mediumQuestsAmount;
            }
            case "hardQuests" -> {
                return hardQuestsAmount;
            }
        }

        return -1;
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
