package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.Bukkit;

public class GlobalReward {

    private final ConfigurationFiles configurationFiles;

    public GlobalReward(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static Reward globalReward;

    public void initGlobalReward() {

        RewardType rewardType = RewardType.valueOf(configurationFiles.getConfigFile().getConfigurationSection("global_reward").getString(".reward_type"));
        if (rewardType == RewardType.COMMAND) {
            globalReward = new Reward(rewardType, configurationFiles.getConfigFile().getConfigurationSection("global_reward").getStringList(".commands"));
        }
        else {
            globalReward = new Reward(rewardType, configurationFiles.getConfigFile().getConfigurationSection("global_quests").getInt(".amount"));
        }
    }

    /**
     * Give reward when players have completed all their quests.
     * @param playerName player name.
     */
    public static void sendGlobalReward(String playerName) {
        Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.ALL_QUESTS_ACHIEVED.toString());
        RewardManager.sendQuestReward(playerName, globalReward);
    }


}
