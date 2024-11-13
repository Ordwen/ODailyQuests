package com.ordwen.odailyquests.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player completed all his quests from a category.
 * If defined, this method sends the reward associated with the category to the player.
 *
 * @since 2.1.0
 */
public class AllCategoryQuestsCompletedEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final String category;
    private boolean isCancelled;

    /**
     * Constructor for the AllQuestsFromCategoryCompleted.
     *
     * @param player   player who completed all his quests.
     * @param category name of the category.
     */
    public AllCategoryQuestsCompletedEvent(Player player, String category) {
        this.player = player;
        this.category = category;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
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
     *
     * @return Player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the name of the quests' category.
     *
     * @return the name of the quests' category.
     */
    public String getCategory() {
        return category;
    }
}
