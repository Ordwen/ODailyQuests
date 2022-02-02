package com.ordwen.odailyquests.quest;

/**
 * List of all possible quests types
 */
public enum Type {
    BREAK("BREAK"),
    PLACE("PLACE"),
    CRAFT("CRAFT"),
    GET("GET"),
    USE("USE");

    private final String typeName;

    /**
     * Type constructor.
     * @param typeName the name of the type.
     */
    Type(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Get the user-configured name for a type.
     * @return the name of the type.
     */
    public String getTypeName() {
        return this.typeName;
    }
}
