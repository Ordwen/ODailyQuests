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

/**
 * {@code EntityQuest} is an abstract base class for quests that require
 * players to interact with or defeat specific entities.
 *
 * <p>It supports two configuration modes:</p>
 * <ul>
 *     <li><b>required / required_entity</b>: a fixed set of acceptable entity types</li>
 *     <li><b>random_required</b>: one randomly selected entity type from a defined list</li>
 * </ul>
 *
 * <p>Features and rules:</p>
 * <ul>
 *     <li>Parses quest configuration to load entity requirements.</li>
 *     <li>Validates that both {@code required} and {@code random_required} are not used together.</li>
 *     <li>Handles both single and multiple entity definitions.</li>
 *     <li>Provides runtime checks to determine whether an entity type matches quest requirements.</li>
 * </ul>
 *
 * <p>Dependencies/Integration:</p>
 * <ul>
 *     <li>{@link AbstractQuest} for base quest logic.</li>
 *     <li>Uses {@link PluginLogger} to report configuration errors.</li>
 *     <li>Uses {@link Progression} to handle randomized entity requirements.</li>
 * </ul>
 */
public abstract class EntityQuest extends AbstractQuest {

    /**
     * The list of required entity types for this quest.
     */
    private final List<EntityType> requiredEntities;

    /**
     * Optional color hint (e.g., for sheep or other dyeable entities).
     */
    protected DyeColor dyeColor;

    /**
     * Constructs an {@code EntityQuest} from a base quest definition.
     *
     * @param base the parent/basic quest definition
     */
    protected EntityQuest(BasicQuest base) {
        super(base);
        this.requiredEntities = new ArrayList<>();
    }

    /**
     * Loads quest parameters from the given configuration section.
     * <p>Recognized keys:</p>
     * <ul>
     *     <li>{@code .required} or {@code .required_entity}: fixed entity requirements</li>
     *     <li>{@code .random_required}: randomized entity requirement</li>
     * </ul>
     *
     * <p>Mutual exclusion: {@code required/required_entity} cannot be used together with {@code random_required}.</p>
     *
     * @param section configuration subsection for this quest
     * @param file    the file name where this configuration resides (for error reporting)
     * @param index   the quest index/key inside the file (for error reporting)
     * @return {@code true} on successful load; {@code false} on configuration errors
     */
    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        boolean hasRequired = section.contains(".required") || section.contains(".required_entity");
        boolean hasRandomRequired = section.contains(".random_required");

        if (hasRequired && hasRandomRequired) {
            PluginLogger.configurationError(file, index, "required/random_required",
                    "You can't use 'required' and 'random_required' at the same time.");
            return false;
        }

        if (hasRandomRequired) {
            super.isRandomRequired = true;
            return loadRequiredEntities(section, file, index, ".random_required");
        } else {
            return loadRequiredEntities(section, file, index, ".required_entity")
                    && loadRequiredEntities(section, file, index, ".required");
        }
    }

    /**
     * Loads required entities from a given configuration path, delegating
     * to the correct loader depending on whether the path is randomized or fixed.
     *
     * @param section configuration section
     * @param file    file name (for error reporting)
     * @param index   quest index/key (for error reporting)
     * @param path    configuration path (".required", ".random_required", etc.)
     * @return {@code true} if entities were loaded successfully, {@code false} otherwise
     */
    private boolean loadRequiredEntities(ConfigurationSection section, String file, String index, String path) {
        if (!section.contains(path)) return true;
        return path.contains("random_required")
                ? loadRandomRequiredEntities(section, file, index, path)
                : loadSimpleRequiredEntities(section, file, index, path);
    }

    /**
     * Loads entities in {@code random_required} mode.
     * Each entry must be a single-entry map like:
     * <pre>{@code
     * - ZOMBIE: "Undead Foe"
     * - SKELETON: "Bone Warrior"
     * }</pre>
     *
     * @param section configuration section
     * @param file    file name (for error reporting)
     * @param index   quest index/key (for error reporting)
     * @param path    configuration path (".random_required")
     * @return {@code true} if entities are valid, {@code false} otherwise
     */
    private boolean loadRandomRequiredEntities(ConfigurationSection section, String file, String index, String path) {
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
            if (!processRandomEntityMap(mapEntry, file, index)) return false;
        }
        return true;
    }

    /**
     * Processes a single {@code random_required} map entry,
     * extracting the entity type and its display name.
     *
     * @param mapEntry the map entry ({@code ENTITY_TYPE: "Display Name"})
     * @param file     file name (for error reporting)
     * @param index    quest index/key (for error reporting)
     * @return {@code true} if the entity type is valid, {@code false} otherwise
     */
    private boolean processRandomEntityMap(Map<?, ?> mapEntry, String file, String index) {
        for (Map.Entry<?, ?> element : mapEntry.entrySet()) {
            final String key = element.getKey().toString();
            final String displayName = element.getValue().toString();

            final EntityType entityType = getEntityType(file, index, key);
            if (entityType == null) return false;

            requiredEntities.add(entityType);
            displayNames.add(displayName);
        }
        return true;
    }

    /**
     * Loads entities in fixed {@code required} or {@code required_entity} mode.
     * Accepts either a single string or a list of entity type names.
     *
     * @param section configuration section
     * @param file    file name (for error reporting)
     * @param index   quest index/key (for error reporting)
     * @param path    configuration path (".required", ".required_entity")
     * @return {@code true} if entities are valid, {@code false} otherwise
     */
    private boolean loadSimpleRequiredEntities(ConfigurationSection section, String file, String index, String path) {
        if (section.isString(path)) {
            final EntityType single = getEntityType(file, index, section.getString(path));
            if (single == null) return false;
            requiredEntities.add(single);
            return true;
        }

        final List<String> presumed = section.getStringList(path);
        for (String name : presumed) {
            final EntityType type = getEntityType(file, index, name);
            if (type == null) return false;
            requiredEntities.add(type);

            // Ensure that display names are properly defined when required
            if (isDisplayNameMissing(section, file, index, path, name)) return false;
        }
        return true;
    }

    /**
     * Resolves an {@link EntityType} from a string.
     *
     * @param file  file name (for error reporting)
     * @param index quest index/key (for error reporting)
     * @param value string to parse as an entity type
     * @return the corresponding {@link EntityType}, or {@code null} if invalid
     */
    private EntityType getEntityType(String file, String index, String value) {
        try {
            return EntityType.valueOf(value);
        } catch (Exception e) {
            PluginLogger.configurationError(file, index, "required_entity",
                    "Invalid entity type detected.");
            return null;
        }
    }

    /**
     * Checks whether the given entity type is required by this quest.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>If random mode is enabled, only the currently selected entity type is accepted.</li>
     *     <li>If fixed mode is used, any entity in the {@code requiredEntities} list is accepted.</li>
     *     <li>If no requirements are set, any entity type is accepted.</li>
     * </ul>
     *
     * @param entityType  the entity type to check
     * @param progression current player progression (used in random mode)
     * @return {@code true} if the entity type is valid for this quest, {@code false} otherwise
     */
    public boolean isRequiredEntity(EntityType entityType, Progression progression) {
        if (isRandomRequired) {
            return entityType == requiredEntities.get(progression.getSelectedRequiredIndex());
        }
        return requiredEntities == null || requiredEntities.isEmpty() || requiredEntities.contains(entityType);
    }

    /**
     * Returns the list of required entities for this quest.
     * <p>In random mode, this returns the full pool, not just the selected entry.</p>
     *
     * @return list of required entity types (possibly empty)
     */
    public List<EntityType> getRequiredEntities() {
        return requiredEntities;
    }
}
