package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GlobalReward extends RewardLoader {

    private static Reward globalReward;
    private static boolean isEnabled;
    private final ConfigurationFiles configurationFiles;
    public GlobalReward(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Give reward when players have completed all their quests.
     *
     * @param playerName player name.
     */
    public static void sendGlobalReward(final String playerName) {
        if (isEnabled) {
            final Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                PluginLogger.warn("Impossible to send global reward to " + playerName + " because he is offline.");
                return;
            }

            final String msg = QuestsMessages.ALL_QUESTS_ACHIEVED.getMessage(playerName);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(Bukkit.getPlayer(playerName), globalReward);
        }
    }

    /**
     * Load global reward.
     */
    public void initGlobalReward() {
        final ConfigurationSection globalRewardSection = configurationFiles.getConfigFile().getConfigurationSection("global_reward");
        if (globalRewardSection == null) {
            isEnabled = false;
            PluginLogger.error("Global reward section is missing in the configuration file.");
            return;
        }

        if (!globalRewardSection.contains("enabled")) {
            isEnabled = false;
            PluginLogger.error("Global reward section is missing in the configuration file.");
            return;
        }

        isEnabled = globalRewardSection.getBoolean("enabled");

        if (isEnabled) {
            final RewardType rewardType = RewardType.valueOf(globalRewardSection.getString(".reward_type"));

            if (rewardType == RewardType.COMMAND) {
                globalReward = new Reward(rewardType, globalRewardSection.getStringList(".commands"));
            } else {
                globalReward = new Reward(rewardType, globalRewardSection.getInt(".amount"));
            }

            globalReward = new RewardLoader().getRewardFromSection(globalRewardSection, "config.yml", null);

            PluginLogger.fine("Global reward successfully loaded.");
        } else PluginLogger.fine("Global reward is disabled.");
    }
}
