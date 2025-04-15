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
import java.util.Set;

public abstract class ItemQuest extends AbstractQuest {

    private static final Set<Material> POTIONS_TYPES = Set.of(
            Material.POTION,
            Material.SPLASH_POTION,
            Material.LINGERING_POTION);

    private static final QuestItemGetter itemGetter = new QuestItemGetter();

    private final List<ItemStack> requiredItems;
    private boolean ignoreNbt = false;

    /**
     * Quest constructor.
     *
     * @param base parent quest.
     */
    protected ItemQuest(BasicQuest base) {
        super(base);
        this.requiredItems = new ArrayList<>();
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        ignoreNbt = section.getBoolean(".ignore_nbt");

        boolean hasRequired = section.contains(".required") || section.contains(".required_item");
        boolean hasRandomRequired = section.contains(".random_required");

        if (hasRequired && hasRandomRequired) {
            PluginLogger.configurationError(file, index, "required/random_required", "You can't use 'required' and 'random_required' at the same time.");
            return false;
        }

        if (hasRandomRequired) {
            super.isRandomRequired = true;
            return loadRequiredItems(section, file, index, ".random_required");
        } else {
            return loadRequiredItems(section, file, index, ".required_item") && loadRequiredItems(section, file, index, ".required");
        }
    }

    /**
     * Load the required items from a given configuration path.
     *
     * @param section the current quest section.
     * @param file    the file name where the quest is.
     * @param index   the quest index in the file.
     * @param path    the configuration path to check.
     * @return true if the required items are loaded successfully, false otherwise.
     */
    private boolean loadRequiredItems(ConfigurationSection section, String file, String index, String path) {
        if (!section.contains(path)) return true;

        final List<String> requiredItemStrings = new ArrayList<>();
        if (section.isList(path)) requiredItemStrings.addAll(section.getStringList(path));
        else requiredItemStrings.add(section.getString(path));

        for (String type : requiredItemStrings) {
            final ItemStack requiredItem = getItem(section, type, file, index, path);
            if (requiredItem == null) return false;

            requiredItems.add(requiredItem);
        }

        return true;
    }

    /**
     * Get the item from the configuration section.
     *
     * @param section the configuration section.
     * @param type    the item type.
     * @param file    the file name.
     * @param index   the quest index.
     * @param path    the path to check.
     * @return the item stack, or null if the item is invalid.
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
     * Update the menu item.
     *
     * @param potionMeta the potion meta.
     */
    private void updateMenuItem(PotionMeta potionMeta) {
        final ItemStack menuItem = getMenuItem();
        if (POTIONS_TYPES.contains(menuItem.getType())) {
            menuItem.setItemMeta(potionMeta);
        }
    }

    public boolean isRequiredItem(ItemStack provided, Progression progression) {
        if (requiredItems == null || requiredItems.isEmpty()) return true;

        List<ItemStack> itemsToCheck = getItemsToCheck(progression);
        if (itemsToCheck.isEmpty()) return false;

        for (ItemStack item : itemsToCheck) {
            if (matchesItem(item, provided)) return true;
        }

        Debugger.write("ItemQuest:isRequiredItem: Item is not required.");
        return false;
    }

    private List<ItemStack> getItemsToCheck(Progression progression) {
        if (!isRandomRequired) return requiredItems;

        int index = progression.getSelectedRequiredIndex();
        if (index < 0 || index >= requiredItems.size()) {
            Debugger.write("ItemQuest:isRequiredItem: Invalid selectedRequiredIndex: " + index);
            return List.of();
        }

        return List.of(requiredItems.get(index));
    }

    private boolean matchesItem(ItemStack required, ItemStack provided) {
        Debugger.write("ItemQuest:isRequiredItem: Checking if item is required: " + required.getType() + " vs " + provided.getType() + ".");

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

    private boolean potionEquals(ItemStack required, ItemStack provided) {
        Debugger.write("ItemQuest:isRequiredItem: Required item is a potion.");
        PotionMeta reqMeta = (PotionMeta) required.getItemMeta();
        PotionMeta provMeta = (PotionMeta) provided.getItemMeta();

        boolean sameType = reqMeta.getBasePotionData().getType() == provMeta.getBasePotionData().getType();
        boolean sameUpgrade = reqMeta.getBasePotionData().isUpgraded() == provMeta.getBasePotionData().isUpgraded();
        boolean sameExtended = reqMeta.getBasePotionData().isExtended() == provMeta.getBasePotionData().isExtended();

        if (!sameType) Debugger.write("ItemQuest:isRequiredItem: Potion type is different.");
        if (!sameUpgrade) Debugger.write("ItemQuest:isRequiredItem: Potion is upgraded.");
        if (!sameExtended) Debugger.write("ItemQuest:isRequiredItem: Potion is extended.");

        return sameType && sameUpgrade && sameExtended;
    }

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
     * Get the required items.
     *
     * @return required items.
     */
    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }
}
