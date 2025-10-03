package com.ordwen.odailyquests.quests.conditions.placeholder;

import com.ordwen.odailyquests.quests.conditions.ConditionOperator;

import java.time.Duration;

/**
 * Utility class that evaluates placeholder conditions of different types.
 * <p>
 * A condition compares a placeholder value with an expected value using
 * operators such as equality, numeric comparison, or duration comparison.
 * The evaluator attempts to parse the values into the appropriate type
 * (string, number, or duration) and returns a {@link PlaceholderConditionResult}
 * describing whether the comparison matched or failed, or whether the format
 * of the inputs was invalid.
 * </p>
 * <p>
 * This class is not meant to be instantiated; all methods are static.
 * </p>
 */
public final class PlaceholderConditionEvaluator {

    private PlaceholderConditionEvaluator() {
    }

    /**
     * Evaluates a condition of the given type by comparing a placeholder value
     * with an expected value. Supports string equality, numeric comparison,
     * and duration comparison.
     *
     * @param type            the type of condition to evaluate
     * @param placeholderValue the actual placeholder value (string form)
     * @param expectedValue    the expected value (string form)
     * @return a result indicating whether the condition matched, failed, or
     *         could not be evaluated due to invalid input format
     */
    public static PlaceholderConditionResult evaluate(ConditionOperator type, String placeholderValue, String expectedValue) {
        try {
            final boolean equals = placeholderValue.trim().equals(expectedValue.trim());
            return switch (type) {
                case EQUALS -> PlaceholderConditionResult.of(equals);
                case NOT_EQUALS -> PlaceholderConditionResult.of(!equals);

                case CONTAINS -> PlaceholderConditionResult.of(placeholderValue.contains(expectedValue));
                case NOT_CONTAINS -> PlaceholderConditionResult.of(!placeholderValue.contains(expectedValue));
                case STARTS_WITH -> PlaceholderConditionResult.of(placeholderValue.startsWith(expectedValue));
                case ENDS_WITH -> PlaceholderConditionResult.of(placeholderValue.endsWith(expectedValue));

                case GREATER_THAN,
                     GREATER_THAN_OR_EQUALS,
                     LESS_THAN,
                     LESS_THAN_OR_EQUALS ->
                        evaluateNumeric(type, placeholderValue, expectedValue);

                case DURATION_GREATER_THAN,
                     DURATION_GREATER_THAN_OR_EQUALS,
                     DURATION_LESS_THAN,
                     DURATION_LESS_THAN_OR_EQUALS ->
                        evaluateDuration(type, placeholderValue, expectedValue);
            };
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return PlaceholderConditionResult.invalidFormatResult();
        }
    }

    /**
     * Evaluates a numeric condition. Both values are parsed as floats,
     * after replacing commas with dots for decimal compatibility.
     *
     * @param type            the numeric condition type
     * @param placeholderValue the actual value to parse
     * @param expectedValue    the expected value to parse
     * @return a result indicating whether the numeric comparison matched
     * @throws NumberFormatException if either value cannot be parsed as a number
     */
    private static PlaceholderConditionResult evaluateNumeric(ConditionOperator type, String placeholderValue, String expectedValue) {
        float current = Float.parseFloat(placeholderValue.replace(",", "."));
        float expected = Float.parseFloat(expectedValue.replace(",", "."));

        return switch (type) {
            case GREATER_THAN -> PlaceholderConditionResult.of(current > expected);
            case GREATER_THAN_OR_EQUALS -> PlaceholderConditionResult.of(current >= expected);
            case LESS_THAN -> PlaceholderConditionResult.of(current < expected);
            case LESS_THAN_OR_EQUALS -> PlaceholderConditionResult.of(current <= expected);
            default -> PlaceholderConditionResult.failureResult();
        };
    }

    /**
     * Evaluates a duration condition. Both values are parsed into
     * {@link Duration} instances and compared.
     *
     * @param type            the duration condition type
     * @param placeholderValue the actual duration value in string format
     * @param expectedValue    the expected duration value in string format
     * @return a result indicating whether the duration comparison matched
     * @throws NumberFormatException             if any duration part is not a number
     * @throws ArrayIndexOutOfBoundsException    if the string does not have the expected format
     */
    private static PlaceholderConditionResult evaluateDuration(ConditionOperator type, String placeholderValue, String expectedValue) {
        Duration current = parseDuration(placeholderValue);
        Duration expected = parseDuration(expectedValue);

        return switch (type) {
            case DURATION_GREATER_THAN -> PlaceholderConditionResult.of(current.compareTo(expected) > 0);
            case DURATION_GREATER_THAN_OR_EQUALS -> PlaceholderConditionResult.of(current.compareTo(expected) >= 0);
            case DURATION_LESS_THAN -> PlaceholderConditionResult.of(current.compareTo(expected) < 0);
            case DURATION_LESS_THAN_OR_EQUALS -> PlaceholderConditionResult.of(current.compareTo(expected) <= 0);
            default -> PlaceholderConditionResult.failureResult();
        };
    }

    /**
     * Parses a duration string into a {@link Duration}.
     * <p>
     * The expected format is {@code "hours:minutes:seconds:millis"}.
     * Each part must be a valid integer.
     * </p>
     *
     * @param value the duration string to parse
     * @return the corresponding {@link Duration}
     * @throws NumberFormatException if any part cannot be parsed as a number
     * @throws ArrayIndexOutOfBoundsException if the string does not contain exactly four parts
     */
    private static Duration parseDuration(String value) {
        String[] parts = value.trim().split(":");
        if (parts.length < 2 || parts.length > 4) throw new ArrayIndexOutOfBoundsException();
        long h = Long.parseLong(parts[0]);
        long m = Long.parseLong(parts[1]);
        long s = parts.length >= 3 ? Long.parseLong(parts[2]) : 0L;
        long ms = parts.length == 4 ? Long.parseLong(parts[3]) : 0L;
        return Duration.ofHours(h).plusMinutes(m).plusSeconds(s).plusMillis(ms);
    }
}