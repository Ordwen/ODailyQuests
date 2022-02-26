package com.ordwen.odailyquests.quests;

/**
 * List of all possible quests types
 */
public enum QuestType {
    BREAK("BREAK"),
    PLACE("PLACE"),
    CRAFT("CRAFT"),
    PICKUP("PICKUP"),
    LAUNCH("LAUNCH"),
    CONSUME("CONSUME"),
    GET("GET"),
    COOK("COOK"),
    ENCHANT("ENCHANT"),
    KILL("KILL"),
    FISH("FISH"),
    ;

    private final String typeName;

    /**
     * Type constructor.
     * @param typeName the name of the type.
     */
    QuestType(String typeName) {
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
