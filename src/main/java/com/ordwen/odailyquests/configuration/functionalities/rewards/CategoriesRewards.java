package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.TimerTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class CategoriesRewards {

    private final ConfigurationFiles configurationFiles;

    public CategoriesRewards(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Load categories rewards.
     */
    public void initCategoriesRewards() {
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            final ConfigurationSection categoriesRewards = configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "categories_rewards");
            if (categoriesRewards == null) {
                questSystem.setEasyRewardEnabled(false);
                questSystem.setMediumRewardEnabled(false);
                questSystem.setHardRewardEnabled(false);

                PluginLogger.error(questSystem.getSystemName() + " Categories rewards section is missing in the configuration file.");
                return;
            }

            questSystem.setEasyRewardEnabled(categoriesRewards.getBoolean("easy.enabled"));
            questSystem.setMediumRewardEnabled(categoriesRewards.getBoolean("medium.enabled"));
            questSystem.setHardRewardEnabled(categoriesRewards.getBoolean("hard.enabled"));

            if (questSystem.isEasyRewardEnabled()) {
                final ConfigurationSection easyRewardSection = categoriesRewards.getConfigurationSection("easy");
                if (easyRewardSection == null) {
                    questSystem.setEasyRewardEnabled(false);
                    PluginLogger.error(questSystem.getSystemName() + " Easy reward section is missing in the configuration file.");
                    return;
                }

                questSystem.setEasyReward(questSystem.getRewardLoader().getRewardFromSection(easyRewardSection, "config.yml", -1));
            }

            if (questSystem.isMediumRewardEnabled()) {
                final ConfigurationSection mediumRewardSection = categoriesRewards.getConfigurationSection("medium");
                if (mediumRewardSection == null) {
                    questSystem.setMediumRewardEnabled(false);
                    PluginLogger.error(questSystem.getSystemName() + " Medium reward section is missing in the configuration file.");
                    return;
                }

                questSystem.setMediumReward(questSystem.getRewardLoader().getRewardFromSection(mediumRewardSection, "config.yml", -1));
            }

            if (questSystem.isHardRewardEnabled()) {
                final ConfigurationSection hardRewardSection = categoriesRewards.getConfigurationSection("hard");
                if (hardRewardSection == null) {
                    questSystem.setHardRewardEnabled(false);
                    PluginLogger.error(questSystem.getSystemName() + " Hard reward section is missing in the configuration file.");
                    return;
                }

                questSystem.setHardReward(questSystem.getRewardLoader().getRewardFromSection(hardRewardSection, "config.yml", -1));
            }
        });
    }

    /**
     * Send a reward to a player depending on the category.
     * @param player player.
     * @param category category.
     */
    public static void sendCategoryReward(QuestSystem questSystem, Player player, String category) {
        switch (category) {
            case "easyQuests" -> sendEasyReward(questSystem, player);
            case "mediumQuests" -> sendMediumReward(questSystem, player);
            case "hardQuests" -> sendHardReward(questSystem, player);
            default -> PluginLogger.error("Category " + category + " is not valid.");
        }
    }

    /**
     * Give reward when players have completed all their easy quests.
     * @param player player.
     */
    private static void sendEasyReward(QuestSystem questSystem, Player player) {
        if (questSystem.isEasyRewardEnabled()) {
            final String msg = QuestsMessages.EASY_QUESTS_ACHIEVED.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(player, questSystem.getEasyReward());
        }
    }

    /**
     * Give reward when players have completed all their medium quests.
     * @param player player.
     */
    private static void sendMediumReward(QuestSystem questSystem, Player player) {
        if (questSystem.isMediumRewardEnabled()) {
            final String msg = QuestsMessages.MEDIUM_QUESTS_ACHIEVED.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(player, questSystem.getMediumReward());
        }
    }

    /**
     * Give reward when players have completed all their hard quests.
     * @param player player.
     */
    private static void sendHardReward(QuestSystem questSystem, Player player) {
        if (questSystem.isHardRewardEnabled()) {
            final String msg = QuestsMessages.HARD_QUESTS_ACHIEVED.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(player, questSystem.getHardReward());
        }
    }
}
