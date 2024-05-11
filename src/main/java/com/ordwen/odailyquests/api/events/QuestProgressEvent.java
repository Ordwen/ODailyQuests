package com.ordwen.odailyquests.api.events;

import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a quest progress.
 * This method advances the player's quest by the amount indicated.
 * It first checks that the player is not in a world prohibited by the configuration.
 * @since 2.2.4
 */
public class QuestProgressEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    private final Player player;
    private final Progression progression;
    private final AbstractQuest quest;
    private final int amount;
    private QuestSystem questSystem;

    /**
     * Constructor for the QuestProgressEvent.
     * @param player player who progressed the quest
     * @param progression current progression of the quest
     * @param quest quest that was progressed
     * @param amount amount of progression
     */
    public QuestProgressEvent(QuestSystem questSystem, Player player, Progression progression, AbstractQuest quest, int amount) {
        this.player = player;
        this.progression = progression;
        this.quest = quest;
        this.amount = amount;
        this.questSystem = questSystem;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public QuestSystem getQuestSystem() {
        return questSystem;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the player who progressed the quest.
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
     * Get the quest that was progressed.
     * @return AbstractQuest object
     */
    public AbstractQuest getQuest() {
        return quest;
    }

    /**
     * Get the amount of progression.
     * @return int
     */
    public int getAmount() {
        return amount;
    }
}
