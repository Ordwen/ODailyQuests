package com.ordwen.odailyquests.quests.types;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;

import java.util.List;

public class EntityQuest extends AbstractQuest {

    final List<EntityType> entityTypes;
    final DyeColor dyeColor;
    final List<String> entityNames;

    public EntityQuest(GlobalQuest globalQuest, List<EntityType> entityTypes, DyeColor dyeColor) {
        super(globalQuest);
        this.entityTypes = entityTypes;
        this.dyeColor = dyeColor;
        entityNames = null;
    }

    public EntityQuest(GlobalQuest globalQuest, List<String> entityNames) {
        super(globalQuest);
        this.entityNames = entityNames;

        this.entityTypes = null;
        this.dyeColor = null;
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
    public List<String> getEntityNames() {
        return this.entityNames;
    }
}
