package com.ordwen.odailyquests.api.commands.admin;

import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.command.CommandSender;

/**
 * Base class providing common message sending functionality for admin commands.
 * <p>
 * This class includes helper methods for sending predefined messages to the command sender (admin).
 * These messages are typically used for error handling and feedback when interacting with commands.
 */
public abstract class AdminMessages {

    /**
     * Sends the admin help message to the sender.
     */
    protected void help(CommandSender sender) {
        final String msg = QuestsMessages.ADMIN_HELP.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid player message to the sender.
     */
    protected void invalidPlayer(CommandSender sender) {
        final String msg = QuestsMessages.INVALID_PLAYER.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid quest message to the sender.
     */
    protected void invalidQuest(CommandSender sender) {
        final String msg = QuestsMessages.INVALID_QUEST_INDEX.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid amount message to the sender.
     */
    protected void invalidAmount(CommandSender sender) {
        final String msg = QuestsMessages.INVALID_AMOUNT.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid category message to the sender.
     */
    protected void invalidCategory(CommandSender sender) {
        final String msg = QuestsMessages.INVALID_CATEGORY.toString();
        if (msg != null) sender.sendMessage(msg);
    }
}
