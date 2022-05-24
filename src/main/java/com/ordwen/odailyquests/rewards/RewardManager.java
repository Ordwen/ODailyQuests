package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.apis.hooks.points.PlayerPointsHook;
import com.ordwen.odailyquests.apis.hooks.points.TokenManagerHook;
import com.ordwen.odailyquests.apis.hooks.eco.VaultHook;
import com.ordwen.odailyquests.configuration.functionalities.Actionbar;
import com.ordwen.odailyquests.configuration.functionalities.Title;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RewardManager {

    public static void sendAllRewardItems(String questName, String playerName, Reward reward) {
        Player player = Bukkit.getPlayer(playerName);

        player.sendMessage(QuestsMessages.QUEST_ACHIEVED.toString().replace("%questName%", questName));
        Title.sendTitle(player, questName);
        Actionbar.sendActionbar(player, questName);
        sendQuestReward(player, reward);
    }

    /**
     * Give quest-reward to player.
     *
     * @param player to give the reward.
     * @param reward     quest reward.
     */
    public static void sendQuestReward(Player player, Reward reward) {
        switch (reward.getRewardType()) {
            case COMMAND:
                for (String cmd : reward.getRewardCommands()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                }
                player.sendMessage(QuestsMessages.REWARD_COMMAND.toString());
                break;
            case EXP_LEVELS:
                player.giveExpLevels(reward.getRewardAmount());
                player.sendMessage(QuestsMessages.REWARD_EXP_LEVELS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case EXP_POINTS:
                player.giveExp(reward.getRewardAmount());
                player.sendMessage(QuestsMessages.REWARD_EXP_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case MONEY:
                VaultHook.getEconomy().depositPlayer(player, reward.getRewardAmount());
                player.sendMessage(QuestsMessages.REWARD_MONEY.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                break;
            case POINTS:
                if (TokenManagerHook.getTokenManagerAPI() != null) {
                    TokenManagerHook.getTokenManagerAPI().addTokens(player, reward.getRewardAmount());
                    player.sendMessage(QuestsMessages.REWARD_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else if (PlayerPointsHook.isPlayerPointsSetup()) {
                    PlayerPointsHook.getPlayerPointsAPI().give(player.getUniqueId(), reward.getRewardAmount());
                    player.sendMessage(QuestsMessages.REWARD_POINTS.toString().replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else {
                    PluginLogger.info(ChatColor.RED + "Impossible to give reward to " + ChatColor.GOLD + player.getName() + ChatColor.RED + ".");
                    PluginLogger.info(ChatColor.RED + "Reward type is " + reward.getRewardType().getRewardTypeName() + " but TokenManager is not hooked.");
                }
        }
    }
}
