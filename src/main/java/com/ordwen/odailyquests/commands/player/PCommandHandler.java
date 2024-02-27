package com.ordwen.odailyquests.commands.player;

import org.bukkit.entity.Player;

public abstract class PCommandHandler extends PlayerMessages {

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
}
