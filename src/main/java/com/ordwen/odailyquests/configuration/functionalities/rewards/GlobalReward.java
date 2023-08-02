package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class GlobalReward {

    private final ConfigurationFiles configurationFiles;

    public GlobalReward(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static Reward globalReward;
    private static boolean isEnabled;

    /**
     * Load global reward.
     */
    public void initGlobalReward() {
        final ConfigurationSection globalRewardSection = configurationFiles.getConfigFile().getConfigurationSection("global_reward");
        isEnabled = globalRewardSection.getBoolean("enabled");
        if (isEnabled) {
            RewardType rewardType = RewardType.valueOf(globalRewardSection.getString(".reward_type"));
            if (rewardType == RewardType.COMMAND) {
                globalReward = new Reward(rewardType, globalRewardSection.getStringList(".commands"));
            } else {
                globalReward = new Reward(rewardType, globalRewardSection.getInt(".amount"));
            }
            PluginLogger.fine("Global reward successfully loaded.");
        } else PluginLogger.fine("Global reward is disabled.");
    }

    /**
     * Give reward when players have completed all their quests.
     * @param playerName player name.
     */
    public static void sendGlobalReward(String playerName) {
        if (isEnabled) {
            final String msg = QuestsMessages.ALL_QUESTS_ACHIEVED.toString();
            if (msg != null) Bukkit.getPlayer(playerName).sendMessage(msg);
            RewardManager.sendQuestReward(Bukkit.getPlayer(playerName), globalReward);
        }
    }
}
