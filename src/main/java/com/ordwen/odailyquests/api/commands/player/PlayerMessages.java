package com.ordwen.odailyquests.api.commands.player;

import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.command.CommandSender;

public abstract class PlayerMessages {

    /**
     * Sends the player help message to the sender.
     *
     * @param sender the sender.
     */
    protected void help(CommandSender sender) {
        final String msg = QuestsMessages.PLAYER_HELP.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the no permission message to the sender.
     *
     * @param sender the sender.
     */
    protected void noPermission(CommandSender sender) {
        final String msg = QuestsMessages.NO_PERMISSION.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the no permission category message to the sender.
     *
     * @param sender the sender.
     */
    protected void noPermissionCategory(CommandSender sender) {
        final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends a message to the sender indicating that the command can only be executed by a player.
     *
     * @param sender the sender.
     */
    protected void playerOnly(CommandSender sender) {
        final String msg = QuestsMessages.PLAYER_ONLY.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends a message to the sender indicating that the category is invalid.
     *
     * @param sender the sender.
     */
    protected void invalidCategory(CommandSender sender) {
        final String msg = QuestsMessages.INVALID_CATEGORY.toString();
        if (msg != null) sender.sendMessage(msg);
    }
}
