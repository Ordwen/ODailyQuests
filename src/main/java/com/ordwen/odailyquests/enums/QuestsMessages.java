package com.ordwen.odailyquests.enums;

import com.ordwen.odailyquests.configuration.essentials.Prefix;
import com.ordwen.odailyquests.files.implementations.MessagesFile;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public enum QuestsMessages {

    PLAYER_HELP("player_help", """
            &3&m---&3 Player commands &3&m---
            &3> &b/dq, /dq me &3- &7see your own quests
            &3> &b/dq reroll <index> &3- &7reroll a quest (requires permission)
            &3> &b/dq show <category> &3- &7see the quests of a category
            """),
    ADMIN_HELP("admin_help", """
            &c&m---&c Admin commands &c&m---
            &3> &b/dqa complete <player> <index> &3- &7complete a player quest
            &3> &b/dqa customcomplete <player> <type> <amount> &3- &7complete a player quest from a custom type
            &3> &b/dqa reset <quests/total> <player> &3- &7draw new quests for a player, or reset his total number of achieved quests
            &3> &b/dqa add total <player> <amount> &3- &7add a number of achieved quests to a player
            &3> &b/dqa add total <category> <player> <amount> &3- &7add a number of achieved quests in a specific category to a player
            &3> &b/dqa remove total <player> <amount> &3- &7remove a number of achieved quests to a player
            &3> &b/dqa remove total <category> <player> <amount> &3- &7remove a number of achieved quests in a specific category to a player
            &3> &b/dqa reroll <player> <index> &3- &7draw a new quest for a player, at a specific index
            &3> &b/dqa set <player> <slot> <category> <file index> &3- &7assign a specific quest to a player
            &3> &b/dqa show <player> &3- &7see quests of a player
            &3> &b/dqa open <player> &3- &7force a player to open the quest interface
            &3> &b/dqa convert <old format> <new format> &3- &7convert the storage format of the plugin
            """),

    NO_PERMISSION("no_permission", "&cYou don't have permission."),
    NO_PERMISSION_CATEGORY("no_permission_category", "&cYou don't have permission to see this category."),
    PLAYER_ONLY("player_only", "&cOnly player can execute this command."),
    INVALID_CATEGORY("invalid_category", "&cInvalid quest category."),
    INVALID_PLAYER("invalid_player", "&cThis player doesn't exist, or is offline."),
    INVALID_QUEST_ID("invalid_quest_id", "&cYou must specify a valid quest ID."),
    QUEST_ALREADY_ASSIGNED("quest_already_assigned", "&cThis quest is already assigned to the player."),
    INVALID_QUEST_INDEX("invalid_quest_index", "&cThe specified index is invalid."),
    INVALID_AMOUNT("invalid_amount", "&cThe specified amount is invalid."),
    PLAYER_QUESTS_NOT_LOADED("player_quests_not_loaded", "&cThe player's quests are not loaded yet. Please try again in a moment."),

    QUEST_ALREADY_ACHIEVED("already_achieved", "&cThis quest is already achieved."),
    QUESTS_IN_PROGRESS("quests_in_progress", "&eYou still have daily quests to complete !"),
    ALL_QUESTS_ACHIEVED_CONNECT("all_quests_achieved_connect", "&aYou have completed all your daily quests !"),
    QUESTS_RENEWED("quests_renewed", "&aYou have new daily quests to complete !"),
    QUESTS_RENEWED_ADMIN("quests_renewed_admin", "&eYou have reset the quests of %target%."),
    QUEST_REROLLED("quest_rerolled", "&aYou have rerolled your quest number %index% !"),
    QUEST_REROLLED_ADMIN("quest_rerolled_admin", "&eYou have rerolled the quest number %index% of %target%."),
    NO_AVAILABLE_QUESTS_IN_CATEGORY("no_available_quests_in_category", "&cThere are no available quests in this category to assign."),
    QUEST_SET_ADMIN("quest_set_admin", "&eYou have set quest %quest_id% (%quest%&r&e) in slot %slot% for %target% in %category%."),
    QUEST_SET_TARGET("quest_set_target", "&eAn admin set your quest number %slot% to %quest%&r&e (%quest_id%) in %category%."),
    ADD_TOTAL_ADMIN("add_total_admin", "&eYou have added %amount% to %target%'s total number of completed quests."),
    ADD_TOTAL_TARGET("add_total_target", "&eAn admin has added %amount% to your total number of completed quests."),
    ADD_TOTAL_CATEGORY_ADMIN("add_total_category_admin", "&eYou have added %amount% to %target%'s total number of completed quests in the category %category%."),
    ADD_TOTAL_CATEGORY_TARGET("add_total_category_target", "&eAn admin has added %amount% to your total number of completed quests in the category %category%."),
    REMOVE_TOTAL_ADMIN("remove_total_admin", "&eYou have removed %amount% to %target%'s total number of completed quests."),
    REMOVE_TOTAL_TARGET("remove_total_target", "&eAn admin has removed %amount% to your total number of completed quests."),
    REMOVE_TOTAL_CATEGORY_ADMIN("remove_total_category_admin", "&eYou have removed %amount% to %target%'s total number of completed quests in the category %category%."),
    REMOVE_TOTAL_CATEGORY_TARGET("remove_total_category_target", "&eAn admin has removed %amount% to your total number of completed quests in the category %category%."),
    TOTAL_AMOUNT_RESET("total_amount_reset", "&eYour total number of completed quests has been reset by an admin."),
    TOTAL_AMOUNT_RESET_ADMIN("total_amount_reset_admin", "&e%target%'s total number of completed quests has been reset by an admin."),
    TOTAL_CATEGORY_RESET_ADMIN("total_category_reset_admin", "&e%target%'s total number of completed quests in the category %category% has been reset by an admin."),
    TOTAL_CATEGORY_RESET_TARGET("total_category_reset_target", "&eYour total number of completed quests in the category %category% has been reset by an admin."),
    QUEST_ACHIEVED("quest_achieved", "&aYou finished the quest &e%questName%&a, well done !"),
    ALL_QUESTS_ACHIEVED("all_quests_achieved", "&aYou have finished all your daily quests, well done !"),
    CATEGORY_QUESTS_ACHIEVED("category_quests_achieved", "&aYou have completed all your %category% quests!"),
    NOT_ENOUGH_ITEM("not_enough_items", "&cYou don't have the required amount to complete this quest."),

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

    PLUGIN_RELOADED("plugin_reloaded", "&aThe plugin has been reloaded successfully!"),
    ERROR_INVENTORY("error_inventory", "&cAn error occurred while opening the inventory."),
    CONVERSION_FAILED("conversion_failed", "&cConversion failed! Please check the console for more information."),
    CONVERSION_SUCCESS("conversion_success", "&aConversion successful! Please select the new storage mode in the config file and restart the server to apply changes."),
    CHECK_CONSOLE("check_console", "&cPlease check the console for more information."),
    IMPOSSIBLE_TO_OPEN_INVENTORY("impossible_to_open_inventory", "&cImpossible to open the quests interface. Is the plugin still loading?"),
    CONTACT_ADMIN("contact_admin", "&cIf the problem persists, please contact the server administrator."),
    CONFIGURATION_ERROR("configuration_error", "&cA configuration error prevents the interface from being displayed. Please inform an administrator.")
    ;

    private final String path;
    private final String defaultMessage;

    QuestsMessages(String path, String message) {
        this.path = path;
        this.defaultMessage = message;
    }

    @Override
    public String toString() {
        String msg = MessagesFile.getInstance().get(this.path, defaultMessage);

        if (msg.trim().isEmpty()) return "";
        else return TextFormatter.format(null, Prefix.getPrefix() + msg);
    }

    public String getMessage(Player player) {
        String msg = MessagesFile.getInstance().get(this.path, defaultMessage);

        if (msg.trim().isEmpty()) return null;
        else return TextFormatter.format(player,Prefix.getPrefix() + msg);
    }

    public String getMessage(String playerName) {
        final Player player = Bukkit.getPlayer(playerName);
        if (player == null) return null;

        String msg = MessagesFile.getInstance().get(this.path, defaultMessage);
        if (msg.trim().isEmpty()) return null;

        else return TextFormatter.format(player,Prefix.getPrefix() + msg);
    }

    public String getMessage(Player player, Map<String, String> placeholders) {
        String msg = MessagesFile.getInstance().get(this.path, defaultMessage);
        if (msg.trim().isEmpty()) return null;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue());
        }

        return TextFormatter.format(player, Prefix.getPrefix() + msg);
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