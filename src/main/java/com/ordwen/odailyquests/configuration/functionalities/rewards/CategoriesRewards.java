package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CategoriesRewards {

    private static final Map<String, Reward> categoriesRewards = new HashMap<>();

    private final ConfigurationFiles configurationFiles;

    public CategoriesRewards(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Load categories rewards.
     */
    public void initCategoriesRewards() {
        categoriesRewards.clear();

        final ConfigurationSection rewardsSection = configurationFiles.getConfigFile().getConfigurationSection("categories_rewards");
        if (rewardsSection == null) {
            PluginLogger.error("Categories rewards section is missing in the configuration file.");
            return;
        }

        for (String category : rewardsSection.getKeys(false)) {
            final ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(category);
            if (rewardSection == null) {
                PluginLogger.error("Category section is missing in the configuration file.");
                continue;
            }

            final Reward reward = getRewardFromSection(rewardsSection);
            categoriesRewards.put(category, reward);
        }
    }

    /**
     * Load a reward from a configuration section.
     * @param section configuration section.
     * @return reward.
     */
    private Reward getRewardFromSection(ConfigurationSection section) {
        final RewardType rewardType = RewardType.valueOf(section.getString("reward_type"));
        Reward reward;
        if (rewardType == RewardType.COMMAND) {
            reward = new Reward(rewardType, section.getStringList("commands"));
        } else {
            reward = new Reward(rewardType, section.getInt("amount"));
        }

        return reward;
    }

    /**
     * Send a reward to a player depending on the category.
     * @param player player.
     * @param category category.
     */
    public static void sendCategoryReward(Player player, String category) {
        if (categoriesRewards.containsKey(category)) {
            final Reward reward = categoriesRewards.get(category);
            final String msg = QuestsMessages.CATEGORY_QUESTS_ACHIEVED.getMessage(player);
            if (msg != null) player.sendMessage(msg.replace("%category%", category));

            RewardManager.sendQuestReward(player, reward);
        }
    }
}
