package com.ordwen.odailyquests.enums;

public enum QuestsPermissions {

    QUEST_USE("odailyquests.use"), // allow using /quests command
    QUEST_SHOW("odailyquests.show"), // allow using /quests show command
    QUESTS_SHOW_PLAYER("odailyquests.player"),
    QUESTS_SHOW_GLOBAL("odailyquests.global"),
    QUESTS_SHOW_EASY("odailyquests.easy"),
    QUESTS_SHOW_MEDIUM("odailyquests.medium"),
    QUESTS_SHOW_HARD("odailyquests.hard"),
    QUESTS_ADMIN("odailyquests.admin"),
    ;

    private String permission;

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
