package com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters;

import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.externals.ExternalItemGetter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemGetter extends ExternalItemGetter implements IItemGetter {

    /**
     * Add custom model data to an item.
     *
     * @param material        the material of the item
     * @param customModelData the custom model data of the item
     * @return the ItemStack with the custom model data
     */
    @Override
    public Pair<String, ItemStack> getCustomModelDataItem(Material material, int customModelData) {
        final ItemStack item = new ItemStack(material);
        final var itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            return new Pair<>("Impossible to apply the custom model data.", null);
        }

        itemMeta.setCustomModelData(customModelData);
        item.setItemMeta(itemMeta);

        return new Pair<>("", item);

    }
}
