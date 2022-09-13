package com.ordwen.odailyquests.events.listeners.inventory.types;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;

import java.util.List;

public class EntityQuest extends AbstractQuest {

    final List<EntityType> entityTypes;
    final DyeColor dyeColor;
    final String entityName;

    public EntityQuest(GlobalQuest globalQuest, List<EntityType> entityTypes, DyeColor dyeColor, String entityName) {
        super(globalQuest);
        this.entityTypes = entityTypes;
        this.dyeColor = dyeColor;
        this.entityName = entityName;
    }

    /**
     * Get the entity required by the quest.
     * @return quest item-required.
     */
    public List<EntityType> getEntityTypes() {
        return this.entityTypes;
    }

    /**
     * Get sheep color.
     * @return sheep color.
     */
    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    /**
     * Get required entity name
     * @return entity name
     */
    public String getEntityName() {
        return this.entityName;
    }
}
