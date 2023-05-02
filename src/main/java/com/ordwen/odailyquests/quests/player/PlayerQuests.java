package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.api.events.AllQuestsCompletedEvent;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

/**
 * Represents the quests of a player and its data.
 */
public class PlayerQuests {

    /* Timestamp of last quests renew */
    private final Long timestamp;

    private int achievedQuests;
    private int totalAchievedQuests;
    private final LinkedHashMap<AbstractQuest, Progression> playerQuests;

    public PlayerQuests(Long timestamp, LinkedHashMap<AbstractQuest, Progression> playerQuests) {
        this.timestamp = timestamp;
        this.playerQuests = playerQuests;
        this.achievedQuests = 0;
        this.totalAchievedQuests = 0;
    }

    /**
     * Get player timestamp.
     * @return timestamp.
     */
    public Long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Increase number of achieved quests.
     * @param player player who achieved a quest.
     */
    public void increaseAchievedQuests(Player player) {
        this.achievedQuests++;
        this.totalAchievedQuests++;

        if (this.achievedQuests == QuestsAmount.getQuestsAmount()) {
            final AllQuestsCompletedEvent event = new AllQuestsCompletedEvent(player);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Set number of achieved quests.
     * @param i number of achieved quests to set.
     */
    public void setAchievedQuests(int i) {
        this.achievedQuests = i;
    }

    /**
     * Set total number of achieved quests.
     * @param i total number of achieved quests to set.
     */
    public void setTotalAchievedQuests(int i) { this.totalAchievedQuests = i; }

    /**
     * Get number of achieved quests.
     */
    public int getAchievedQuests() {
        return this.achievedQuests;
    }

    /**
     * Get total number of achieved quests.
     */
    public int getTotalAchievedQuests() {
        return this.totalAchievedQuests;
    }

    /**
     * Get player quests.
     * @return a LinkedHashMap of quests and their progression.
     */
    public LinkedHashMap<AbstractQuest, Progression> getPlayerQuests() {
        return this.playerQuests;
    }

}
