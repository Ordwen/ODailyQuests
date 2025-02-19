package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GlobalReward extends RewardLoader implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public GlobalReward(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;
    private Reward reward;

    @Override
    public void load() {
        final ConfigurationSection globalRewardSection = configurationFile.getConfig().getConfigurationSection("global_reward");
        if (globalRewardSection == null || !globalRewardSection.contains("enabled")) {
            PluginLogger.error("Global reward section is missing or incomplete in the configuration file. Disabling.");
            isEnabled = false;
            return;
        }

        isEnabled = globalRewardSection.getBoolean("enabled");

        if (isEnabled) {
            final RewardType rewardType = RewardType.valueOf(globalRewardSection.getString(".reward_type"));

            if (rewardType == RewardType.COMMAND) {
                reward = new Reward(rewardType, globalRewardSection.getStringList(".commands"));
            } else {
                reward = new Reward(rewardType, globalRewardSection.getInt(".amount"));
            }

            reward = new RewardLoader().getRewardFromSection(globalRewardSection, "config.yml", null);

            PluginLogger.fine("Global reward successfully loaded.");
        } else PluginLogger.fine("Global reward is disabled.");
    }

    public void sendGlobalRewardInternal(String playerName) {
        if (isEnabled) {
            final Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                PluginLogger.warn("Impossible to send global reward to " + playerName + " because he is offline.");
                return;
            }

            final String msg = QuestsMessages.ALL_QUESTS_ACHIEVED.getMessage(playerName);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(Bukkit.getPlayer(playerName), reward);
        }
    }

    private static GlobalReward getInstance() {
        return ConfigFactory.getConfig(GlobalReward.class);
    }

    public static void sendGlobalReward(String playerName) {
        getInstance().sendGlobalRewardInternal(playerName);
    }
}
