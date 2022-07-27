package com.ordwen.odailyquests.quests.player.progression.types;

import com.ordwen.odailyquests.quests.player.progression.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.types.GlobalQuest;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;

public class EntityQuest extends AbstractQuest {

    final EntityType entityType;
    final DyeColor dyeColor;
    final String entityName;

    public EntityQuest(GlobalQuest globalQuest, EntityType entityType, DyeColor dyeColor, String entityName) {
        super(globalQuest);
        this.entityType = entityType;
        this.dyeColor = dyeColor;
        this.entityName = entityName;
    }

    /**
     * Get the entity required by the quest.
     * @return quest item-required.
     */
    public EntityType getEntityType() {
        return this.entityType;
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
