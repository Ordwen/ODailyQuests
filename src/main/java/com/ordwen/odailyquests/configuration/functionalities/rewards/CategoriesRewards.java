package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CategoriesRewards implements IConfigurable {

    private final Map<String, Reward> categoryRewards = new HashMap<>();

    private final ConfigurationFile configurationFile;
    private final RewardLoader rewardLoader = new RewardLoader();

    public CategoriesRewards(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final ConfigurationSection categoriesRewardsConfig = configurationFile.getConfig().getConfigurationSection("categories_rewards");
        if (categoriesRewardsConfig == null) {
            PluginLogger.error("categories_rewards section is missing in the configuration file.");
            return;
        }

        for (String category : categoriesRewardsConfig.getKeys(false)) {
            final ConfigurationSection rewardSection = categoriesRewardsConfig.getConfigurationSection(category);
            if (rewardSection == null) {
                PluginLogger.error("Reward section for category " + category + " is missing in the configuration file.");
                continue;
            }

            final Reward reward = rewardLoader.getRewardFromSection(rewardSection, "config.yml", null);
            categoryRewards.put(category, reward);
        }
    }

    /**
     * Send a reward to a player depending on the category.
     *
     * @param player   player.
     * @param category category.
     */
    public void sendCategoryRewardInternal(Player player, String category) {
        if (!categoryRewards.containsKey(category)) {
            Debugger.write("Category " + category + " is missing in the categories_rewards section.");
            return;
        }

        final Reward reward = categoryRewards.get(category);
        if (reward != null) {
            final String msg = QuestsMessages.CATEGORY_QUESTS_ACHIEVED.toString();
            if (msg != null) {
                player.sendMessage(msg.replace("%category%", category));
            }

            RewardManager.sendReward(player, reward, Collections.emptyMap());
        } else {
            PluginLogger.error("No reward found for category " + category);
        }
    }

    private static CategoriesRewards getInstance() {
        return ConfigFactory.getConfig(CategoriesRewards.class);
    }

    public static void sendCategoryReward(Player player, String category) {
        getInstance().sendCategoryRewardInternal(player, category);
    }
}
