package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityQuest extends AbstractQuest {

    protected final List<EntityType> requiredEntities;
    protected DyeColor dyeColor;

    public EntityQuest(BasicQuest base) {
        super(base);
        this.requiredEntities = new ArrayList<>();
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        if (!section.contains(".required_entity")) return true;

        if (section.isString(".required_entity")) {
            final EntityType entityType = getEntityType(file, index, section.getString(".required_entity"));
            if (entityType != null) requiredEntities.add(entityType);
            else return false;
        } else {
            for (String presumedEntity : section.getStringList(".required_entity")) {
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
    private EntityType getEntityType(String file, int index, String value) {
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

    /**
     * Get the entity required by the quest.
     *
     * @return quest item-required.
     */
    public List<EntityType> getRequiredEntities() {
        return this.requiredEntities;
    }

    /**
     * Get sheep color.
     *
     * @return sheep color.
     */
    public DyeColor getDyeColor() {
        return this.dyeColor;
    }
}
