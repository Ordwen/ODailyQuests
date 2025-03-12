package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityQuest extends AbstractQuest {


    protected final List<EntityType> requiredEntities;
    protected DyeColor dyeColor;

    protected EntityQuest(BasicQuest base) {
        super(base);
        this.requiredEntities = new ArrayList<>();
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        return loadRequiredEntities(section, file, index, ".required_entity")
                && loadRequiredEntities(section, file, index, ".required");
    }

    /**
     * Load entity types from a configuration section.
     *
     * @param section the configuration section
     * @param file    the file name
     * @param index   the quest index
     * @param path    the path to check
     * @return true if loading was successful, false otherwise
     */
    private boolean loadRequiredEntities(ConfigurationSection section, String file, String index, String path) {
        if (!section.contains(path)) return true;

        if (section.isString(path)) {
            final EntityType entityType = getEntityType(file, index, section.getString(path));
            if (entityType != null) requiredEntities.add(entityType);
            else return false;
        } else {
            for (String presumedEntity : section.getStringList(path)) {
                final EntityType entityType = getEntityType(file, index, presumedEntity);
                if (entityType != null) requiredEntities.add(entityType);
                else return false;
            }
        }

        return true;
    }


    /**
     * Get the entity type.
     *
     * @param file  the file name
     * @param index the quest index
     * @param value the value
     * @return the entity type, or null if the entity type is invalid
     */
    private EntityType getEntityType(String file, String index, String value) {
        try {
            return EntityType.valueOf(value);
        } catch (Exception e) {
            PluginLogger.configurationError(file, index, "required_entity", "Invalid entity type detected.");
            return null;
        }
    }

    /**
     * Check if the entity is required by the quest.
     *
     * @param entityType the entity type
     * @return true if the entity is required, false otherwise
     */
    public boolean isRequiredEntity(EntityType entityType) {
        return requiredEntities == null || requiredEntities.isEmpty() || requiredEntities.contains(entityType);
    }
}
