package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.externs.hooks.eco.CoinsEngineHook;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.externs.hooks.points.PlayerPointsHook;
import com.ordwen.odailyquests.externs.hooks.points.TokenManagerHook;
import com.ordwen.odailyquests.externs.hooks.eco.VaultHook;
import com.ordwen.odailyquests.configuration.functionalities.progression.ActionBar;
import com.ordwen.odailyquests.configuration.functionalities.progression.Title;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

public class RewardManager {

    public static void sendAllRewardItems(String questName, Player player, Reward reward) {

        Debugger.addDebug("RewardManager: sendAllRewardItems summoned by " + player.getName() + " for " + questName + ".");

        final String msg = QuestsMessages.QUEST_ACHIEVED.getMessage(player);
        if (msg != null) player.sendMessage(msg.replace("%questName%", questName));

        Title.sendTitle(player, questName);
        ActionBar.sendActionbar(player, questName);
        sendQuestReward(player, reward);
    }

    /**
     * Give quest-reward to player.
     *
     * @param player to give the reward.
     * @param reward     quest reward.
     */
    public static void sendQuestReward(Player player, Reward reward) {
        if (reward.getRewardType() == RewardType.NONE) return;

        Debugger.addDebug("RewardManager: sendQuestReward summoned by " + player.getName() + " for " + reward.getRewardType());

        String msg;
        switch (reward.getRewardType()) {

            case COMMAND -> {
                for (String cmd : reward.getRewardCommands()) {
                    cmd = ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, cmd));
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                }

                msg = QuestsMessages.REWARD_COMMAND.getMessage(player);
                if (msg != null) player.sendMessage(msg);
            }

            case EXP_LEVELS -> {
                player.giveExpLevels((int) reward.getRewardAmount());
                msg = QuestsMessages.REWARD_EXP_LEVELS.getMessage(player);
                if (msg != null) player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
            }

            case EXP_POINTS -> {
                player.giveExp((int) reward.getRewardAmount());
                msg = QuestsMessages.REWARD_EXP_POINTS.getMessage(player);
                if (msg != null) player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
            }

            case MONEY -> {
                if (VaultHook.getEconomy() != null) {
                    VaultHook.getEconomy().depositPlayer(player, reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_MONEY.getMessage(player);
                    if (msg != null) player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                }

                else {
                    PluginLogger.error("Impossible to give reward to " + player.getName() + ".");
                    PluginLogger.error("Reward type is " + reward.getRewardType() + " but Vault is not hooked.");
                    player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
                }
            }

            case POINTS -> {
                if (TokenManagerHook.getTokenManagerAPI() != null) {
                    TokenManagerHook.getTokenManagerAPI().addTokens(player, (int) reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_POINTS.getMessage(player);
                    if (msg != null) player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                }

                else if (PlayerPointsHook.isPlayerPointsSetup()) {
                    PlayerPointsHook.getPlayerPointsAPI().give(player.getUniqueId(), (int) reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_POINTS.getMessage(player);
                    if (msg != null)  player.sendMessage(msg.replace("%rewardAmount%", String.valueOf(reward.getRewardAmount())));
                }

                else {
                    PluginLogger.error("Impossible to give reward to " + player.getName() + ".");
                    PluginLogger.error("Reward type is " + reward.getRewardType() + " but no points plugin is hooked.");
                    player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
                }
            }

            case COINS_ENGINE -> {
                if (!CoinsEngineHook.isCoinsEngineHooked()) {
                    rewardTypeError(player, reward.getRewardType());
                    return;
                }

                final Currency currency = CoinsEngineAPI.getCurrency(reward.getRewardCurrency());
                if (currency == null) {
                    currencyError(player, reward.getRewardCurrency());
                    return;
                }

                CoinsEngineAPI.addBalance(player, currency, reward.getRewardAmount());
                msg = QuestsMessages.REWARD_COINS_ENGINE.getMessage(player);
                if (msg != null) player.sendMessage(msg
                                .replace("%rewardAmount%", String.valueOf(reward.getRewardAmount()))
                                .replace("%currencyName%", ColorConvert.convertColorCode(reward.getRewardCurrencyDisplayName())));
            }
        }
    }

    /**
     * Send error message to player if reward type is not supported.
     * @param player to send the message.
     * @param rewardType reward type.
     */
    private static void rewardTypeError(Player player, RewardType rewardType) {
        PluginLogger.error("Impossible to give reward to " + player.getName() + ".");
        PluginLogger.error("Reward type is " + rewardType + " but there is no supported plugin hooked");
        player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
    }

    /**
     * Send error message to player if currency is not supported.
     * @param player to send the message.
     * @param currencyName currency name.
     */
    private static void currencyError(Player player, String currencyName) {
        PluginLogger.error("Impossible to give reward to " + player.getName() + ".");
        PluginLogger.error("Currency name is " + currencyName + " but there is no currency with this name.");
        player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
    }
}
