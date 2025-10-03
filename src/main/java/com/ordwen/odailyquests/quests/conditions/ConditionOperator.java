package com.ordwen.odailyquests.quests.conditions;

/**
 * Defines the types of conditions that can be applied when comparing
 * a placeholder value against an expected value.
 * <p>
 * Condition types may represent simple string equality, numeric comparisons,
 * or comparisons between durations expressed in a time format.
 * </p>
 */
public enum ConditionOperator {

    /** True if the placeholder value is exactly equal to the expected value. */
    EQUALS,

    /** True if the placeholder value is not equal to the expected value. */
    NOT_EQUALS,

    /** True if the placeholder value, interpreted as a string, contains the expected value as a substring. */
    CONTAINS,

    /** True if the placeholder value, interpreted as a string, does not contain the expected value as a substring. */
    NOT_CONTAINS,

    /** True if the placeholder value, interpreted as a string, starts with the expected value. */
    STARTS_WITH,

    /** True if the placeholder value, interpreted as a string, ends with the expected value. */
    ENDS_WITH,

    /** True if the placeholder value, interpreted as a number, is strictly greater than the expected value. */
    GREATER_THAN,

    /** True if the placeholder value, interpreted as a number, is strictly less than the expected value. */
    LESS_THAN,

    /** True if the placeholder value, interpreted as a number, is greater than or equal to the expected value. */
    GREATER_THAN_OR_EQUALS,

    /** True if the placeholder value, interpreted as a number, is less than or equal to the expected value. */
    LESS_THAN_OR_EQUALS,

    /** True if the placeholder value, interpreted as a duration, is strictly greater than the expected duration. */
    DURATION_GREATER_THAN,

    /** True if the placeholder value, interpreted as a duration, is strictly less than the expected duration. */
    DURATION_LESS_THAN,

    /** True if the placeholder value, interpreted as a duration, is greater than or equal to the expected duration. */
    DURATION_GREATER_THAN_OR_EQUALS,

    /** True if the placeholder value, interpreted as a duration, is less than or equal to the expected duration. */
    DURATION_LESS_THAN_OR_EQUALS
}