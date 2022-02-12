package com.ordwen.odailyquests.enums;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@SuppressWarnings("SpellCheckingInspection")
public enum QuestsMessages {

    NO_PERMISSION("no_permission", "&cYou don't have permission."),
    PLAYER_ONLY("player_only", "&cOnly player can execute this command."),
    INVALID_SYNTAX("invalid_syntax", "&cInvalid syntax."),
    INVALID_CATEGORY("invalid_category", "&cInvalid quest category."),
    CATEGORIZED_DISABLED("categorized_disabled","&cCategorized quests are disabled, only the global menu is available."),
    INVALID_PLAYER("invalid_player", "&cThis player doesn't exist, or is offline."),
    INVALID_QUEST_ID("invalid_quest_id","&cYou must specify a valid quest ID, between 1 and 3."),
    QUEST_ALREADY_ACHIEVED("already_achieved", "&cThis quest is already achieved."),

    QUESTS_IN_PROGRESS("quests_in_progress", "&eYou still have daily quests to complete !"),
    QUESTS_RENEWED("quests_renewed", "&aYou have new daily quests to complete !"),
    QUEST_ACHIEVED("quest_achieved", "&aYou finished the quest &e%questName%&a, well done !"),
    NOT_ENOUGH_ITEM("not_enough_items","&cYou don't have the required amount to complete this quest."),

    REWARD_COMMAND("reward_command", "&aYou receive some rewards commands."),
    REWARD_EXP("reward_exp", "&aYou receive &e%rewardAmount% &bEXP&a."),
    REWARD_MONEY("reward_money", "&aYou receive &e%rewardAmount% &b$&a."),
    REWARD_POINTS("reward_points", "&aYou receive &e%rewardAmount% &bpoints&a."),
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
