package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.externs.hooks.points.PlayerPointsHook;
import com.ordwen.odailyquests.externs.hooks.points.TokenManagerHook;
import com.ordwen.odailyquests.externs.hooks.eco.VaultHook;
import com.ordwen.odailyquests.configuration.functionalities.Actionbar;
import com.ordwen.odailyquests.configuration.functionalities.Title;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.tools.PluginLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RewardManager {

    public static void sendAllRewardItems(String questName, Player player, Reward reward) {

        final String msg = QuestsMessages.QUEST_ACHIEVED.toString();
        if (msg != null) player.sendMessage(msg.replace("%questName%", questName));

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
        if (reward == null) return;

        String msg;
        switch (reward.getRewardType()) {
            case COMMAND -> {
                for (String cmd : reward.getRewardCommands()) {
                    cmd = PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', cmd));
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                }
                msg = QuestsMessages.REWARD_COMMAND.toString();
                if (msg != null) player.sendMessage(msg);
            }
            case EXP_LEVELS -> {
                player.giveExpLevels(reward.getRewardAmount());
                msg = QuestsMessages.REWARD_EXP_LEVELS.toString();
                if (msg != null)
                    player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
            }
            case EXP_POINTS -> {
                player.giveExp(reward.getRewardAmount());
                msg = QuestsMessages.REWARD_EXP_POINTS.toString();
                if (msg != null)
                    player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
            }
            case MONEY -> {
                if (VaultHook.getEconomy() != null) {
                    VaultHook.getEconomy().depositPlayer(player, reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_MONEY.toString();
                    if (msg != null)
                        player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else {

                    msg = QuestsMessages.REWARD_MONEY_FAIL.toString();
                    if (msg != null) player.sendMessage(msg);
                    PluginLogger.error("Impossible to give money to player " + player.getName() + ". Vault is not properly setup!");
                }
            }
            case POINTS -> {
                if (TokenManagerHook.getTokenManagerAPI() != null) {
                    TokenManagerHook.getTokenManagerAPI().addTokens(player, reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_POINTS.toString();
                    if (msg != null)
                        player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else if (PlayerPointsHook.isPlayerPointsSetup()) {
                    PlayerPointsHook.getPlayerPointsAPI().give(player.getUniqueId(), reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_POINTS.toString();
                    if (msg != null)
                        player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                } else {
                    PluginLogger.error("Impossible to give reward to " + player.getName() + ".");
                    PluginLogger.error("Reward type is " + reward.getRewardType().getRewardTypeName() + " but TokenManager is not hooked.");
                }
            }
        }
    }
}
