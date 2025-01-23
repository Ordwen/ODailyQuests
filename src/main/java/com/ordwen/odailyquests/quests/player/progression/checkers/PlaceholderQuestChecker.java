package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.inventory.PlaceholderQuest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;

public class PlaceholderQuestChecker extends QuestChecker<PlaceholderQuest> {

    public PlaceholderQuestChecker(Player player, Progression progression, PlaceholderQuest quest) {
        super(player, progression, quest);
    }

    /**
     * Validate PLACEHOLDER quest type.
     */
    @Override
    public void validateAndComplete() {
        if (!quest.isAllowedToProgress(player, quest)) return;

        if (!PAPIHook.isPlaceholderAPIHooked()) {
            sendMessage(QuestsMessages.PLACEHOLDER_API_NOT_ENABLED);
            return;
        }

        final String placeholderValue = PAPIHook.getPlaceholders(player, quest.getPlaceholder());
        boolean isValid = validateCondition(placeholderValue);

        if (isValid) {
            completeQuest();
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', quest.getErrorMessage()));
        }
    }

    private boolean validateCondition(String placeholderValue) {
        try {
            return switch (quest.getConditionType()) {
                case EQUALS -> placeholderValue.equals(quest.getExpectedValue());
                case NOT_EQUALS -> !placeholderValue.equals(quest.getExpectedValue());
                case GREATER_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS ->
                        validateNumericCondition(placeholderValue);
                case DURATION_GREATER_THAN, DURATION_GREATER_THAN_OR_EQUALS, DURATION_LESS_THAN,
                     DURATION_LESS_THAN_OR_EQUALS -> validateDurationCondition(placeholderValue);
            };
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            handleValidationError(placeholderValue);
            return false;
        }
    }

    private boolean validateNumericCondition(String placeholderValue) {
        float currentValue = Float.parseFloat(placeholderValue);
        float expectedValue = Float.parseFloat(quest.getExpectedValue());

        return switch (quest.getConditionType()) {
            case GREATER_THAN -> currentValue > expectedValue;
            case GREATER_THAN_OR_EQUALS -> currentValue >= expectedValue;
            case LESS_THAN -> currentValue < expectedValue;
            case LESS_THAN_OR_EQUALS -> currentValue <= expectedValue;
            default -> false;
        };
    }

    private boolean validateDurationCondition(String placeholderValue) {
        Duration currentDuration = parseDuration(placeholderValue);
        Duration expectedDuration = parseDuration(quest.getExpectedValue());

        return switch (quest.getConditionType()) {
            case DURATION_GREATER_THAN -> currentDuration.compareTo(expectedDuration) > 0;
            case DURATION_GREATER_THAN_OR_EQUALS -> currentDuration.compareTo(expectedDuration) >= 0;
            case DURATION_LESS_THAN -> currentDuration.compareTo(expectedDuration) < 0;
            case DURATION_LESS_THAN_OR_EQUALS -> currentDuration.compareTo(expectedDuration) <= 0;
            default -> false;
        };
    }

    private Duration parseDuration(String value) {
        String[] parts = value.split(":");
        return Duration.ofHours(Long.parseLong(parts[0]))
                .plusMinutes(Long.parseLong(parts[1]))
                .plusSeconds(Long.parseLong(parts[2]))
                .plusMillis(Long.parseLong(parts[3]));
    }

    private void handleValidationError(String placeholder) {
        String message = QuestsMessages.PLACEHOLDER_NOT_NUMBER.toString();
        if (message != null) {
            message = message.replace("%placeholder%", placeholder);
            player.sendMessage(message);
        }
    }
}
