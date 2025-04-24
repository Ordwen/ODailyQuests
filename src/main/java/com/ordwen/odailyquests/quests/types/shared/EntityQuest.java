package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntityQuest extends AbstractQuest {

    private final List<EntityType> requiredEntities;

    protected DyeColor dyeColor;

    protected EntityQuest(BasicQuest base) {
        super(base);
        this.requiredEntities = new ArrayList<>();
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        boolean hasRequired = section.contains(".required") || section.contains(".required_entity");
        boolean hasRandomRequired = section.contains(".random_required");

        if (hasRequired && hasRandomRequired) {
            PluginLogger.configurationError(file, index, "required/random_required", "You can't use 'required' and 'random_required' at the same time.");
            return false;
        }

        if (hasRandomRequired) {
            super.isRandomRequired = true;
            return loadRequiredEntities(section, file, index, ".random_required");
        } else {
            return loadRequiredEntities(section, file, index, ".required_entity") && loadRequiredEntities(section, file, index, ".required");
        }
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
        boolean isRandom = path.contains("random_required");

        if (isRandom) {
            final List<?> rawList = section.getList(path);
            if (rawList == null || rawList.isEmpty()) {
                PluginLogger.configurationError(file, index, path, "The list of required entities is empty but 'random_required' is set.");
                return false;
            }

            for (Object entry : rawList) {
                if (!(entry instanceof Map<?, ?> mapEntry)) {
                    PluginLogger.configurationError(file, index, path, "Each entry in 'random_required' must be a map like - ZOMBIE: \"Display Name\"");
                    return false;
                }

                for (Map.Entry<?, ?> element : mapEntry.entrySet()) {
                    final String key = element.getKey().toString();
                    final String displayName = element.getValue().toString();

                    final EntityType entityType = getEntityType(file, index, key);
                    if (entityType == null) return false;

                    requiredEntities.add(entityType);
                    displayNames.add(displayName);
                }
            }
        } else {
            if (section.isString(path)) {
                final EntityType entityType = getEntityType(file, index, section.getString(path));
                if (entityType != null) requiredEntities.add(entityType);
                else return false;
            } else {
                for (String presumedEntity : section.getStringList(path)) {
                    final EntityType entityType = getEntityType(file, index, presumedEntity);
                    if (entityType != null) requiredEntities.add(entityType);
                    else return false;

                    if (isDisplayNameMissing(section, file, index, path, presumedEntity)) return false;
                }
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
     * @param entityType  the entity type
     * @param progression current player progression
     * @return true if the entity is required, false otherwise
     */
    public boolean isRequiredEntity(EntityType entityType, Progression progression) {
        if (isRandomRequired) {
            return entityType == requiredEntities.get(progression.getSelectedRequiredIndex());
        }

        return requiredEntities == null || requiredEntities.isEmpty() || requiredEntities.contains(entityType);
    }

    public List<EntityType> getRequiredEntities() {
        return requiredEntities;
    }
}
