package com.ordwen.odailyquests.quests.conditions.placeholder;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Utility class responsible for evaluating a list of placeholder-based conditions
 * against a player. Each condition uses PlaceholderAPI to resolve values and checks
 * whether they match an expected value. If the evaluation fails, the player may
 * receive feedback messages depending on configuration.
 * <p>
 * The result of an evaluation is a simple {@code boolean} indicating whether all
 * conditions were satisfied.
 * </p>
 * <p>
 * This class is not meant to be instantiated; all methods are static.
 * </p>
 */
public final class PlaceholderRuleSetEvaluator {

    private PlaceholderRuleSetEvaluator() {
    }

    /**
     * Evaluates a list of placeholder conditions for a given player.
     * Ensures that PlaceholderAPI is available, checks each condition in turn,
     * and optionally sends feedback messages to the player if evaluation fails.
     *
     * @param player       the player for whom conditions are evaluated
     * @param conditions   the list of conditions to check (may be null or empty)
     * @param sendMessages whether to send feedback messages to the player
     * @return {@code true} if all conditions are satisfied; {@code false} otherwise
     */
    public static boolean evaluate(Player player, List<PlaceholderCondition> conditions, boolean sendMessages) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        if (!isPlaceholderApiReady(player, sendMessages)) {
            return false;
        }

        for (PlaceholderCondition condition : conditions) {
            final String placeholderValue = TextFormatter.format(player, condition.placeholder());
            final String expectedValue = TextFormatter.format(player, condition.expectedValue());

            final PlaceholderConditionResult result = PlaceholderConditionEvaluator.evaluate(condition.conditionOperator(), placeholderValue, expectedValue);

            if (result.invalidFormat()) {
                return handleInvalidFormat(player, sendMessages, placeholderValue);
            }

            if (!result.matched()) {
                return handleMismatch(player, sendMessages, condition);
            }
        }

        return true;
    }

    /**
     * Verifies whether PlaceholderAPI is available. If not, optionally informs the player.
     *
     * @param player       the player to notify
     * @param sendMessages whether to send a warning message
     * @return {@code true} if PlaceholderAPI is enabled; {@code false} otherwise
     */
    private static boolean isPlaceholderApiReady(Player player, boolean sendMessages) {
        if (TextFormatter.isPlaceholderAPIEnabled()) {
            return true;
        }
        if (sendMessages) {
            sendMessage(player, QuestsMessages.PLACEHOLDER_API_NOT_ENABLED.getMessage(player));
        }
        return false;
    }

    /**
     * Handles the case where a placeholder value cannot be parsed into the expected format.
     * Optionally sends a predefined error message to the player.
     *
     * @param player           the player to notify
     * @param sendMessages     whether to send a message
     * @param placeholderValue the raw placeholder value that failed to parse
     * @return always {@code false}, since evaluation cannot succeed in this case
     */
    private static boolean handleInvalidFormat(Player player, boolean sendMessages, String placeholderValue) {
        if (sendMessages) {
            final String template = QuestsMessages.PLACEHOLDER_NOT_NUMBER.toString();
            if (template != null) {
                player.sendMessage(template.replace("%placeholder%", placeholderValue));
            }
        }
        return false;
    }

    /**
     * Handles the case where a placeholder condition does not match the expected value.
     * Optionally sends the custom error message defined in the condition.
     *
     * @param player       the player to notify
     * @param sendMessages whether to send a message
     * @param condition    the condition that failed
     * @return always {@code false}, since evaluation failed
     */
    private static boolean handleMismatch(Player player, boolean sendMessages, PlaceholderCondition condition) {
        if (sendMessages) {
            final String rawMessage = condition.errorMessage();
            if (rawMessage != null && !rawMessage.isEmpty()) {
                sendMessage(player, TextFormatter.format(player, rawMessage));
            }
        }
        return false;
    }

    /**
     * Sends a message to the player if the message is not null or empty.
     *
     * @param player  the player to notify
     * @param message the message to send
     */
    private static void sendMessage(Player player, String message) {
        if (message != null && !message.isEmpty()) {
            player.sendMessage(message);
        }
    }
}