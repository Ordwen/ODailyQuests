package com.ordwen.odailyquests.configuration.functionalities.rewards;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
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

    private final ConfigurationFiles configurationFiles;

    public GlobalReward(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }
    /**
     * Load global reward.
     */
    public void initGlobalReward() {
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
        final ConfigurationSection globalRewardSection = configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "global_reward");
        if (globalRewardSection == null) {
            questSystem.setGlobalRewardEnabled(false);
            PluginLogger.error(questSystem.getSystemName() + " Global reward section is missing in the configuration file.");
            return;
        }

        if (!globalRewardSection.contains("enabled")) {
            questSystem.setGlobalRewardEnabled(false);
            PluginLogger.error(questSystem.getSystemName() + " Global reward section is missing in the configuration file.");
            return;
        }

        questSystem.setGlobalRewardEnabled(globalRewardSection.getBoolean("enabled"));

        if (questSystem.isGlobalRewardEnabled()) {
            final RewardType rewardType = RewardType.valueOf(globalRewardSection.getString(".reward_type"));

            if (rewardType == RewardType.COMMAND) {
                questSystem.setGlobalReward(new Reward(rewardType, globalRewardSection.getStringList(".commands")));
            } else {
                questSystem.setGlobalReward(new Reward(rewardType, globalRewardSection.getInt(".amount")));
            }

            questSystem.setGlobalReward(new RewardLoader().getRewardFromSection(globalRewardSection, "config.yml", -1));

            PluginLogger.fine(questSystem.getSystemName() + " Global reward successfully loaded.");
        } else PluginLogger.fine(questSystem.getSystemName() + " Global reward is disabled.");
        });
    }

    /**
     * Give reward when players have completed all their quests.
     * @param playerName player name.
     */
    public static void sendGlobalReward(QuestSystem questSystem, String playerName) {
        if (questSystem.isGlobalRewardEnabled()) {
            final Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                PluginLogger.warn("Impossible to send " + questSystem.getSystemName() + " global reward to " + playerName + " because he is offline.");
                return;
            }

            final String msg = questSystem.getALL_QUESTS_ACHIEVED().getMessage(playerName);
            if (msg != null) player.sendMessage(msg);

            RewardManager.sendQuestReward(Bukkit.getPlayer(playerName), questSystem.getGlobalReward());
        }
    }
}
