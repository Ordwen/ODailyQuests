package com.ordwen.odailyquests.quests.conditions;

import com.ordwen.odailyquests.tools.PluginLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a condition that must be met for a quest to be available to a player.
 * Uses PlaceholderAPI to evaluate conditions based on player data.
 */
public class QuestCondition {

    private final String placeholder;
    private final ConditionOperator operator;
    private final String expectedValue;
    private final String errorMessage;

    /**
     * Creates a new quest condition.
     *
     * @param placeholder   the PlaceholderAPI placeholder to check
     * @param operator     the comparison operator
     * @param expectedValue the expected value to compare against
     * @param errorMessage  the error message to display if condition fails
     */
    public QuestCondition(String placeholder, ConditionOperator operator, String expectedValue, String errorMessage) {
        this.placeholder = placeholder;
        this.operator = operator;
        this.expectedValue = expectedValue;
        this.errorMessage = errorMessage;
    }

    /**
     * Checks if the condition is met for the given player.
     *
     * @param player the player to check the condition for
     * @return true if the condition is met, false otherwise
     */
    public boolean isMet(Player player) {
        if (player == null) return false;

        try {
            String actualValue = PlaceholderAPI.setPlaceholders(player, placeholder);
            
            // Handle numeric comparisons
            if (operator.isNumeric()) {
                return evaluateNumeric(actualValue, expectedValue);
            }
            
            // Handle string comparisons
            return evaluateString(actualValue, expectedValue);
            
        } catch (Exception e) {
            PluginLogger.error("Error evaluating quest condition: " + e.getMessage());
            return false;
        }
    }

    /**
     * Evaluates numeric comparisons.
     */
    private boolean evaluateNumeric(String actualValue, String expectedValue) {
        try {
            double actual = Double.parseDouble(actualValue);
            double expected = Double.parseDouble(expectedValue);
            
            return switch (operator) {
                case GREATER_THAN -> actual > expected;
                case GREATER_THAN_OR_EQUAL -> actual >= expected;
                case LESS_THAN -> actual < expected;
                case LESS_THAN_OR_EQUAL -> actual <= expected;
                default -> false;
            };
        } catch (NumberFormatException e) {
            PluginLogger.error("Invalid numeric value in quest condition: " + actualValue + " or " + expectedValue);
            return false;
        }
    }

    /**
     * Evaluates string comparisons.
     */
    private boolean evaluateString(String actualValue, String expectedValue) {
        return switch (operator) {
            case EQUALS -> actualValue.equals(expectedValue);
            case NOT_EQUALS -> !actualValue.equals(expectedValue);
            case CONTAINS -> actualValue.contains(expectedValue);
            case NOT_CONTAINS -> !actualValue.contains(expectedValue);
            case STARTS_WITH -> actualValue.startsWith(expectedValue);
            case ENDS_WITH -> actualValue.endsWith(expectedValue);
            default -> false;
        };
    }

    /**
     * Gets the error message for this condition.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the placeholder being checked.
     *
     * @return the placeholder
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Gets the operator being used.
     *
     * @return the operator
     */
    public ConditionOperator getOperator() {
        return operator;
    }

    /**
     * Gets the expected value.
     *
     * @return the expected value
     */
    public String getExpectedValue() {
        return expectedValue;
    }

    /**
     * Loads a list of conditions from a configuration section.
     *
     * @param section the configuration section containing conditions
     * @return a list of QuestCondition objects
     */
    public static List<QuestCondition> loadConditions(ConfigurationSection section) {
        List<QuestCondition> conditions = new ArrayList<>();
        
        if (section == null) return conditions;

        for (String key : section.getKeys(false)) {
            QuestCondition condition = createConditionFromSection(section.getConfigurationSection(key), key);
            if (condition != null) {
                conditions.add(condition);
            }
        }

        return conditions;
    }

    /**
     * Creates a QuestCondition from a configuration section.
     *
     * @param conditionSection the configuration section for the condition
     * @param key the key of the condition
     * @return the created QuestCondition, or null if invalid
     */
    private static QuestCondition createConditionFromSection(ConfigurationSection conditionSection, String key) {
        if (conditionSection == null) {
            return null;
        }

        String placeholder = conditionSection.getString("placeholder");
        String operatorStr = conditionSection.getString("operator");
        String expectedValue = conditionSection.getString("expected");
        String errorMessage = conditionSection.getString("error_message", "Condition not met");

        if (placeholder == null || operatorStr == null || expectedValue == null) {
            PluginLogger.error("Invalid quest condition configuration: missing required fields");
            return null;
        }

        ConditionOperator operator = ConditionOperator.fromString(operatorStr);
        if (operator == null) {
            PluginLogger.error("Invalid operator in quest condition: " + operatorStr);
            return null;
        }

        return new QuestCondition(placeholder, operator, expectedValue, errorMessage);
    }

    /**
     * Checks if all conditions are met for a player.
     *
     * @param conditions the list of conditions to check
     * @param player     the player to check conditions for
     * @return true if all conditions are met, false otherwise
     */
    public static boolean areAllConditionsMet(List<QuestCondition> conditions, Player player) {
        if (conditions == null || conditions.isEmpty()) return true;
        
        for (QuestCondition condition : conditions) {
            if (!condition.isMet(player)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the first failed condition message for a player.
     *
     * @param conditions the list of conditions to check
     * @param player     the player to check conditions for
     * @return the error message of the first failed condition, or null if all conditions are met
     */
    public static String getFirstFailedConditionMessage(List<QuestCondition> conditions, Player player) {
        if (conditions == null || conditions.isEmpty()) return null;
        
        for (QuestCondition condition : conditions) {
            if (!condition.isMet(player)) {
                return condition.getErrorMessage();
            }
        }
        return null;
    }
} 
