package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

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
        isEnabled = configurationFiles.getConfigFile().getConfigurationSection("global_reward").getBoolean("enabled");
        if (isEnabled) {
            RewardType rewardType = RewardType.valueOf(configurationFiles.getConfigFile().getConfigurationSection("global_reward").getString(".reward_type"));
            if (rewardType == RewardType.COMMAND) {
                globalReward = new Reward(rewardType, configurationFiles.getConfigFile().getConfigurationSection("global_reward").getStringList(".commands"));
            } else {
                globalReward = new Reward(rewardType, configurationFiles.getConfigFile().getConfigurationSection("global_reward").getInt(".amount"));
            }
            PluginLogger.info("Global reward successfully loaded.");
        } else PluginLogger.info("Global reward is disabled.");
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
