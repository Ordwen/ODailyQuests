package com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters;

import com.ordwen.odailyquests.tools.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface IItemGetter {
    Pair<String, ItemStack> getCustomModelDataItem(Material material, int customModelData);
}
