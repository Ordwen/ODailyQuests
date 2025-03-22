package com.ordwen.odailyquests.enums;

public enum QuestsPermissions {

    QUEST_USE("odailyquests.use"),
    QUEST_SHOW("odailyquests.show"),
    QUEST_REROLL("odailyquests.reroll"),
    QUESTS_ADMIN("odailyquests.admin"),
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
    public String getPermission() {
        return this.permission;
    }
}
