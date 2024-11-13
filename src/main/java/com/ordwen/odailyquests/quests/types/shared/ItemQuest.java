package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.getters.QuestItemGetter;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ItemQuest extends AbstractQuest {

    private static final Set<Material> POTIONS_TYPES = Set.of(Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION);
    private static final String TYPE_PATH = ".required_item";

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

    public boolean isRequiredItem(ItemStack provided) {
        if (requiredItems == null || requiredItems.isEmpty()) return true;

        for (ItemStack item : requiredItems) {
            Debugger.addDebug("ItemQuest:isRequiredItem: Checking if item is required: " + item.getType() + " vs " + provided.getType() + ".");

            if (ignoreNbt && item.getType() == provided.getType()) {
                Debugger.addDebug("ItemQuest:isRequiredItem: Ignoring NBT data.");
                return true;
            }

            if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                Debugger.addDebug("ItemQuest:isRequiredItem: Required item has custom model data.");
                if (provided.hasItemMeta() && provided.getItemMeta().hasCustomModelData()) {
                    Debugger.addDebug("ItemQuest:isRequiredItem: Provided item has custom model data.");
                    return item.getType() == provided.getType() && item.getItemMeta().getCustomModelData() == provided.getItemMeta().getCustomModelData();
                }
                Debugger.addDebug("ItemQuest:isRequiredItem: Provided item does not have custom model data.");
                return false;
            }

            if (item.isSimilar(provided)) {
                Debugger.addDebug("ItemQuest:isRequiredItem: Item is similar.");
                return true;
            }

            Debugger.addDebug("ItemQuest:isRequiredItem: Item is not similar.");
        }
        Debugger.addDebug("ItemQuest:isRequiredItem: Item is not required.");
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        if (!section.contains(TYPE_PATH)) return true;
        ignoreNbt = section.getBoolean(".ignore_nbt");

        final QuestItemGetter itemGetter = new QuestItemGetter();

        final List<String> requiredItemStrings = new ArrayList<>();
        if (section.isList(TYPE_PATH)) requiredItemStrings.addAll(section.getStringList(TYPE_PATH));
        else requiredItemStrings.add(section.getString(TYPE_PATH));

        return loadRequiredItems(section, file, index, requiredItemStrings, itemGetter);
    }

    /**
     * Load the required items.
     *
     * @param section             the current quest section.
     * @param file                the file name where the quest is.
     * @param index               the quest index in the file.
     * @param requiredItemStrings the required item strings.
     * @param itemGetter          the item getter.
     * @return true if the required items are loaded, false otherwise.
     */
    private boolean loadRequiredItems(ConfigurationSection section, String file, String index, List<String> requiredItemStrings, QuestItemGetter itemGetter) {
        for (String type : requiredItemStrings) {
            if (type.equalsIgnoreCase("CUSTOM_ITEM")) {
                final ItemStack item = itemGetter.loadCustomItem(section, file, index);
                if (item == null) return false;
                requiredItems.add(item);
                continue;
            }

            final ItemStack requiredItem = itemGetter.getItemStackFromMaterial(type, file, index, "required_item");
            if (requiredItem == null) return false;

            if (POTIONS_TYPES.contains(requiredItem.getType())) {
                final PotionMeta potionMeta = itemGetter.loadPotionItem(section, file, index, requiredItem);
                if (potionMeta == null) return false;

                requiredItem.setItemMeta(potionMeta);
                updateMenuItem(potionMeta);
            }

            requiredItems.add(requiredItem);
        }

        return true;
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

    /**
     * Check if the NBT data should be ignored.
     *
     * @return true if the NBT data should be ignored, false otherwise.
     */
    public boolean isIgnoreNbt() {
        return ignoreNbt;
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
