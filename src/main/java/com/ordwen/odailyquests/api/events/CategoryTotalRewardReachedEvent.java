package com.ordwen.odailyquests.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player reaches a milestone in a specific category (e.g., 5 quests completed in that category).
 * If defined, this event may trigger a category-specific reward.
 * @since 3.0.0
 */
public class CategoryTotalRewardReachedEvent extends Event implements Cancellable {

    private final Player player;
    private final String category;
    private final int completedInCategory;
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    public CategoryTotalRewardReachedEvent(Player player, String category, int completedInCategory) {
        this.player = player;
        this.category = category;
        this.completedInCategory = completedInCategory;
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

    public String getCategory() {
        return category;
    }

    public int getCompletedInCategory() {
        return completedInCategory;
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