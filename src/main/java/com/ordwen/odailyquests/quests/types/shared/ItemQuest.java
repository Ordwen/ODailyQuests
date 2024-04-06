package com.ordwen.odailyquests.quests.types.shared;

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
        if (requiredItems == null) return true;

        for (ItemStack item : requiredItems) {

            if (ignoreNbt && item.getType() == provided.getType()) {
                return true;
            }

            if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                if (provided.hasItemMeta() && provided.getItemMeta().hasCustomModelData()) {
                    return item.getType() == provided.getType() && item.getItemMeta().getCustomModelData() == provided.getItemMeta().getCustomModelData();
                }
                return false;
            }

            if (item.isSimilar(provided)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        if (!section.contains(".required_item")) return true;
        ignoreNbt = section.getBoolean(".ignore_nbt");

        final QuestItemGetter itemGetter = new QuestItemGetter();

        final List<String> requiredItemStrings = new ArrayList<>();
        if (section.isList(".required_item")) requiredItemStrings.addAll(section.getStringList(".required_item"));
        else requiredItemStrings.add(section.getString(".required_item"));

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

                final ItemStack menuItem = getMenuItem();
                if (POTIONS_TYPES.contains(menuItem.getType())) {
                    menuItem.setItemMeta(potionMeta);
                }
            }

            requiredItems.add(requiredItem);
        }

        return true;
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
