package com.ordwen.odailyquests.enums;

import com.ordwen.odailyquests.configuration.essentials.Prefix;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@SuppressWarnings("SpellCheckingInspection")
public enum QuestsMessages {

    PLAYER_HELP("player_help", """
            &a&nPlayer commands:
            &e/dq me &a: see your own quests
            &e/dq reroll <index> &a: reroll a quest (requires permission)
            &e/dq show <global/easy/medium/hard> &a: see the quests of a category
            """),
    ADMIN_HELP("admin_help", """
            &c&nAdmin commands:
            &e/dqa complete <player> <index> &a: complete a player quest
            &e/dqa customcomplete <player> <index> <amount> &a: complete a player quest from a custom type
            &e/dqa reset <quests/total> <player> &a: draw new quests for a player, or reset his total number of achieved quests.
            &e/dqa add total <player> <amount> &a: add a number of achieved quests to a player.
            &e/dqa reroll <player> <index> &a: draw a new quest for a player, at a specific index.
            &e/dqa show <player> &a: see quests of a player
            """),

    NO_PERMISSION("no_permission", "&cYou don't have permission."),
    NO_PERMISSION_CATEGORY("no_permission_category", "&cYou don't have permission to see this category."),
    PLAYER_ONLY("player_only", "&cOnly player can execute this command."),
    INVALID_CATEGORY("invalid_category", "&cInvalid quest category."),
    INVALID_PLAYER("invalid_player", "&cThis player doesn't exist, or is offline."),
    INVALID_QUEST_ID("invalid_quest_id", "&cYou must specify a valid quest ID."),
    INVALID_QUEST_INDEX("invalid_quest_index", "&cThe specified index is invalid."),
    INVALID_AMOUNT("invalid_amount", "&cThe specified amount is invalid."),

    QUEST_ALREADY_ACHIEVED("already_achieved", "&cThis quest is already achieved."),
    QUESTS_IN_PROGRESS("quests_in_progress", "&eYou still have daily quests to complete !"),
    ALL_QUESTS_ACHIEVED_CONNECT("all_quests_achieved_connect", "&aYou have completed all your daily quests !"),
    QUESTS_RENEWED("quests_renewed", "&aYou have new daily quests to complete !"),
    QUESTS_RENEWED_ADMIN("quests_renewed_admin", "&eYou have reset the quests of %target%."),
    QUEST_REROLLED("quest_rerolled", "&aYou have rerolled your quest number %index% !"),
    QUEST_REROLLED_ADMIN("quest_rerolled_admin", "&eYou have rerolled the quest number %index% of %target%."),
    ADD_TOTAL_ADMIN("add_total_admin", "&eYou have added %amount% to %target%'s total number of completed quests."),
    ADD_TOTAL_TARGET("add_total_target", "&eAn admin has added %amount% to your total number of completed quests."),
    QUEST_ACHIEVED("quest_achieved", "&aYou finished the quest &e%questName%&a, well done !"),
    ALL_QUESTS_ACHIEVED("all_quests_achieved", "&aYou have finished all your daily quests, well done !"),
    CATEGORY_QUESTS_ACHIEVED("category_quests_achieved", "&aYou have completed all your %category% quests!"),
    NOT_ENOUGH_ITEM("not_enough_items", "&cYou don't have the required amount to complete this quest."),
    TOTAL_AMOUNT_RESET("total_amount_reset", "&eYour total number of completed quests has been reset by an admin."),
    TOTAL_AMOUNT_RESET_ADMIN("total_amount_reset_admin", "&e%target%'s total number of completed quests has been reset by an admin."),

    WORLD_DISABLED("world_disabled", "&cYou can't complete quests in this world."),
    NOT_REQUIRED_WORLD("not_required_world", "&cYou can't complete this quest in this world."),
    NOT_REQUIRED_REGION("not_required_region", "&cYou can't complete this quest in this region."),

    REWARD_COMMAND("reward_command", "&aYou receive some rewards commands."),
    REWARD_EXP_LEVELS("reward_exp_levels", "&aYou receive &e%rewardAmount% &bEXP &alevels."),
    REWARD_EXP_POINTS("reward_exp_points", "&aYou receive &e%rewardAmount% &bEXP &apoints."),
    REWARD_MONEY("reward_money", "&aYou receive &e%rewardAmount% &b$&a."),
    REWARD_POINTS("reward_points", "&aYou receive &e%rewardAmount% &bpoints&a."),
    REWARD_COINS_ENGINE("reward_coins_engine", "&aYou receive &e%rewardAmount% &b%currencyName%&a."),

    NEW_DAY("new_day", "&6It's a new day!"),
    TOO_FAR_FROM_LOCATION("too_far", "&cYou are too far from the required location."),
    BAD_WORLD_LOCATION("bad_world", "&cYou are in the wrong world."),

    PLACEHOLDER_API_NOT_ENABLED("placeholder_api_not_enabled", "&cThe PlaceholderAPI plugin is not enabled. Please inform an administrator."),
    PLACEHOLDER_NOT_NUMBER("placeholder_not_number", "&cThe placeholder %placeholder% is not a number. Please inform an administrator."),

    CANNOT_COMPLETE_QUEST_WITH_OFF_HAND("cannot_complete_quest_with_off_hand", "&cAll required items must be in your inventory, not in your off hand."),
    CANNOT_REROLL_IF_ACHIEVED("cannot_reroll_if_achieved", "&cYou can't reroll a quest that you have already achieved!"),
    ;

    private final String path;
    private final String defaultMessage;
    private static FileConfiguration lang;

    /**
     * Message constructor.
     *
     * @param message message (String).
     */
    QuestsMessages(String path, String message) {
        this.path = path;
        this.defaultMessage = message;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     *
     * @param messagesFile the config to set.
     */
    public static void setFile(FileConfiguration messagesFile) {
        lang = messagesFile;
    }

    /**
     * Get message.
     *
     * @return message.
     */
    @Override
    public String toString() {
        String msg = lang.getString(this.path, defaultMessage);

        if (msg.trim().isEmpty()) return "";
        else return TextFormatter.format(null, Prefix.getPrefix() + msg);
    }

    public String getMessage(Player player) {
        String msg = lang.getString(this.path, defaultMessage);

        if (msg.trim().isEmpty()) return null;
        else return TextFormatter.format(player,Prefix.getPrefix() + msg);
    }

    public String getMessage(String playerName) {
        final Player player = Bukkit.getPlayer(playerName);
        if (player == null) return null;

        String msg = lang.getString(this.path, defaultMessage);
        if (msg.trim().isEmpty()) return null;

        else return TextFormatter.format(player,Prefix.getPrefix() + msg);
    }

    /**
     * Get the default value of the path.
     *
     * @return the default value of the path.
     */
    public String getDefault() {
        return this.defaultMessage;
    }

    /**
     * Get the path to the string.
     *
     * @return the path to the string.
     */
    public String getPath() {
        return this.path;
    }
}
