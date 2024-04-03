package com.ordwen.odailyquests.quests.types;

import com.ordwen.odailyquests.quests.ConditionType;

public class PlaceholderQuest extends AbstractQuest {

    private final String placeholder;
    private final ConditionType conditionType;
    private final String expectedValue;
    private final String errorMessage;

    public PlaceholderQuest(BasicQuest base, String placeholder, ConditionType conditionType, String expectedValue, String errorMessage) {
        super(base);

        this.placeholder = placeholder;
        this.conditionType = conditionType;
        this.expectedValue = expectedValue;
        this.errorMessage = errorMessage;
    }

    /**
     * Get the placeholder required by the quest.
     * @return quest required placeholder.
     */
    public String getPlaceholder() {
        return this.placeholder;
    }

    /**
     * Get the condition type required by the quest.
     * @return ConditionType object.
     */
    public ConditionType getConditionType() {
        return this.conditionType;
    }

    /**
     * Get the expected value required by the quest.
     * @return quest expected value.
     */
    public String getExpectedValue() {
        return this.expectedValue;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
