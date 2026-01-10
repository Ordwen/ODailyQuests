package com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters;

import com.ordwen.odailyquests.nms.NMSHandler;
import com.ordwen.odailyquests.tools.externals.ExternalItemGetter;
import com.ordwen.odailyquests.tools.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    /**
     * Add item model to an item.
     *
     * @param itemModel the item model key
     * @return the ItemStack with the item model
     */
    public Pair<String, ItemStack> getItemModelItem(String itemModel) {
        if (itemModel == null || itemModel.isEmpty()) {
            return new Pair<>("Item model cannot be empty.", null);
        }

        final ItemStack item = new ItemStack(Material.PAPER);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return new Pair<>("Unable to create metadata for item model.", null);
        }

        final boolean applied = NMSHandler.trySetItemModel(meta, itemModel);
        if (!applied) {
            return new Pair<>("Item model feature is not supported on this server version or the key is invalid.", null);
        }

        item.setItemMeta(meta);
        return new Pair<>("", item);
    }
}
