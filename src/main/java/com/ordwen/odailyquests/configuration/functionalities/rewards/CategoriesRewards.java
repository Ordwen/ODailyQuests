package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CategoriesRewards {

    private static boolean isEasyRewardEnabled;
    private static boolean isMediumRewardEnabled;
    private static boolean isHardRewardEnabled;

    private static Reward easyReward;
    private static Reward mediumReward;
    private static Reward hardReward;

    private final ConfigurationFiles configurationFiles;
    private final RewardLoader rewardLoader = new RewardLoader();

    public CategoriesRewards(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Load categories rewards.
     */
    public void initCategoriesRewards() {
        final ConfigurationSection categoriesRewards = configurationFiles.getConfigFile().getConfigurationSection("categories_rewards");
        if (categoriesRewards == null) {
            isEasyRewardEnabled = false;
            isMediumRewardEnabled = false;
            isHardRewardEnabled = false;

            PluginLogger.error("Categories rewards section is missing in the configuration file.");
            return;
        }

        isEasyRewardEnabled = categoriesRewards.getBoolean("easy.enabled");
        isMediumRewardEnabled = categoriesRewards.getBoolean("medium.enabled");
        isHardRewardEnabled = categoriesRewards.getBoolean("hard.enabled");

        if (isEasyRewardEnabled) {
            final ConfigurationSection easyRewardSection = categoriesRewards.getConfigurationSection("easy");
            if (easyRewardSection == null) {
                isEasyRewardEnabled = false;
                PluginLogger.error("Easy reward section is missing in the configuration file.");
                return;
            }

            easyReward = rewardLoader.getRewardFromSection(easyRewardSection, "config.yml", -1);
        }

        if (isMediumRewardEnabled) {
            final ConfigurationSection mediumRewardSection = categoriesRewards.getConfigurationSection("medium");
            if (mediumRewardSection == null) {
                isMediumRewardEnabled = false;
                PluginLogger.error("Medium reward section is missing in the configuration file.");
                return;
            }

            mediumReward = rewardLoader.getRewardFromSection(mediumRewardSection, "config.yml", -1);
        }

        if (isHardRewardEnabled) {
            final ConfigurationSection hardRewardSection = categoriesRewards.getConfigurationSection("hard");
            if (hardRewardSection == null) {
                isHardRewardEnabled = false;
                PluginLogger.error("Hard reward section is missing in the configuration file.");
                return;
            }

            hardReward = rewardLoader.getRewardFromSection(hardRewardSection, "config.yml", -1);
        }
    }

    /**
     * Send a reward to a player depending on the category.
     * @param player player.
     * @param category category.
     */
    public static void sendCategoryReward(Player player, String category) {
        switch (category) {
            case "easyQuests" -> sendEasyReward(player);
            case "mediumQuests" -> sendMediumReward(player);
            case "hardQuests" -> sendHardReward(player);
            default -> PluginLogger.error("Category " + category + " is not valid.");
        }
    }

    /**
     * Give reward when players have completed all their easy quests.
     * @param player player.
     */
    private static void sendEasyReward(Player player) {
        if (isEasyRewardEnabled) {
            final String msg = QuestsMessages.EASY_QUESTS_ACHIEVED.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(player, easyReward);
        }
    }

    /**
     * Give reward when players have completed all their medium quests.
     * @param player player.
     */
    private static void sendMediumReward(Player player) {
        if (isMediumRewardEnabled) {
            final String msg = QuestsMessages.MEDIUM_QUESTS_ACHIEVED.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(player, mediumReward);
        }
    }

    /**
     * Give reward when players have completed all their hard quests.
     * @param player player.
     */
    private static void sendHardReward(Player player) {
        if (isHardRewardEnabled) {
            final String msg = QuestsMessages.HARD_QUESTS_ACHIEVED.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(player, hardReward);
        }
    }
}
