package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.apis.PlayerPointsHook;
import com.ordwen.odailyquests.apis.TokenManagerHook;
import com.ordwen.odailyquests.apis.VaultHook;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Objects;

public class RewardManager {

    /* Logger for stacktrace */

    private final ConfigurationFiles configurationFiles;

    public RewardManager(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Give quest-reward to player.
     *
     * @param playerName name of player to give the reward.
     * @param reward     quest reward.
     */
    public static void sendQuestReward(String playerName, Reward reward) {
        switch (reward.getRewardType()) {
            case COMMAND:
                for (String cmd : reward.getRewardCommands()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", playerName));
                }
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.REWARD_COMMAND.toString());
                break;
            case EXP_LEVELS:
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).giveExpLevels(reward.getRewardAmount());
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.REWARD_EXP_LEVELS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case EXP_POINTS:
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).giveExp(reward.getRewardAmount());
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.REWARD_EXP_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case MONEY:
                VaultHook.getEconomy().depositPlayer(Bukkit.getPlayer(playerName), reward.getRewardAmount());
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.REWARD_MONEY.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case POINTS:
                if (TokenManagerHook.getTokenManagerAPI() != null) {
                    TokenManagerHook.getTokenManagerAPI().addTokens(Bukkit.getPlayer(playerName), reward.getRewardAmount());
                    Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.REWARD_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else if (PlayerPointsHook.isPlayerPointsSetup()) {
                    PlayerPointsHook.getPlayerPointsAPI().give(Bukkit.getPlayer(playerName).getUniqueId(), reward.getRewardAmount());
                    Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.REWARD_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else {
                    PluginLogger.info(ChatColor.RED + "Impossible to give reward to " + ChatColor.GOLD + playerName + ChatColor.RED + ".");
                    PluginLogger.info(ChatColor.RED + "Reward type is " + reward.getRewardType().getRewardTypeName() + " but TokenManager is not hooked.");
                }
        }
    }
}
