package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.command.CommandSender;

public abstract class ACommandHandler {

    protected final CommandSender sender;
    protected final String[] args;

    public ACommandHandler(CommandSender sender, String[] args) {
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
    public void help() {
        final String msg = QuestsMessages.ADMIN_HELP.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends the invalid player message to the sender.
     */
    public void invalidPlayer() {
        final String msg = QuestsMessages.INVALID_PLAYER.toString();
        if (msg != null) sender.sendMessage(msg);
    }
}
