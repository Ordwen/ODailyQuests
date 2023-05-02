package com.ordwen.odailyquests.api.events;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a quest is completed.
 * @since 2.1.0
 */
public class QuestCompletedEvent extends Event implements Cancellable {

    private final Player player;
    private final Progression progression;
    private final AbstractQuest abstractQuest;

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    /**
     * Constructor for the QuestCompletedEvent.
     * @param player player who completed the quest
     * @param progression current progression of the quest
     * @param abstractQuest quest that was completed
     */
    public QuestCompletedEvent(Player player, Progression progression, AbstractQuest abstractQuest) {
        this.player = player;
        this.progression = progression;
        this.abstractQuest = abstractQuest;

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
     * Get the player who completed the quest.
     * @return Player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the current progression of the quest.
     * @return Progression object
     */
    public Progression getProgression() {
        return progression;
    }

    /**
     * Get the quest that was completed.
     * @return AbstractQuest object
     */
    public AbstractQuest getAbstractQuest() {
        return abstractQuest;
    }
}
