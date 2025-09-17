package com.ordwen.odailyquests.quests.player.progression.clickable.commands;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestCommand;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestContext;
import com.ordwen.odailyquests.quests.types.inventory.PlaceholderQuest;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.ChatColor;

import java.time.Duration;

public class PlaceholderQuestCommand extends QuestCommand<PlaceholderQuest> {

    public PlaceholderQuestCommand(QuestContext context, Progression progression, PlaceholderQuest quest) {
        super(context, progression, quest);
    }

    /**
     * Validate PLACEHOLDER quest type.
     */
    @Override
    public void execute() {
        final var player = context.getPlayer();

        if (!quest.isAllowedToProgress(player, quest)) return;

        if (!TextFormatter.isPlaceholderAPIEnabled()) {
            sendMessage(QuestsMessages.PLACEHOLDER_API_NOT_ENABLED);
            return;
        }

        final String placeholderValue = TextFormatter.format(player, quest.getPlaceholder());
        final String expectedValue = TextFormatter.format(player, quest.getExpectedValue());

        final boolean isValid = validateCondition(placeholderValue, expectedValue);

        if (isValid) {
            completeQuest();
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', quest.getErrorMessage()));
        }
    }

    private boolean validateCondition(String placeholderValue, String expectedValue) {
        try {
            return switch (quest.getConditionType()) {
                case EQUALS -> placeholderValue.equals(expectedValue);
                case NOT_EQUALS -> !placeholderValue.equals(expectedValue);
                case GREATER_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS ->
                        validateNumericCondition(placeholderValue, expectedValue);
                case DURATION_GREATER_THAN, DURATION_GREATER_THAN_OR_EQUALS, DURATION_LESS_THAN,
                     DURATION_LESS_THAN_OR_EQUALS -> validateDurationCondition(placeholderValue, expectedValue);
            };
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            handleValidationError(placeholderValue);
            return false;
        }
    }

    private boolean validateNumericCondition(String placeholderValue, String expectedValue) {
        float current = Float.parseFloat(placeholderValue.replace(",", "."));
        float expected = Float.parseFloat(expectedValue.replace(",", "."));

        return switch (quest.getConditionType()) {
            case GREATER_THAN -> current > expected;
            case GREATER_THAN_OR_EQUALS -> current >= expected;
            case LESS_THAN -> current < expected;
            case LESS_THAN_OR_EQUALS -> current <= expected;
            default -> false;
        };
    }

    private boolean validateDurationCondition(String placeholderValue, String expectedValue) {
        Duration currentDuration = parseDuration(placeholderValue);
        Duration expectedDuration = parseDuration(expectedValue);

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
            context.getPlayer().sendMessage(message);
        }
    }
}
