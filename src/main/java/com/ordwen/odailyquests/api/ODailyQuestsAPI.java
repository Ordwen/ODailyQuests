package com.ordwen.odailyquests.api;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.api.quests.QuestTypeRegistry;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to access the API of ODailyQuests.
 */
public class ODailyQuestsAPI {

    private static boolean canRegister = true;
    private static final Map<String, Class<? extends AbstractQuest>> externalTypes = new HashMap<>();

    private final QuestTypeRegistry questTypeRegistry = new QuestTypeRegistry();

    /**
     * Get the QuestTypeRegistry object.
     *
     * @return QuestTypeRegistry object
     */
    public QuestTypeRegistry getQuestTypeRegistry() {
        return questTypeRegistry;
    }

    /**
     * Get the PlayerQuests object of a player.
     * Default to default system if not specified to not break api.
     *
     * @param playerName name of the player
     * @return PlayerQuests object
     */
    public PlayerQuests getPlayerQuests(String playerName) {
        return ODailyQuests.questSystemMap.get("normal").getActiveQuests().get(playerName);
    }

    /**
     * Get the PlayerQuests object of a player, with the supplied system
     *
     * @param playerName name of the player
     * @return PlayerQuests object
     */
    public PlayerQuests getPlayerQuests(String playerName, QuestSystem questSystem) {
        return questSystem.getActiveQuests().get(playerName);
    }

    /**
     * Register a new quest type.
     *
     * @param type       type of the quest
     * @param questClass class of the quest
     */
    public static void registerQuestType(String type, Class<? extends AbstractQuest> questClass) {
        if (canRegister) {
            externalTypes.put(type, questClass);
        } else {
            PluginLogger.error("Cannot register quest type " + type + " because the plugin has already been enabled.");
        }
    }

    /**
     * Disable the registration of new quest types.
     */
    public static void disableRegistration() {
        canRegister = false;
    }

    /**
     * Get the external quest types.
     *
     * @return external quest types
     */
    public static Map<String, Class<? extends AbstractQuest>> getExternalTypes() {
        return externalTypes;
    }
}
