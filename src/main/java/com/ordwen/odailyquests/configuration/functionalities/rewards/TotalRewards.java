package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.TotalRewardsFile;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class TotalRewards implements IConfigurable {

    private final FileConfiguration config;

    private final RewardLoader rewardLoader;
    private final Map<Integer, Reward> globalTotalRewards;
    private final Map<String, Map<Integer, Reward>> categoryTotalRewards;

    private boolean enabled;

    public TotalRewards(TotalRewardsFile totalRewardsFile) {
        this.config = totalRewardsFile.getConfig();
        this.rewardLoader = new RewardLoader();
        this.globalTotalRewards = new HashMap<>();
        this.categoryTotalRewards = new HashMap<>();
    }

    public void load() {
        this.enabled = config.getBoolean("enabled", false);
        if (enabled) {
            loadGlobalTotalRewards(config);
            loadCategoryTotalRewards(config);
        }
    }

    private void loadGlobalTotalRewards(FileConfiguration config) {
        final ConfigurationSection globalSection = config.getConfigurationSection("global_total_rewards");
        if (globalSection != null) {
            for (String key : globalSection.getKeys(false)) {
                final int questCount = Integer.parseInt(key);
                final ConfigurationSection rewardSection = globalSection.getConfigurationSection(key);
                final Reward reward = rewardLoader.getRewardFromSection(rewardSection, "total_rewards.yml", key);
                globalTotalRewards.put(questCount, reward);
            }
        }
    }

    private void loadCategoryTotalRewards(FileConfiguration config) {
        final ConfigurationSection categorySection = config.getConfigurationSection("category_total_rewards");
        if (categorySection != null) {
            for (String category : categorySection.getKeys(false)) {
                final ConfigurationSection categoryRewardsSection = categorySection.getConfigurationSection(category);
                final Map<Integer, Reward> categoryRewards = new HashMap<>();
                for (String key : categoryRewardsSection.getKeys(false)) {
                    final int questCount = Integer.parseInt(key);
                    final ConfigurationSection rewardSection = categoryRewardsSection.getConfigurationSection(key);
                    final Reward reward = rewardLoader.getRewardFromSection(rewardSection, "total_rewards.yml", key);
                    categoryRewards.put(questCount, reward);
                }
                categoryTotalRewards.put(category, categoryRewards);
            }
        }
    }

    private Reward getGlobalTotalRewardInternal(int questCount) {
        if (!enabled) return null;
        return globalTotalRewards.get(questCount);
    }

    private Reward getCategoryTotalRewardInternal(String category, int questCount) {
        if (!enabled || category == null || category.isEmpty()) return null;

        final Map<Integer, Reward> categoryRewards = categoryTotalRewards.get(category);
        if (categoryRewards != null) {
            return categoryRewards.get(questCount);
        }
        return null;
    }

    private boolean isGlobalStepInternal(int questCount) {
        return globalTotalRewards.containsKey(questCount);
    }

    private boolean isCategoryStepInternal(String category, int questCount) {
        if (category == null || category.isEmpty()) return false;
        final Map<Integer, Reward> categoryRewards = categoryTotalRewards.get(category);
        return categoryRewards != null && categoryRewards.containsKey(questCount);
    }

    private static TotalRewards getInstance() {
        return ConfigFactory.getConfig(TotalRewards.class);
    }

    public static Reward getGlobalTotalReward(int questCount) {
        return getInstance().getGlobalTotalRewardInternal(questCount);
    }

    public static Reward getCategoryTotalReward(String category, int questCount) {
        return getInstance().getCategoryTotalRewardInternal(category, questCount);
    }

    public static boolean isGlobalStep(int questCount) {
        return getInstance().isGlobalStepInternal(questCount);
    }

    public static boolean isCategoryStep(String category, int questCount) {
        return getInstance().isCategoryStepInternal(category, questCount);
    }
}
