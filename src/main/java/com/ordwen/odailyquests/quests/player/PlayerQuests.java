package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.events.listeners.inventory.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.configuration.functionalities.GlobalReward;

import java.util.LinkedHashMap;

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
     */
    public void increaseAchievedQuests(String playerName) {
        this.achievedQuests++;
        this.totalAchievedQuests++;

        if (this.achievedQuests == QuestsAmount.getQuestsAmount()) {
            GlobalReward.sendGlobalReward(playerName);
        }
    }

    public void setAchievedQuests(int i) {
        this.achievedQuests = i;
    }

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
     * @return player quests.
     */
    public LinkedHashMap<AbstractQuest, Progression> getPlayerQuests() {
        return this.playerQuests;
    }

}
