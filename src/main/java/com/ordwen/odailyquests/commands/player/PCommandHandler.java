package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.entity.Player;

public abstract class PCommandHandler {

    protected final Player player;
    protected final String[] args;


    protected PCommandHandler(Player player, String[] args) {
        this.player = player;
        this.args = args;
    }

    /**
     * Handles the command execution.
     */
    public abstract void handle();

    /**
     * Sends the player help message to the sender.
     */
    protected void help() {
        final String msg = QuestsMessages.PLAYER_HELP.toString();
        if (msg != null) player.sendMessage(msg);
    }

    /**
     * Sends the no permission message to the sender.
     */
    protected void noPermission() {
        final String msg = QuestsMessages.NO_PERMISSION.toString();
        if (msg != null) player.sendMessage(msg);
    }

    /**
     * Sends the no permission category message to the sender.
     */
    protected void noPermissionCategory() {
        final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
        if (msg != null) player.sendMessage(msg);
    }

    /**
     * Sends the categorized disabled message to the sender.
     */
    protected void categorizedDisabled() {
        final String msg = QuestsMessages.CATEGORIZED_DISABLED.toString();
        if (msg != null) player.sendMessage(msg);
    }
}
