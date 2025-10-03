package com.ordwen.odailyquests.quests.conditions.placeholder;

import com.ordwen.odailyquests.quests.conditions.ConditionOperator;

/**
 * Represents a single placeholder-based condition that can be evaluated
 * against a player.
 * <p>
 * A condition defines:
 * <ul>
 *   <li>the placeholder expression to resolve,</li>
 *   <li>the type of comparison to apply,</li>
 *   <li>the expected value for comparison,</li>
 *   <li>and an optional error message to display if the condition fails.</li>
 * </ul>
 *
 * <p>Instances of this record are immutable.
 *
 * @param placeholder       the placeholder expression to evaluate (e.g. {@code "%player_level%"})
 * @param conditionOperator the type of condition to check (e.g. equals, greater than, etc.)
 * @param expectedValue     the expected value to compare against
 * @param errorMessage      the message to display if the condition fails (may be null or empty)
 */
public record PlaceholderCondition(
        String placeholder,
        ConditionOperator conditionOperator,
        String expectedValue,
        String errorMessage
) {
}
