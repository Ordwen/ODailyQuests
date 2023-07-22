package com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters;

import org.bukkit.inventory.ItemStack;

public interface IInterfaceItem {

    ItemStack getItem(String material, String itemIndex, String parameter);
    ItemStack getOraxenItem(String namespace, String itemIndex, String parameter);
    ItemStack getItemsAdderItem(String namespace, String itemIndex, String parameter);
    ItemStack getCustomHead(String texture, String questIndex, String parameter);
    ItemStack getCustomModelDataItem(String customModelData, String questIndex, String parameter);

    void configurationError(String itemIndex, String parameter, String reason);

}
