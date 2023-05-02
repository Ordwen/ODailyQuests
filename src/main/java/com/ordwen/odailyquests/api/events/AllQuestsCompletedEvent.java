package com.ordwen.odailyquests.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player completes all his quests.
 * @since 2.1.0
 */
public class AllQuestsCompletedEvent extends Event implements Cancellable {

    private final Player player;
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    /**
     * Constructor for the AllQuestsCompletedEvent.
     * @param player player who completed all his quests
     */
    public AllQuestsCompletedEvent(Player player) {
        this.player = player;
        this.isCancelled = false;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    /**
     * Get the player who completed all his quests.
     * @return Player object
     */
    public Player getPlayer() {
        return player;
    }
}
