package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.configuration.file.FileConfiguration;

public class QuestsAmount {

    private final ConfigurationFiles configurationFiles;

    public QuestsAmount(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public void loadQuestsAmount() {

        final FileConfiguration config = configurationFiles.getConfigFile();

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {

            int questsAmount = 0;
            if (questSystem.getQuestsMode() == 1) {
                questSystem.setGlobalQuestsAmount(config.getInt(questSystem.getConfigPath() + "global_quests_amount"));
                questsAmount = questSystem.getGlobalQuestsAmount();
            } else if (questSystem.getQuestsMode() == 2) {
                questSystem.setEasyQuestsAmount(config.getInt(questSystem.getConfigPath() + "easy_quests_amount"));
                questSystem.setMediumQuestsAmount(config.getInt(questSystem.getConfigPath() + "medium_quests_amount"));
                questSystem.setHardQuestsAmount(config.getInt(questSystem.getConfigPath() + "hard_quests_amount"));

                questsAmount = questSystem.getEasyQuestsAmount() + questSystem.getMediumQuestsAmount() + questSystem.getHardQuestsAmount();
            }
            questSystem.setQuestsAmount(questsAmount);
        });
    }

    /**
     * Get the amount of quests by category.
     * @param name category name.
     * @return amount of quests by category.
     */
    public static int getQuestsAmountByCategory(QuestSystem questSystem, String name) {
        switch (name) {
            case "easyQuests" -> {
                return questSystem.getEasyQuestsAmount();
            }
            case "mediumQuests" -> {
                return questSystem.getMediumQuestsAmount();
            }
            case "hardQuests" -> {
                return questSystem.getHardQuestsAmount();
            }
        }
        return -1;
    }
}
