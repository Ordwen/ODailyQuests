package com.ordwen.odailyquests.api;

import com.ordwen.odailyquests.api.commands.admin.AdminCommandRegistry;
import com.ordwen.odailyquests.api.commands.player.PlayerCommandRegistry;
import com.ordwen.odailyquests.api.quests.QuestTypeRegistry;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides access to the ODailyQuests API, allowing interaction with quest types, player quests,
 * and command registries for both players and admins.
 */
public class ODailyQuestsAPI {

    /**
     * Indicates whether quest type registration is allowed.
     * Set to false after the plugin has been enabled to prevent further registration.
     */
    private static boolean canRegister = true;

    /**
     * A map holding the registered external quest types, mapped by their type name.
     * The values are the corresponding quest class types.
     */
    private static final Map<String, Class<? extends AbstractQuest>> externalTypes = new HashMap<>();

    /**
     * The registry for quest types.
     */
    private final QuestTypeRegistry questTypeRegistry = new QuestTypeRegistry();

    /**
     * The registry for player commands.
     */
    private final PlayerCommandRegistry playerCommandRegistry = new PlayerCommandRegistry();

    /**
     * The registry for admin commands.
     */
    private final AdminCommandRegistry adminCommandRegistry = new AdminCommandRegistry();

    /**
     * Retrieves the active quests for a specific player.
     *
     * @param playerName the name of the player
     * @return the PlayerQuests object representing the player's active quests
     */
    public static PlayerQuests getPlayerQuests(String playerName) {
        return QuestsManager.getActiveQuests().get(playerName);
    }

    /**
     * Retrieves the QuestTypeRegistry used to manage quest types.
     *
     * @return the QuestTypeRegistry object
     */
    public QuestTypeRegistry getQuestTypeRegistry() {
        return questTypeRegistry;
    }

    /**
     * Retrieves the PlayerCommandRegistry used to manage player commands.
     *
     * @return the PlayerCommandRegistry object
     */
    public PlayerCommandRegistry getPlayerCommandRegistry() {
        return playerCommandRegistry;
    }

    /**
     * Retrieves the AdminCommandRegistry used to manage admin commands.
     *
     * @return the AdminCommandRegistry object
     */
    public AdminCommandRegistry getAdminCommandRegistry() {
        return adminCommandRegistry;
    }

    /**
     * Registers a new quest type.
     * The registration is only allowed if the plugin is in a state that permits it.
     *
     * @param type the type of the quest to be registered
     * @param questClass the class of the quest type
     */
    public static void registerQuestType(String type, Class<? extends AbstractQuest> questClass) {
        if (canRegister) {
            externalTypes.put(type, questClass);
        } else {
            PluginLogger.error("Cannot register quest type " + type + " because the plugin has already been enabled.");
        }
    }

    /**
     * Disables further registration of new quest types.
     */
    public static void disableRegistration() {
        canRegister = false;
    }

    /**
     * Retrieves a map of external quest types that have been registered.
     *
     * @return a map of external quest types, where the key is the type name and the value is the quest class
     */
    public static Map<String, Class<? extends AbstractQuest>> getExternalTypes() {
        return externalTypes;
    }
}