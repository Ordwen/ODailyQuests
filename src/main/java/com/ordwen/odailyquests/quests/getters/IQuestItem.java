package com.ordwen.odailyquests.quests.getters;

import org.bukkit.inventory.ItemStack;

public interface IQuestItem {

    ItemStack getItem(String material, String fileName, String questIndex, String parameter);

    ItemStack getOraxenItem(String namespace, String fileName, String questIndex, String parameter);

    ItemStack getItemsAdderItem(String namespace, String fileName, String questIndex, String parameter);

    ItemStack getMMOItemsItem(String namespace, String fileName, String questIndex, String parameter);

    ItemStack getCustomHead(String texture, String fileName, String questIndex, String parameter);

    ItemStack getCustomModelDataItem(String customModelData, String fileName, String questIndex, String parameter);
}
