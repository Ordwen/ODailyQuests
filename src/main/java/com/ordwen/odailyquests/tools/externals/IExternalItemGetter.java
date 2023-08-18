package com.ordwen.odailyquests.tools.externals;

import com.ordwen.odailyquests.tools.Pair;
import org.bukkit.inventory.ItemStack;

public interface IExternalItemGetter {

    Pair<String, ItemStack> getOraxenItem(String namespace);
    Pair<String, ItemStack> getItemsAdderItem(String namespace);
    Pair<String, ItemStack> getMMOItemsItem(String namespace);
    Pair<String, ItemStack> getCustomHead(String texture);
}
