package com.ordwen.odailyquests.api.quests;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.types.AbstractQuest;

import java.util.HashMap;

public class QuestTypeRegistry extends HashMap<String, Class<? extends AbstractQuest>> {

    public void registerQuestType(String type, Class<? extends AbstractQuest> questClass) {
        Debugger.addDebug("Registering quest type: " + type);
        this.put(type, questClass);
    }

    public Class<? extends AbstractQuest> getMainClass(String type) {
        return this.get(type);
    }
}
