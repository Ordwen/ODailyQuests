package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.functionalities.progression.ToastNotification;
import com.ordwen.odailyquests.externs.hooks.points.PlayerPointsHook;
import com.ordwen.odailyquests.externs.hooks.points.TokenManagerHook;
import com.ordwen.odailyquests.externs.hooks.eco.VaultHook;
import com.ordwen.odailyquests.configuration.functionalities.progression.ActionBar;
import com.ordwen.odailyquests.configuration.functionalities.progression.Title;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginUtils;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code RewardManager} class is responsible for handling and delivering quest rewards to players.
 * <p>
 * Rewards can be of multiple types (commands, experience, money, points, custom plugins like CoinsEngine).
 * This class centralizes the logic for giving those rewards and ensures that error handling and
 * placeholder replacements are consistently applied.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *     <li>Send quest completion notifications (titles, action bars, toasts, messages).</li>
 *     <li>Dispatch rewards depending on their {@link RewardType}.</li>
 *     <li>Integrate with third-party APIs (Vault, TokenManager, PlayerPoints, CoinsEngine).</li>
 *     <li>Provide error handling when required plugins are missing or misconfigured.</li>
 * </ul>
 *
 * <p>
 * This class cannot be instantiated and exposes only static methods.
 * </p>
 */
public class RewardManager {

    /**
     * Prevent instantiation of the utility class.
     */
    private RewardManager() {
    }

    private static final String REWARD_AMOUNT = "%rewardAmount%";

    /**
     * Sends all the visual feedback and actual reward items to a player when they achieve a quest.
     *
     * @param questName   the name of the quest
     * @param player      the player receiving the reward
     * @param reward      the reward configuration
     * @param progression the progression object used to populate placeholders
     */
    public static void sendQuestRewardItems(String questName, Player player, Reward reward, Progression progression) {
        Debugger.write("RewardManager: sendAllRewardItems summoned by " + player.getName() + " for " + questName + ".");

        final String msg = QuestsMessages.QUEST_ACHIEVED.getMessage(player, Map.of("%questName%", questName));
        if (msg != null) player.sendMessage(msg);

        // Send visual notifications
        Title.sendTitle(player, questName);
        ToastNotification.sendToastNotification(player, questName);
        ActionBar.sendActionbar(player, questName);

        // Prepare placeholders for reward messages
        final Map<String, String> placeholders = Map.of(
                "%required%", String.valueOf(progression.getRequiredAmount()),
                "%questName%", questName
        );

        final double rewardAmount = ensureProgressionRewardAmount(reward, progression);
        sendReward(player, reward, placeholders, rewardAmount);
    }

    /**
     * Sends the configured reward to the player.
     *
     * @param player       the player receiving the reward
     * @param reward       the reward configuration
     * @param placeholders optional placeholders to expand in command or message rewards
     */
    public static void sendReward(Player player, Reward reward, Map<String, String> placeholders) {
        sendReward(player, reward, placeholders, null);
    }

    /**
     * Sends the configured reward to the player with an optional resolved amount.
     *
     * @param player         the player receiving the reward
     * @param reward         the reward configuration
     * @param placeholders   optional placeholders to expand in command or message rewards
     * @param resolvedAmount resolved amount to use, or {@code null} to compute it
     */
    public static void sendReward(Player player, Reward reward, Map<String, String> placeholders, Double resolvedAmount) {
        if (reward.getRewardType() == RewardType.NONE) return;

        Debugger.write("RewardManager: sendQuestReward summoned by " + player.getName() + " for " + reward.getRewardType());

        final double rewardAmount = resolvedAmount != null ? resolvedAmount : reward.resolveRewardAmount();
        final Map<String, String> expandedPlaceholders = new HashMap<>();
        if (placeholders != null && !placeholders.isEmpty()) {
            expandedPlaceholders.putAll(placeholders);
        }
        expandedPlaceholders.put(REWARD_AMOUNT, String.valueOf(rewardAmount));

        switch (reward.getRewardType()) {
            case COMMAND -> handleCommandReward(player, reward, expandedPlaceholders);
            case EXP_LEVELS -> handleExpLevelsReward(player, rewardAmount);
            case EXP_POINTS -> handleExpPointsReward(player, rewardAmount);
            case MONEY -> handleMoneyReward(player, rewardAmount);
            case POINTS -> handlePointsReward(player, rewardAmount);
            case COINS_ENGINE -> handleCoinsEngineReward(player, reward, rewardAmount);
            default -> rewardTypeError(player, reward.getRewardType());
        }

        // Custom reward message (optional)
        final String custom = reward.getMessage();
        if (custom != null && !custom.isEmpty()) {
            player.sendMessage(expandPlaceholders(player, custom, expandedPlaceholders));
        }
    }

    /**
     * Ensures that the progression object has a reward amount set.
     * If not, it sets it to the resolved reward amount from the reward configuration.
     *
     * @param reward      the reward configuration
     * @param progression the progression object
     * @return the ensured reward amount
     */
    private static double ensureProgressionRewardAmount(Reward reward, Progression progression) {
        if (progression == null) {
            return reward.resolveRewardAmount();
        }

        if (!progression.hasRewardAmount()) {
            progression.setRewardAmount(reward.resolveRewardAmount());
        }

        return progression.getRewardAmount();
    }

    /* -------------------- Reward handlers -------------------- */

    /**
     * Handles rewards of type {@link RewardType#COMMAND}.
     * Executes configured commands, replacing placeholders before dispatch.
     */
    private static void handleCommandReward(Player player, Reward reward, Map<String, String> placeholders) {
        for (String raw : reward.getRewardCommands()) {
            final String cmd = expandPlaceholders(player, raw, placeholders);
            ODailyQuests.morePaperLib
                    .scheduling()
                    .globalRegionalScheduler()
                    .run(() -> {
                        try {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                            Debugger.write("[RewardCmd] Executed command: " + cmd);
                        } catch (Exception e) {
                            Debugger.write("[RewardCmd] Error while executing command: " + cmd);
                            if (e.getMessage() != null) {
                                Debugger.write(e.getMessage());
                            }
                            final String msg = QuestsMessages.REWARD_COMMAND_ERROR.toString();
                            if (msg != null) {
                                player.sendMessage(msg.replace("%command%", cmd));
                            }
                        }
                    });
        }
        sendMsg(player, QuestsMessages.REWARD_COMMAND);
    }

    /**
     * Handles rewards of type {@link RewardType#EXP_LEVELS}.
     */
    private static void handleExpLevelsReward(Player player, double amount) {
        ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(player)
                .run(() -> {
                    player.giveExpLevels((int) amount);
                    Debugger.write("RewardManager: Given " + amount + " EXP levels to " + player.getName() + ".");
                }, null);
        sendMsgAmount(player, QuestsMessages.REWARD_EXP_LEVELS, amount);
    }

    /**
     * Handles rewards of type {@link RewardType#EXP_POINTS}.
     */
    private static void handleExpPointsReward(Player player, double amount) {
        ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(player)
                .run(() -> {
                    player.giveExp((int) amount);
                    Debugger.write("RewardManager: Given " + amount + " EXP points to " + player.getName() + ".");
                }, null);
        sendMsgAmount(player, QuestsMessages.REWARD_EXP_POINTS, amount);
    }

    /**
     * Handles rewards of type {@link RewardType#MONEY}.
     * Uses Vault API if available.
     */
    private static void handleMoneyReward(Player player, double amount) {
        if (VaultHook.getEconomy() == null) {
            rewardTypeErrorWithVault(player, RewardType.MONEY);
            return;
        }

        VaultHook.getEconomy().depositPlayer(player, amount);
        Debugger.write("RewardManager: Given " + amount + " money to " + player.getName() + ".");
        sendMsgAmount(player, QuestsMessages.REWARD_MONEY, amount);
    }

    /**
     * Handles rewards of type {@link RewardType#POINTS}.
     * Uses TokenManager or PlayerPoints if available.
     */
    private static void handlePointsReward(Player player, double amount) {
        if (TokenManagerHook.getTokenManagerAPI() != null) {
            TokenManagerHook.getTokenManagerAPI().addTokens(player, (int) amount);
            Debugger.write("RewardManager: Given " + amount + " points to " + player.getName() + " via TokenManager.");
            sendMsgAmount(player, QuestsMessages.REWARD_POINTS, amount);
            return;
        }
        if (PlayerPointsHook.isPlayerPointsSetup()) {
            PlayerPointsHook.getPlayerPointsAPI().give(player.getUniqueId(), (int) amount);
            Debugger.write("RewardManager: Given " + amount + " points to " + player.getName() + " via PlayerPoints.");
            sendMsgAmount(player, QuestsMessages.REWARD_POINTS, amount);
            return;
        }
        rewardTypeErrorNoPoints(player, RewardType.POINTS);
    }

    /**
     * Handles rewards of type {@link RewardType#COINS_ENGINE}.
     * Uses the CoinsEngine API to give currency to the player.
     */
    private static void handleCoinsEngineReward(Player player, Reward reward, double amount) {
        if (!PluginUtils.isPluginEnabled("CoinsEngine")) {
            rewardTypeError(player, reward.getRewardType());
            return;
        }

        final Currency currency = CoinsEngineAPI.getCurrency(reward.getRewardCurrency());
        if (currency == null) {
            currencyError(player, reward.getRewardCurrency());
            return;
        }

        CoinsEngineAPI.addBalance(player, currency, amount);
        Debugger.write("RewardManager: Given " + amount + " " + reward.getRewardCurrency() + " to " + player.getName() + " via CoinsEngine.");
        sendMsgAmountAndCurrency(
                player,
                QuestsMessages.REWARD_COINS_ENGINE,
                amount,
                TextFormatter.format(reward.getRewardCurrencyDisplayName())
        );
    }

    /* -------------------- Utility methods -------------------- */

    /**
     * Expands all placeholders in a string for the given player.
     *
     * @param player       the player for placeholder substitution
     * @param raw          the raw string containing placeholders
     * @param placeholders custom placeholders map
     * @return the formatted string with placeholders replaced
     */
    private static String expandPlaceholders(Player player, String raw, Map<String, String> placeholders) {
        String s = TextFormatter.format(TextFormatter.format(player, raw)).replace("%player%", player.getName());
        if (placeholders == null || placeholders.isEmpty()) return s;
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            final String k = e.getKey();
            final String v = e.getValue();
            if (k != null && v != null) s = s.replace(k, v);
        }
        return s;
    }

    /**
     * Sends a message for a given quest message type.
     */
    private static void sendMsg(Player player, QuestsMessages qm) {
        final String msg = qm.getMessage(player);
        if (msg != null) player.sendMessage(msg);
    }

    /**
     * Sends a message with an amount placeholder replaced.
     */
    private static void sendMsgAmount(Player player, QuestsMessages qm, double amount) {
        final String msg = qm.getMessage(player);
        if (msg != null) player.sendMessage(msg.replace(REWARD_AMOUNT, String.valueOf(amount)));
    }

    /**
     * Sends a message with both amount and currency placeholders replaced.
     */
    private static void sendMsgAmountAndCurrency(Player player, QuestsMessages qm, double amount, String currencyName) {
        final String msg = qm.getMessage(player);
        if (msg != null) {
            player.sendMessage(
                    msg.replace(REWARD_AMOUNT, String.valueOf(amount))
                            .replace("%currencyName%", currencyName)
            );
        }
    }

    /* -------------------- Error handling -------------------- */

    /**
     * Logs and notifies the player that a required plugin is missing.
     */
    private static void rewardTypeError(Player player, RewardType type) {
        PluginLogger.error("Impossible to give the reward to " + player.getName() + ".");
        PluginLogger.error("Reward type is " + type + " but required plugin is not hooked.");
        player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
    }

    /**
     * Logs and notifies the player that Vault is missing.
     */
    private static void rewardTypeErrorWithVault(Player player, RewardType type) {
        PluginLogger.error("Impossible to give the reward to " + player.getName() + ".");
        PluginLogger.error("Reward type is " + type + " but Vault is not hooked.");
        player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
    }

    /**
     * Logs and notifies the player that no points plugin is hooked.
     */
    private static void rewardTypeErrorNoPoints(Player player, RewardType type) {
        PluginLogger.error("Impossible to give the reward to " + player.getName() + ".");
        PluginLogger.error("Reward type is " + type + " but no points plugin is hooked.");
        player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
    }

    /**
     * Logs and notifies the player that a CoinsEngine currency is invalid.
     */
    private static void currencyError(Player player, String currency) {
        PluginLogger.error("Impossible to give the reward to " + player.getName() + ".");
        PluginLogger.error("CoinsEngine currency '" + currency + "' not found.");
        player.sendMessage(ChatColor.RED + "Impossible to give you your reward. Please contact an administrator.");
    }
}