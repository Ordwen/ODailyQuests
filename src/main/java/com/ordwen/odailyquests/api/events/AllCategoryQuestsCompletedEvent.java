package com.ordwen.odailyquests.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AllCategoryQuestsCompletedEvent extends Event implements Cancellable {

    private final Player player;
    private final String category;
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    /**
     * Constructor for the AllQuestsFromCategoryCompleted.
     * @param player player who completed all his quests.
     */
    public AllCategoryQuestsCompletedEvent(Player player, String category) {
        this.player = player;
        this.category = category;
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

    /**
     * Get the name of the quests' category.
     * @return the name of the quests' category.
     */
    public String getCategory() {
        return category;
    }
}
