package com.ordwen.odailyquests.quests.types.tmp.custom.mobs;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomMobQuest extends AbstractQuest {

    protected List<String> entityNames;

    public CustomMobQuest(BasicQuest base) {
        super(base);
        this.entityNames = new ArrayList<>();
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        if (section.isString(".entity_name")) {
            entityNames.add(section.getString(".entity_name"));
        } else {
            entityNames.addAll(section.getStringList(".entity_name"));
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

    /**
     * Get required entity name
     *
     * @return entity name
     */
    public List<String> getEntityNames() {
        return this.entityNames;
    }
}
