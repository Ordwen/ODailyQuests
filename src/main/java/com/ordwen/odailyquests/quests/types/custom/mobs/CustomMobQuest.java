package com.ordwen.odailyquests.quests.types.custom.mobs;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomMobQuest extends AbstractQuest {

    private static final String NAME_PATH = ".entity_name";

    protected List<String> entityNames;

    protected CustomMobQuest(BasicQuest base) {
        super(base);
        this.entityNames = new ArrayList<>();
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        if (section.isString(NAME_PATH)) {
            entityNames.add(section.getString(NAME_PATH));
        } else {
            entityNames.addAll(section.getStringList(NAME_PATH));
        }

        for (String entityName : entityNames) {
            entityNames.set(entityNames.indexOf(entityName), ColorConvert.convertColorCode(entityName));
        }

        if (entityNames.isEmpty()) {
            PluginLogger.configurationError(file, index, null, "There is no entity name defined for quest type CUSTOM_MOBS.");
            return false;
        }

        return true;
    }

    public boolean isRequiredEntity(String entityName) {
        return entityNames.contains(entityName);
    }
}
