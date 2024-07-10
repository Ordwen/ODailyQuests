package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.command.CommandSender;

public abstract class ACommandHandler {

    protected final CommandSender sender;
    protected final String[] args;

    protected ACommandHandler(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    /**
     * Handles the command execution.
     */
    public abstract void handle();

    /**
     * Sends the admin help message to the sender.
     */
    protected void help() {
        final String msg = QuestsMessages.ADMIN_HELP.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid player message to the sender.
     */
    protected void invalidPlayer() {
        final String msg = QuestsMessages.INVALID_PLAYER.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid quest message to the sender.
     */
    protected void invalidQuest() {
        final String msg = QuestsMessages.INVALID_QUEST_INDEX.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid amount message to the sender.
     */
    protected void invalidAmount() {
        final String msg = QuestsMessages.INVALID_AMOUNT.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the player only message to the sender.
     */
    protected void playerOnly() {
        final String msg = QuestsMessages.PLAYER_ONLY.toString();
        if (msg != null) sender.sendMessage(msg);
    }
}
