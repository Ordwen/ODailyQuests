package com.ordwen.odailyquests.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player reaches a global milestone (X total quests completed).
 * If defined, this event may trigger a global reward.
 * @since 3.0.0
 */
public class TotalRewardReachedEvent extends Event implements Cancellable {

    private final Player player;
    private final int totalCompleted;
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    public TotalRewardReachedEvent(Player player, int totalCompleted) {
        this.player = player;
        this.totalCompleted = totalCompleted;
        this.cancelled = false;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTotalCompleted() {
        return totalCompleted;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}