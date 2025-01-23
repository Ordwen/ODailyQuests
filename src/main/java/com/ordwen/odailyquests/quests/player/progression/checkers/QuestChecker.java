package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class QuestChecker<Q extends AbstractQuest> {
    protected final Player player;
    protected final Progression progression;
    protected final Q quest;

    protected QuestChecker(Player player, Progression progression, Q quest) {
        this.player = player;
        this.progression = progression;
        this.quest = quest;
    }

    /**
     * Validate and complete the quest.
     */
    public abstract void validateAndComplete();

    /**
     * Send a message to the player if the message is defined.
     *
     * @param message the message enum.
     */
    protected void sendMessage(QuestsMessages message) {
        String msg = message.getMessage(player);
        if (msg != null) player.sendMessage(msg);
    }

    /**
     * Call the quest completion event.
     */
    protected void completeQuest() {
        Bukkit.getPluginManager().callEvent(new QuestCompletedEvent(player, progression, quest));
        player.closeInventory();
    }
}
