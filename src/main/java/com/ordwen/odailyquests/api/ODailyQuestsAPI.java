package com.ordwen.odailyquests.api;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;

/**
 * This class is used to access the API of ODailyQuests.
 */
public class ODailyQuestsAPI {

    private final ODailyQuests oDailyQuests;

    /**
     * Constructor for the ODailyQuestsAPI.
     * @param oDailyQuests main class of the plugin
     */
    public ODailyQuestsAPI(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Get the PlayerQuests object of a player.
     * @param playerName name of the player
     * @return PlayerQuests object
     */
    public PlayerQuests getPlayerQuests(String playerName) {
        return QuestsManager.getActiveQuests().get(playerName);
    }
}
