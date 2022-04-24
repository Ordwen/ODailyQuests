package com.ordwen.odailyquests.enums;

import com.ordwen.odailyquests.tools.ColorConvert;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SpellCheckingInspection")
public enum QuestsMessages {

    PLAYER_HELP("player_help", "&a&nPlayer commands:\n&e/quests me &a: see your own quests\n&e/quests show <global/easy/medium/hard> &a: see the quests of a category"),
    ADMIN_HELP("admin_help", "&c&nAdmin commands:\n&e/qadmin complete <player> <index> &a: complete a player quest\n&e/qadmin reset <player> &a: draw new quests for a player\n&e/qadmin show <player> &a: see quests of a player"),
    NO_PERMISSION("no_permission", "&cYou don't have permission."),
    NO_PERMISSION_CATEGORY("no_permission_category", "&cYou don't have permission to see this category."),
    PLAYER_ONLY("player_only", "&cOnly player can execute this command."),
    INVALID_CATEGORY("invalid_category", "&cInvalid quest category."),
    INVALID_PLAYER("invalid_player", "&cThis player doesn't exist, or is offline."),
    INVALID_QUEST_ID("invalid_quest_id","&cYou must specify a valid quest ID, between 1 and 3."),

    CATEGORIZED_DISABLED("categorized_disabled","&cCategorized quests are disabled, only the global menu is available."),
    GLOBAL_DISABLED("global_disabled", "&cGlobal quests are disabled, only the categorized menus are available."),
    QUEST_ALREADY_ACHIEVED("already_achieved", "&cThis quest is already achieved."),

    QUESTS_IN_PROGRESS("quests_in_progress", "&eYou still have daily quests to complete !"),
    QUESTS_RENEWED("quests_renewed", "&aYou have new daily quests to complete !"),
    QUEST_ACHIEVED("quest_achieved", "&aYou finished the quest &e%questName%&a, well done !"),
    ALL_QUESTS_ACHIEVED("all_quests_achieved", "&aYou have finished all your daily quests, well done !"),
    NOT_ENOUGH_ITEM("not_enough_items","&cYou don't have the required amount to complete this quest."),

    REWARD_COMMAND("reward_command", "&aYou receive some rewards commands."),
    REWARD_EXP_LEVELS("reward_exp_levels", "&aYou receive &e%rewardAmount% &bEXP &alevels."),
    REWARD_EXP_POINTS("reward_exp_points", "&aYou receive &e%rewardAmount% &bEXP &apoints."),
    REWARD_MONEY("reward_money", "&aYou receive &e%rewardAmount% &b$&a."),
    REWARD_POINTS("reward_points", "&aYou receive &e%rewardAmount% &bpoints&a."),
    ;

    private final String path;
    private final String defaultMessage;
    private static FileConfiguration LANG;

    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

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
        String msg = LANG.getString(this.path, defaultMessage);
        return ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(msg));
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
