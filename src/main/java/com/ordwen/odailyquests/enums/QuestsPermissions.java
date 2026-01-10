package com.ordwen.odailyquests.enums;

/**
 * All plugin permissions, even if some are used only once.
 * Centralized for clarity and future-proofing.
 */
public enum QuestsPermissions {

    QUESTS_ADMIN("odailyquests.admin"),

    QUESTS_PROGRESS("odailyquests.progress"),
    QUESTS_PLAYER_USE("odailyquests.use"),
    QUESTS_PLAYER_REROLL("odailyquests.reroll"),
    QUESTS_PLAYER_SHOW("odailyquests.show"),
    QUESTS_PLAYER_BYPASS_SPAWNER("odailyquests.bypass.spawner"),
    QUESTS_PLAYER_BYPASS_REROLL_LIMIT("odailyquests.bypass.rerolllimit")
    ;

    private final String permission;

    /**
     * Permission constructor.
     * @param permission permission (String).
     */
    QuestsPermissions(String permission) {
        this.permission = permission;
    }

    /**
     * Get permission.
     * @return permission.
     */
    public String get() {
        return this.permission;
    }
}
