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

        return loadRequiredItems(section, file, index, ".required_item")
                && loadRequiredItems(section, file, index, ".required");
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

    public boolean isRequiredItem(ItemStack provided) {
        if (requiredItems == null || requiredItems.isEmpty()) return true;

        for (ItemStack item : requiredItems) {
            Debugger.write("ItemQuest:isRequiredItem: Checking if item is required: " + item.getType() + " vs " + provided.getType() + ".");

            if (ignoreNbt && item.getType() == provided.getType()) {
                Debugger.write("ItemQuest:isRequiredItem: Ignoring NBT data.");

                // check if potion
                if (POTIONS_TYPES.contains(item.getType())) {
                    Debugger.write("ItemQuest:isRequiredItem: Required item is a potion.");
                    boolean canProgress = true;

                    final PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                    final PotionMeta providedPotionMeta = (PotionMeta) provided.getItemMeta();

                    // check potion type
                    if (potionMeta.getBasePotionData().getType() != providedPotionMeta.getBasePotionData().getType()) {
                        Debugger.write("ItemQuest:isRequiredItem: Potion type is different.");
                        canProgress = false;
                    }

                    // check upgraded
                    if (potionMeta.getBasePotionData().isUpgraded() != providedPotionMeta.getBasePotionData().isUpgraded()) {
                        Debugger.write("ItemQuest:isRequiredItem: Potion is upgraded.");
                        canProgress = false;
                    }

                    // check extended
                    if (potionMeta.getBasePotionData().isExtended() != providedPotionMeta.getBasePotionData().isExtended()) {
                        Debugger.write("ItemQuest:isRequiredItem: Potion is extended.");
                        canProgress = false;
                    }

                    if (!canProgress) continue;
                }

                return true;
            }

            if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                Debugger.write("ItemQuest:isRequiredItem: Required item has custom model data.");
                if (provided.hasItemMeta() && provided.getItemMeta().hasCustomModelData()) {
                    Debugger.write("ItemQuest:isRequiredItem: Provided item has custom model data.");
                    return item.getType() == provided.getType() && item.getItemMeta().getCustomModelData() == provided.getItemMeta().getCustomModelData();
                }
                Debugger.write("ItemQuest:isRequiredItem: Provided item does not have custom model data.");
                return false;
            }

            if (item.isSimilar(provided)) {
                Debugger.write("ItemQuest:isRequiredItem: Item is similar.");
                return true;
            }

            Debugger.write("ItemQuest:isRequiredItem: Item is not similar.");
        }
        Debugger.write("ItemQuest:isRequiredItem: Item is not required.");
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
