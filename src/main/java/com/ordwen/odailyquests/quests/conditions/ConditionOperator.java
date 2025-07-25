package com.ordwen.odailyquests.quests.conditions;

/**
 * Represents the comparison operators that can be used in quest conditions.
 */
public enum ConditionOperator {
    
    // String operators
    EQUALS("equals", false),
    NOT_EQUALS("not_equals", false),
    CONTAINS("contains", false),
    NOT_CONTAINS("not_contains", false),
    STARTS_WITH("starts_with", false),
    ENDS_WITH("ends_with", false),
    
    // Numeric operators
    GREATER_THAN(">", true),
    GREATER_THAN_OR_EQUAL(">=", true),
    LESS_THAN("<", true),
    LESS_THAN_OR_EQUAL("<=", true);

    private final String stringValue;
    private final boolean numeric;

    ConditionOperator(String stringValue, boolean numeric) {
        this.stringValue = stringValue;
        this.numeric = numeric;
    }

    /**
     * Gets the string representation of this operator.
     *
     * @return the string value
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Checks if this operator is numeric.
     *
     * @return true if numeric, false otherwise
     */
    public boolean isNumeric() {
        return numeric;
    }

    /**
     * Converts a string to a ConditionOperator.
     *
     * @param value the string value to convert
     * @return the ConditionOperator, or null if not found
     */
    public static ConditionOperator fromString(String value) {
        if (value == null) return null;
        
        for (ConditionOperator operator : values()) {
            if (operator.getStringValue().equalsIgnoreCase(value)) {
                return operator;
            }
        }
        return null;
    }

    /**
     * Gets all available operators as strings.
     *
     * @return array of operator strings
     */
    public static String[] getAvailableOperators() {
        ConditionOperator[] operators = values();
        String[] result = new String[operators.length];
        
        for (int i = 0; i < operators.length; i++) {
            result[i] = operators[i].getStringValue();
        }
        
        return result;
    }

    /**
     * Gets all numeric operators as strings.
     *
     * @return array of numeric operator strings
     */
    public static String[] getNumericOperators() {
        return java.util.Arrays.stream(values())
                .filter(ConditionOperator::isNumeric)
                .map(ConditionOperator::getStringValue)
                .toArray(String[]::new);
    }

    /**
     * Gets all string operators as strings.
     *
     * @return array of string operator strings
     */
    public static String[] getStringOperators() {
        return java.util.Arrays.stream(values())
                .filter(op -> !op.isNumeric())
                .map(ConditionOperator::getStringValue)
                .toArray(String[]::new);
    }
} 