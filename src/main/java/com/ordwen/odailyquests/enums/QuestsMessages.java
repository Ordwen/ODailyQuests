package com.ordwen.odailyquests.enums;

import org.bukkit.ChatColor;

public enum QuestsMessages {

    NO_PERMISSION("You don't have permission."),
    INVALID_SYNTAX("Invalid syntax."),

    INVALID_CATEGORY("Invalid quest category"),

    QUESTS_IN_PROGRESS("&eYou still have daily quests to complete !"),
    QUESTS_RENEWED("&aYou have new daily quests to complete !");

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
        return ChatColor.translateAlternateColorCodes('&', this.message);
    }
}
