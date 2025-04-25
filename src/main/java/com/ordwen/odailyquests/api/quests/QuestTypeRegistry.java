package com.ordwen.odailyquests.api.quests;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import java.util.HashMap;

/**
 * This class manages the registration and retrieval of quest types.
 * It extends {@link HashMap} to store quest types, where the key is the type name and the value is the corresponding quest class.
 */
public class QuestTypeRegistry extends HashMap<String, Class<? extends AbstractQuest>> {

    /**
     * Registers a new quest type by associating a type name with its corresponding quest class.
     *
     * @param type the name of the quest type to register
     * @param questClass the class of the quest that corresponds to the type
     */
    public void registerQuestType(String type, Class<? extends AbstractQuest> questClass) {
        this.put(type, questClass);
    }

    /**
     * Retrieves the quest class associated with the specified quest type.
     *
     * @param type the name of the quest type
     * @return the {@link Class} of the quest associated with the type, or null if the type is not registered
     */
    public Class<? extends AbstractQuest> getMainClass(String type) {
        return this.get(type);
    }
}
