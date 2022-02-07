package com.ordwen.odailyquests.enums;

public enum QuestsMessages {

    NO_PERMISSION("You don't have permission."),
    INVALID_SYNTAX("Invalid syntax."),

    INVALID_CATEGORY("Invalid quest category"),

    QUESTS_IN_PROGRESS("You still have daily quests to complete !"),
    QUESTS_RENEWED("You have new daily quests to complete !");

    private String message;

    /**
     * Message constructor.
     * @param message message (String).
     */
    QuestsMessages(String message) {
        this.message = message;
    }

    /**
     * Get message.
     * @return message.
     */
    public String getMessage() {
        return this.message;
    }
}
