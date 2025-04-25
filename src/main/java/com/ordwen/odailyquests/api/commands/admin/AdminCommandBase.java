package com.ordwen.odailyquests.api.commands.admin;

import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AdminCommandBase extends AdminMessages implements AdminCommand, AdminCommandCompleter {

    /**
     * Retrieves a player instance by their exact name.
     * <p>
     * If the player is not found (offline or name mismatch), sends an "invalid player" message
     * to the sender and returns {@code null}.
     *
     * @param sender     the command sender (used to send feedback in case of an invalid player)
     * @param playerName the exact name of the target player
     * @return the target {@link Player} if found, or {@code null} otherwise
     */
    protected Player getTargetPlayer(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            invalidPlayer(sender);
        }
        return target;
    }

    /**
     * Parses an integer value from a string argument.
     * <p>
     * If the argument is not a valid number, sends an "invalid quest index" message
     * to the sender and returns {@code -1} as a fallback.
     *
     * @param sender the command sender (used to send feedback in case of invalid input)
     * @param arg    the string to parse as an integer
     * @return the parsed integer value, or {@code -1} if the input was invalid
     */
    protected int parseQuestIndex(CommandSender sender, String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(QuestsMessages.INVALID_QUEST_INDEX.toString());
            return -1;
        }
    }
}