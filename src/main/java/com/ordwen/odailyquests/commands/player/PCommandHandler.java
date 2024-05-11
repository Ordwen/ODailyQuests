package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.QuestSystem;
import org.bukkit.entity.Player;

public abstract class PCommandHandler {

    protected final Player player;
    protected final String[] args;
    protected final QuestSystem questSystem;
    protected final PlayerMessages playerMessages;


    protected PCommandHandler(Player player, String[] args, QuestSystem questSystem, PlayerMessages playerMessages) {
        this.player = player;
        this.args = args;
        this.questSystem = questSystem;
        this.playerMessages = playerMessages;
    }

    /**
     * Handles the command execution.
     */
    public abstract void handle();
}
