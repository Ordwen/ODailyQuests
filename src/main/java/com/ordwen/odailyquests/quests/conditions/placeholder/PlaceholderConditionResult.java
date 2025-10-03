package com.ordwen.odailyquests.quests.conditions.placeholder;

/**
 * Represents the outcome of evaluating a placeholder condition.
 * <p>
 * A result may indicate that the condition was successfully matched,
 * that it failed to match, or that it could not be evaluated due to
 * an invalid input format (for example, when parsing a number or a duration fails).
 * </p>
 *
 * @param matched       {@code true} if the condition was satisfied
 * @param invalidFormat {@code true} if the evaluation failed due to invalid input format
 */
public record PlaceholderConditionResult(boolean matched, boolean invalidFormat) {

    /**
     * Creates a result indicating that the condition was satisfied.
     *
     * @return a success result
     */
    public static PlaceholderConditionResult successResult() {
        return new PlaceholderConditionResult(true, false);
    }

    /**
     * Creates a result indicating that the condition was evaluated but did not match.
     *
     * @return a failure result
     */
    public static PlaceholderConditionResult failureResult() {
        return new PlaceholderConditionResult(false, false);
    }

    /**
     * Creates a result indicating that the condition could not be evaluated
     * because the input values were not in a valid format.
     *
     * @return an invalid format result
     */
    public static PlaceholderConditionResult invalidFormatResult() {
        return new PlaceholderConditionResult(false, true);
    }

    /**
     * Creates either a success or failure result depending on the given flag.
     *
     * @param matched whether the condition was satisfied
     * @return a success result if {@code matched} is true, otherwise a failure result
     */
    public static PlaceholderConditionResult of(boolean matched) {
        return matched ? successResult() : failureResult();
    }
}
