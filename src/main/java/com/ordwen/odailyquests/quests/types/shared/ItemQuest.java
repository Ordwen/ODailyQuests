package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.getters.QuestItemGetter;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@code ItemQuest} is an abstract base for quests that require players to
 * provide specific items. It supports two configuration modes:
 * <ul>
 *     <li><b>required / required_item</b>: a fixed set of acceptable items</li>
 *     <li><b>random_required</b>: a randomized choice (one of several defined items)</li>
 * </ul>
 *
 * <p>Features and rules:</p>
 * <ul>
 *     <li>Parses quest configuration to load required items (including potion metadata).</li>
 *     <li>Supports a {@code CUSTOM_ITEM} shortcut resolved by {@link QuestItemGetter}.</li>
 *     <li>Can ignore NBT when matching items via {@code ignore_nbt} flag (with potion safety checks).</li>
 *     <li>Supports custom model data comparisons when present.</li>
 * </ul>
 *
 * <p>Dependencies/Integration:</p>
 * <ul>
 *     <li>{@link AbstractQuest} for base quest logic.</li>
 *     <li>Uses {@link QuestItemGetter} to resolve materials, custom items, and potion meta.</li>
 *     <li>Uses {@link PluginLogger} to report configuration errors.</li>
 *     <li>Uses {@link Debugger} for detailed debug output during item matching.</li>
 * </ul>
 */
public abstract class ItemQuest extends AbstractQuest {

    /** Set of potion materials that require special metadata handling. */
    private static final Set<Material> POTIONS_TYPES = Set.of(
            Material.POTION,
            Material.SPLASH_POTION,
            Material.LINGERING_POTION
    );

    /** Helper used to resolve items, custom items, and potion metadata from configuration. */
    private static final QuestItemGetter itemGetter = new QuestItemGetter();

    /** The loaded list of required items for this quest. */
    private final List<ItemStack> requiredItems;

    /** If {@code true}, NBT is ignored when matching required vs. provided items (with potion safeguards). */
    private boolean ignoreNbt = false;

    /**
     * Constructs an {@code ItemQuest} from a base quest descriptor.
     *
     * @param base the parent/basic quest definition
     */
    protected ItemQuest(BasicQuest base) {
        super(base);
        this.requiredItems = new ArrayList<>();
    }

    /**
     * Loads quest parameters from the given configuration section.
     * <p>Recognized keys:</p>
     * <ul>
     *     <li>{@code .ignore_nbt} (boolean)</li>
     *     <li>{@code .required} (string or list of strings)</li>
     *     <li>{@code .required_item} (string or list of strings)</li>
     *     <li>{@code .random_required} (list of maps: {@code MATERIAL: "Display Name"})</li>
     * </ul>
     *
     * <p>Mutual exclusion: {@code required/required_item} cannot be used together with {@code random_required}.</p>
     *
     * @param section configuration subsection for this quest
     * @param file    the file name where this configuration resides (for error reporting)
     * @param index   the quest index/key inside the file (for error reporting)
     * @return {@code true} on successful load; {@code false} on configuration errors
     */
    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        ignoreNbt = section.getBoolean(".ignore_nbt");

        final boolean hasRequired = section.contains(".required") || section.contains(".required_item");
        final boolean hasRandomRequired = section.contains(".random_required");

        if (hasRequired && hasRandomRequired) {
            PluginLogger.configurationError(file, index, "required/random_required",
                    "You can't use 'required' and 'random_required' at the same time.");
            return false;
        }

        if (hasRandomRequired) {
            super.isRandomRequired = true;
            return loadRequiredItems(section, file, index, ".random_required");
        } else {
            // Accept both legacy ".required_item" and ".required"
            return loadRequiredItems(section, file, index, ".required_item")
                    && loadRequiredItems(section, file, index, ".required");
        }
    }

    /**
     * Loads required items from a given configuration path, delegating to the appropriate strategy
     * depending on whether the path indicates randomized requirements.
     *
     * @param section configuration section
     * @param file    file name (for error reporting)
     * @param index   quest index/key (for error reporting)
     * @param path    configuration path (e.g., ".required", ".random_required")
     * @return {@code true} if items were loaded (or path not present); {@code false} on errors
     */
    private boolean loadRequiredItems(ConfigurationSection section, String file, String index, String path) {
        if (!section.contains(path)) return true;
        return path.contains("random_required")
                ? loadRandomRequired(section, file, index, path)
                : loadSimpleRequired(section, file, index, path);
    }

    /**
     * Loads items for the {@code random_required} mode. Each list entry must be a one-entry map:
     * {@code MATERIAL: "Display Name"}.
     *
     * @param section configuration section
     * @param file    file name (for error reporting)
     * @param index   quest index/key (for error reporting)
     * @param path    configuration path (".random_required")
     * @return {@code true} on success; {@code false} if the list is empty or contains invalid entries
     */
    private boolean loadRandomRequired(ConfigurationSection section, String file, String index, String path) {
        final List<?> rawList = section.getList(path);
        if (rawList == null || rawList.isEmpty()) {
            PluginLogger.configurationError(file, index, path,
                    "The list of required items is empty but 'random_required' is set.");
            return false;
        }

        for (Object entry : rawList) {
            if (!(entry instanceof Map<?, ?> mapEntry)) {
                PluginLogger.configurationError(file, index, path,
                        "Each entry in 'random_required' must be a map like - MATERIAL: \"Display Name\"");
                return false;
            }
            if (!processRandomMapEntry(section, mapEntry, file, index, path)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Processes a single {@code random_required} map entry, extracting the material key and display name,
     * resolving the corresponding {@link ItemStack}, and registering both the item and its display name.
     *
     * @param section  configuration section
     * @param mapEntry the single-entry map ({@code MATERIAL: "Display Name"})
     * @param file     file name (for error reporting)
     * @param index    quest index/key (for error reporting)
     * @param path     configuration path (".random_required")
     * @return {@code true} on success; {@code false} if the item cannot be resolved
     */
    private boolean processRandomMapEntry(ConfigurationSection section, Map<?, ?> mapEntry, String file, String index, String path) {
        for (Map.Entry<?, ?> element : mapEntry.entrySet()) {
            final String key = element.getKey().toString();
            final String displayName = element.getValue().toString();

            final ItemStack requiredItem = getItem(section, key, file, index, path);
            if (requiredItem == null) return false;

            requiredItems.add(requiredItem);
            // 'displayNames' is inherited from AbstractQuest
            displayNames.add(displayName);
        }
        return true;
    }

    /**
     * Loads items for the fixed {@code required} / {@code required_item} modes.
     * Accepts either a single string or a list of strings.
     *
     * @param section configuration section
     * @param file    file name (for error reporting)
     * @param index   quest index/key (for error reporting)
     * @param path    configuration path (".required" or ".required_item")
     * @return {@code true} on success; {@code false} if any item cannot be resolved
     */
    private boolean loadSimpleRequired(ConfigurationSection section, String file, String index, String path) {
        final List<String> itemStrings = extractItemStrings(section, path);

        for (String type : itemStrings) {
            final ItemStack requiredItem = getItem(section, type, file, index, path);
            if (requiredItem == null) return false;
            requiredItems.add(requiredItem);
        }
        return true;
    }

    /**
     * Extracts one or many item type entries from a path that can be either a single string or a list.
     *
     * @param section configuration section
     * @param path    configuration path
     * @return a non-null list of item type strings (possibly empty)
     */
    private List<String> extractItemStrings(ConfigurationSection section, String path) {
        final List<String> items = new ArrayList<>();
        if (section.isList(path)) {
            items.addAll(section.getStringList(path));
        } else {
            final String single = section.getString(path);
            if (single != null && !single.isEmpty()) items.add(single);
        }
        return items;
    }

    /**
     * Resolves an {@link ItemStack} from the configuration.
     * <ul>
     *     <li>If the type is {@code CUSTOM_ITEM}, delegates to {@link QuestItemGetter#loadCustomItem}.</li>
     *     <li>Otherwise resolves a standard material and, if it is a potion, loads and applies {@link PotionMeta}.</li>
     * </ul>
     *
     * @param section configuration section
     * @param type    material name or {@code CUSTOM_ITEM}
     * @param file    file name (for error reporting)
     * @param index   quest index/key (for error reporting)
     * @param path    configuration path (for error reporting)
     * @return the resolved {@link ItemStack}, or {@code null} if invalid
     */
    private ItemStack getItem(ConfigurationSection section, String type, String file, String index, String path) {
        if (type.equalsIgnoreCase("CUSTOM_ITEM")) {
            return itemGetter.loadCustomItem(section, file, index);
        }

        final ItemStack requiredItem = itemGetter.getItemStackFromMaterial(type, file, index, path);
        if (requiredItem == null) return null;

        if (POTIONS_TYPES.contains(requiredItem.getType())) {
            final PotionMeta potionMeta = itemGetter.loadPotionItem(section, file, index, requiredItem);
            if (potionMeta != null) {
                requiredItem.setItemMeta(potionMeta);
                updateMenuItem(potionMeta);
            }
        }

        return requiredItem;
    }

    /**
     * If the quest menu icon is a potion, updates its {@link PotionMeta} to reflect required potion data
     * (useful for previewing the exact variant required).
     *
     * @param potionMeta potion metadata to apply to the quest menu item
     */
    private void updateMenuItem(PotionMeta potionMeta) {
        final ItemStack menuItem = getMenuItem();
        if (POTIONS_TYPES.contains(menuItem.getType())) {
            menuItem.setItemMeta(potionMeta);
        }
    }

    /**
     * Checks whether a provided item satisfies the quest requirements, taking into account:
     * <ul>
     *     <li>Random selection (if {@code isRandomRequired} is enabled).</li>
     *     <li>{@code ignore_nbt} flag (with potion-specific equality rules).</li>
     *     <li>Custom model data equality, when set on required items.</li>
     *     <li>{@link ItemStack#isSimilar(ItemStack)} fallback check.</li>
     * </ul>
     *
     * @param provided    the item supplied by the player
     * @param progression the current progression (used to pick the random-required index)
     * @return {@code true} if the provided item matches one of the required items, otherwise {@code false}
     */
    public boolean isRequiredItem(ItemStack provided, Progression progression) {
        if (requiredItems == null || requiredItems.isEmpty()) return true;

        final List<ItemStack> itemsToCheck = getItemsToCheck(progression);
        if (itemsToCheck.isEmpty()) return false;

        for (ItemStack item : itemsToCheck) {
            if (matchesItem(item, provided)) return true;
        }

        Debugger.write("ItemQuest:isRequiredItem: Item is not required.");
        return false;
    }

    /**
     * Returns the list of items to check against the provided item:
     * either all required items (fixed mode) or a singleton list with
     * the currently selected random-required item.
     *
     * @param progression progression data holding the selected index for random mode
     * @return list of items to check (possibly empty)
     */
    private List<ItemStack> getItemsToCheck(Progression progression) {
        if (!isRandomRequired) return requiredItems;

        final int index = progression.getSelectedRequiredIndex();
        if (index < 0 || index >= requiredItems.size()) {
            Debugger.write("ItemQuest:isRequiredItem: Invalid selectedRequiredIndex: " + index);
            return List.of();
        }

        return List.of(requiredItems.get(index));
    }

    /**
     * Determines whether a provided item matches a single required item, considering:
     * <ul>
     *     <li>{@code ignore_nbt} (with potion-specific strict checks)</li>
     *     <li>Custom model data equality</li>
     *     <li>{@link ItemStack#isSimilar(ItemStack)} fallback</li>
     * </ul>
     *
     * @param required the required item definition
     * @param provided the player-provided item
     * @return {@code true} if items match; {@code false} otherwise
     */
    private boolean matchesItem(ItemStack required, ItemStack provided) {
        Debugger.write("ItemQuest:isRequiredItem: Checking if item is required: "
                + required.getType() + " vs " + provided.getType() + ".");

        if (ignoreNbt && required.getType() == provided.getType()) {
            Debugger.write("ItemQuest:isRequiredItem: Ignoring NBT data.");
            return !POTIONS_TYPES.contains(required.getType()) || potionEquals(required, provided);
        }

        if (hasMatchingCustomModelData(required, provided)) return true;

        if (required.isSimilar(provided)) {
            Debugger.write("ItemQuest:isRequiredItem: Item is similar.");
            return true;
        }

        Debugger.write("ItemQuest:isRequiredItem: Item is not similar.");
        return false;
    }

    /**
     * Compares potion variants by base type, upgrade, and extended flags.
     *
     * @param required the required potion item
     * @param provided the provided potion item
     * @return {@code true} if both potions are equivalent; otherwise {@code false}
     */
    private boolean potionEquals(ItemStack required, ItemStack provided) {
        Debugger.write("ItemQuest:isRequiredItem: Required item is a potion.");
        final PotionMeta reqMeta = (PotionMeta) required.getItemMeta();
        final PotionMeta provMeta = (PotionMeta) provided.getItemMeta();

        final boolean sameType = reqMeta.getBasePotionData().getType() == provMeta.getBasePotionData().getType();
        final boolean sameUpgrade = reqMeta.getBasePotionData().isUpgraded() == provMeta.getBasePotionData().isUpgraded();
        final boolean sameExtended = reqMeta.getBasePotionData().isExtended() == provMeta.getBasePotionData().isExtended();

        if (!sameType) Debugger.write("ItemQuest:isRequiredItem: Potion type is different.");
        if (!sameUpgrade) Debugger.write("ItemQuest:isRequiredItem: Potion is upgraded.");
        if (!sameExtended) Debugger.write("ItemQuest:isRequiredItem: Potion is extended.");

        return sameType && sameUpgrade && sameExtended;
    }

    /**
     * Checks whether both items have matching custom model data for the same material.
     *
     * @param required the required item
     * @param provided the provided item
     * @return {@code true} if both have identical custom model data; otherwise {@code false}
     */
    private boolean hasMatchingCustomModelData(ItemStack required, ItemStack provided) {
        if (!required.hasItemMeta() || !required.getItemMeta().hasCustomModelData()) return false;

        Debugger.write("ItemQuest:isRequiredItem: Required item has custom model data.");

        if (provided.hasItemMeta() && provided.getItemMeta().hasCustomModelData()) {
            Debugger.write("ItemQuest:isRequiredItem: Provided item has custom model data.");
            return required.getType() == provided.getType()
                    && required.getItemMeta().getCustomModelData() == provided.getItemMeta().getCustomModelData();
        }

        Debugger.write("ItemQuest:isRequiredItem: Provided item does not have custom model data.");
        return false;
    }
    /**
     * Returns the list of required items currently configured for this quest.
     * <p>In random-required mode, this is the full pool (not the selected item).</p>
     *
     * @return the required items (possibly empty)
     */
    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }
}
