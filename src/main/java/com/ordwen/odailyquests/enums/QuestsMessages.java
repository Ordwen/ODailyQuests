package com.ordwen.odailyquests.enums;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum QuestsMessages {

    NO_PERMISSION("no_permission", "You don't have permission."),
    INVALID_SYNTAX("invalid_syntax", "Invalid syntax."),
    INVALID_CATEGORY("invalid_category", "Invalid quest category."),

    QUESTS_IN_PROGRESS("quests_in_progress", "&eYou still have daily quests to complete !"),
    QUESTS_RENEWED("quests_renewed", "&aYou have new daily quests to complete !"),

    QUEST_ACHIEVED("quest_achieved", "&aYou finished the quest &e%questName% &a, well done !"),
    QUEST_REWARDED("quest_rewarded", "&aYou receive &e%rewardAmount% &b%rewardTypeLabel%&a."),
    ;

    private final String path;
    private final String defaultMessage;
    private static FileConfiguration LANG;

    /**
     * Message constructor.
     * @param message message (String).
     */
    QuestsMessages(String path, String message) {
        this.path = path;
        this.defaultMessage = message;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     * @param messagesFile the config to set.
     */
    public static void setFile(FileConfiguration messagesFile) {
        LANG = messagesFile;
    }

    /**
     * Get message.
     * @return message.
     */
    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, defaultMessage));
    }

    /**
     * Get the default value of the path.
     * @return the default value of the path.
     */
    public String getDefault() {
        return this.defaultMessage;
    }

    /**
     * Get the path to the string.
     * @return the path to the string.
     */
    public String getPath() {
        return this.path;
    }
}
