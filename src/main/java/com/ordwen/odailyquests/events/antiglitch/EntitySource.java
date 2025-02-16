package com.ordwen.odailyquests.events.antiglitch;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.functionalities.SpawnerProgression;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class EntitySource {

    private EntitySource() {
    }

    public static final NamespacedKey FROM_SPAWNER = new NamespacedKey(ODailyQuests.INSTANCE, "from_spawner");

    /**
     * Adds an entity from a spawner.
     *
     * @param entity the entity to add.
     */
    public static void addEntityFromSpawner(Entity entity) {
        entity.getPersistentDataContainer().set(
                FROM_SPAWNER,
                PersistentDataType.BYTE,
                (byte) 1
        );

        Debugger.addDebug("EntitySource: addEntityFromSpawner added entity from spawner.");
    }

    /**
     * Checks if an entity is from a spawner.
     *
     * @param entity the entity to check.
     * @return true if the entity is from a spawner.
     */
    public static boolean isEntityFromSpawner(Entity entity) {
        Debugger.addDebug("EntitySource: isEntityFromSpawner checking if entity is from spawner.");
        return SpawnerProgression.isSpawnersProgressionDisabled() && entity.getPersistentDataContainer().has(FROM_SPAWNER, PersistentDataType.BYTE);
    }
}
