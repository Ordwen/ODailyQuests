package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.apis.PlayerPoints;
import com.ordwen.odailyquests.apis.TokenManagerAPI;
import com.ordwen.odailyquests.apis.VaultAPI;
import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.util.Objects;
import java.util.logging.Logger;

public class RewardManager {

    /* Logger for stacktrace */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

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
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.REWARD_EXP.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case EXP_POINTS:
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).giveExp(reward.getRewardAmount());
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.REWARD_EXP.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case MONEY:
                VaultAPI.getEconomy().depositPlayer(Bukkit.getPlayer(playerName), reward.getRewardAmount());
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.REWARD_MONEY.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case POINTS:
                if (TokenManagerAPI.getTokenManagerAPI() != null) {
                    TokenManagerAPI.getTokenManagerAPI().addTokens(Bukkit.getPlayer(playerName), reward.getRewardAmount());
                    Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.REWARD_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else if (PlayerPoints.isPlayerPointsSetup()) {
                    PlayerPoints.getPlayerPointsAPI().give(Bukkit.getPlayer(playerName).getUniqueId(), reward.getRewardAmount());
                    Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.REWARD_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else {
                    logger.info(ChatColor.RED + "Impossible to give reward to " + ChatColor.GOLD + playerName + ChatColor.RED + ".");
                    logger.info(ChatColor.RED + "Reward type is " + reward.getRewardType().getRewardTypeName() + " but TokenManager is not hooked.");
                }
        }
    }
}
