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
    TAME("TAME"),
    BREED("BREED"),
    SHEAR("SHEAR"),
    MILKING("MILKING"),

    EXP_POINTS("EXP_POINTS"),
    EXP_LEVELS("EXP_LEVELS"),
    VILLAGER_TRADE("VILLAGER_TRADE"),

    CUSTOM_MOBS("CUSTOM_MOBS"),
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
