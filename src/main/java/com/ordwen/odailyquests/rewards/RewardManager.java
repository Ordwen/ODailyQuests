package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.externs.hooks.points.PlayerPointsHook;
import com.ordwen.odailyquests.externs.hooks.points.TokenManagerHook;
import com.ordwen.odailyquests.externs.hooks.eco.VaultHook;
import com.ordwen.odailyquests.configuration.functionalities.progression.ActionBar;
import com.ordwen.odailyquests.configuration.functionalities.progression.Title;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.tools.PluginUtils;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

public class RewardManager {

    private RewardManager() {}
    
    private static final String IMPOSSIBLE_TO_GIVE_REWARD = "Impossible to give the reward to ";
    private static final String REWARD_TYPE_IS = "Reward type is ";
    private static final String CONTACT_ADMINISTRATOR = "Impossible to give you your reward. Please contact an administrator.";
    private static final String REWARD_AMOUNT = "%rewardAmount%";
    
    public static void sendAllRewardItems(String questName, Player player, Reward reward) {
        Debugger.write("RewardManager: sendAllRewardItems summoned by " + player.getName() + " for " + questName + ".");

        final String msg = QuestsMessages.QUEST_ACHIEVED.getMessage(player);
        if (msg != null) player.sendMessage(msg.replace("%questName%", questName));

        Title.sendTitle(player, questName);
        ActionBar.sendActionbar(player, questName);
        sendReward(player, reward);
    }

    /**
     * Give quest-reward to player.
     *
     * @param player to give the reward.
     * @param reward     quest reward.
     */
    public static void sendReward(Player player, Reward reward) {
        if (reward.getRewardType() == RewardType.NONE) return;

        Debugger.write("RewardManager: sendQuestReward summoned by " + player.getName() + " for " + reward.getRewardType());

        String msg;
        switch (reward.getRewardType()) {
            case COMMAND -> {
                for (String cmd : reward.getRewardCommands()) {
                    cmd = TextFormatter.format(TextFormatter.format(player, cmd));
                    String finalCmd = cmd;
                    ODailyQuests.morePaperLib.scheduling().globalRegionalScheduler().run(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCmd.replace("%player%", player.getName())));
                }

                msg = QuestsMessages.REWARD_COMMAND.getMessage(player);
                if (msg != null) player.sendMessage(msg);
            }

            case EXP_LEVELS -> {
                ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(player).run(() -> player.giveExpLevels((int) reward.getRewardAmount()), null);
                msg = QuestsMessages.REWARD_EXP_LEVELS.getMessage(player);
                if (msg != null) player.sendMessage(msg.replace(REWARD_AMOUNT, String.valueOf(reward.getRewardAmount())));
            }

            case EXP_POINTS -> {
                ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(player).run(() -> player.giveExp((int) reward.getRewardAmount()), null);
                msg = QuestsMessages.REWARD_EXP_POINTS.getMessage(player);
                if (msg != null) player.sendMessage(msg.replace(REWARD_AMOUNT, String.valueOf(reward.getRewardAmount())));
            }

            case MONEY -> {
                if (VaultHook.getEconomy() != null) {
                    VaultHook.getEconomy().depositPlayer(player, reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_MONEY.getMessage(player);
                    if (msg != null) player.sendMessage(msg.replace(REWARD_AMOUNT, String.valueOf(reward.getRewardAmount())));
                }

                else {
                    PluginLogger.error(IMPOSSIBLE_TO_GIVE_REWARD + player.getName() + ".");
                    PluginLogger.error(REWARD_TYPE_IS + reward.getRewardType() + " but Vault is not hooked.");
                    player.sendMessage(ChatColor.RED + CONTACT_ADMINISTRATOR);
                }
            }

            case POINTS -> {
                if (TokenManagerHook.getTokenManagerAPI() != null) {
                    TokenManagerHook.getTokenManagerAPI().addTokens(player, (int) reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_POINTS.getMessage(player);
                    if (msg != null) player.sendMessage(msg.replace(REWARD_AMOUNT, String.valueOf(reward.getRewardAmount())));
                }

                else if (PlayerPointsHook.isPlayerPointsSetup()) {
                    PlayerPointsHook.getPlayerPointsAPI().give(player.getUniqueId(), (int) reward.getRewardAmount());

                    msg = QuestsMessages.REWARD_POINTS.getMessage(player);
                    if (msg != null)  player.sendMessage(msg.replace(REWARD_AMOUNT, String.valueOf(reward.getRewardAmount())));
                }

                else {
                    PluginLogger.error(IMPOSSIBLE_TO_GIVE_REWARD + player.getName() + ".");
                    PluginLogger.error(REWARD_TYPE_IS + reward.getRewardType() + " but no points plugin is hooked.");
                    player.sendMessage(ChatColor.RED + CONTACT_ADMINISTRATOR);
                }
            }

            case COINS_ENGINE -> {
                if (!PluginUtils.isPluginEnabled("CoinsEngine")) {
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
                                .replace(REWARD_AMOUNT, String.valueOf(reward.getRewardAmount()))
                                .replace("%currencyName%", TextFormatter.format(reward.getRewardCurrencyDisplayName())));
            }
        }

        if (reward.getMessage() != null && !reward.getMessage().isEmpty()) {
            player.sendMessage(TextFormatter.format(reward.getMessage().replace("%player%", player.getName())));
        }
    }

    /**
     * Send error message to player if reward type is not supported.
     * @param player to send the message.
     * @param rewardType reward type.
     */
    private static void rewardTypeError(Player player, RewardType rewardType) {
        PluginLogger.error(IMPOSSIBLE_TO_GIVE_REWARD + player.getName() + ".");
        PluginLogger.error(REWARD_TYPE_IS + rewardType + " but there is no supported plugin hooked");
        player.sendMessage(ChatColor.RED + CONTACT_ADMINISTRATOR);
    }

    /**
     * Send error message to player if currency is not supported.
     * @param player to send the message.
     * @param currencyName currency name.
     */
    private static void currencyError(Player player, String currencyName) {
        PluginLogger.error(IMPOSSIBLE_TO_GIVE_REWARD + player.getName() + ".");
        PluginLogger.error("Currency name is " + currencyName + " but there is no currency with this name.");
        player.sendMessage(ChatColor.RED + CONTACT_ADMINISTRATOR);
    }
}
