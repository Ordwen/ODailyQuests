package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.progression.Progression;

import java.util.HashMap;

public class PlayerQuests {

    /* Timestamp of last quests renew */
    Long timestamp;

    /* Quest active quest, Boolean quest status */
    HashMap<Quest, Progression> playerQuests;

    public PlayerQuests(Long timestamp, HashMap<Quest, Progression> playerQuests) {
        this.timestamp = timestamp;
        this.playerQuests = playerQuests;
    }

    /**
     * Get player timestamp.
     * @return timestamp.
     */
    public Long getTimestamp() {
        return this.timestamp;
    }
    /**
     * Get the progression of quest.
     * @param quest quest to check.
     * @return progression.
     */
    public Integer getPlayerProgression(Quest quest) {
        return playerQuests.get(quest).getProgression();
    }

    /**
     * Get status of quest.
     * @param quest quest to checK.
     * @return status.
     */
    public boolean getQuestStatus(Quest quest) {
        return playerQuests.get(quest).isAchieved();
    }

    /**
     * Get player quests.
     * @return player quests.
     */
    public HashMap<Quest, Progression> getPlayerQuests() {
        return this.playerQuests;
    }
}
