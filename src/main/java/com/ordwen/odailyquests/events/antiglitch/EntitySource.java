package com.ordwen.odailyquests.events.antiglitch;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import org.bukkit.entity.Entity;

import java.util.HashSet;

public class EntitySource {

    private final static HashSet<Entity> entitiesFromSpawners = new HashSet<>();

    /**
     * Adds an entity from a spawner.
     * @param entity the entity to add.
     */
    public static void addEntityFromSpawner(Entity entity) {
        Debugger.addDebug("Adding entity from spawner.");
        entitiesFromSpawners.add(entity);
    }

    /**
     * Removes an entity from a spawner.
     * @param entity the entity to remove.
     */
    public static void removeEntityFromSpawner(Entity entity) {
        Debugger.addDebug("Removing entity from spawner.");
        entitiesFromSpawners.remove(entity);
    }

    /**
     * Checks if an entity is from a spawner.
     * @param entity the entity to check.
     * @return true if the entity is from a spawner.
     */
    public static boolean isEntityFromSpawner(Entity entity) {
        return entitiesFromSpawners.contains(entity);
    }
}
