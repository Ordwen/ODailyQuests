package com.ordwen.odailyquests.quests.getters;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters.ItemGetter;
import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class QuestItemGetter extends ItemGetter implements IQuestItem {

    /**
     * Get an item from a string.
     *
     * @param material   the material of the item
     * @param fileName   the name of the file where the item is
     * @param questIndex the index of the quest in the file
     * @param parameter  the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getItem(String material, String fileName, int questIndex, String parameter) {
        final String[] split = material.split(":", 2);
        return switch (split[0]) {
            case "oraxen" -> this.getOraxenItem(split[1], fileName, questIndex, parameter);
            case "itemsadder" -> this.getItemsAdderItem(split[1], fileName, questIndex, parameter);
            case "mmoitems" -> this.getMMOItemsItem(split[1], fileName, questIndex, parameter);
            case "customhead" -> this.getCustomHead(split[1], fileName, questIndex, parameter);
            case "custommodeldata" -> this.getCustomModelDataItem(split[1], fileName, questIndex, parameter);
            default -> null;
        };
    }

    /**
     * Get an item from Oraxen.
     *
     * @param namespace  the namespace of the item
     * @param fileName   the name of the file where the item is
     * @param questIndex the index of the quest in the file
     * @param parameter  the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getOraxenItem(String namespace, String fileName, int questIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getOraxenItem(namespace);
        if (!result.first().isEmpty()) {
            PluginLogger.configurationError(fileName, questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Get an item from ItemsAdder.
     *
     * @param namespace  the namespace of the item
     * @param fileName   the name of the file where the item is
     * @param questIndex the index of the quest in the file
     * @param parameter  the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getItemsAdderItem(String namespace, String fileName, int questIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getItemsAdderItem(namespace);
        if (!result.first().isEmpty()) {
            PluginLogger.configurationError(fileName, questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Get an item from MMOItems.
     *
     * @param namespace  the namespace of the item
     * @param fileName   the name of the file where the item is
     * @param questIndex the index of the quest in the file
     * @param parameter  the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getMMOItemsItem(String namespace, String fileName, int questIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getMMOItemsItem(namespace);
        if (!result.first().isEmpty()) {
            PluginLogger.configurationError(fileName, questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Get an item with custom model data.
     *
     * @param customModelData the custom model data of the item
     * @param fileName        the name of the file where the item is
     * @param questIndex      the index of the quest in the file
     * @param parameter       the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getCustomModelDataItem(String customModelData, String fileName, int questIndex, String parameter) {
        final String[] split = customModelData.split(":");
        if (split.length != 2) {
            PluginLogger.configurationError(fileName, questIndex, parameter, "You need to provide the item and the custom model data.");
            return null;
        }

        final Material material = Material.getMaterial(split[0].toUpperCase());
        if (material == null) {
            PluginLogger.configurationError(fileName, questIndex, parameter, "The material " + split[0] + " does not exist.");
            return null;
        }

        int cmd;
        try {
            cmd = Integer.parseInt(split[1]);
        } catch (Exception e) {
            PluginLogger.configurationError(fileName, questIndex, parameter, split[1] + " is not a number!");
            return null;
        }

        final Pair<String, ItemStack> result = super.getCustomModelDataItem(material, cmd);

        if (!result.first().isEmpty()) {
            PluginLogger.configurationError(fileName, questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Get a custom head.
     *
     * @param texture    the texture of the head
     * @param fileName   the name of the file where the item is
     * @param questIndex the index of the quest in the file
     * @param parameter  the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getCustomHead(String texture, String fileName, int questIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getCustomHead(texture);
        if (!result.first().isEmpty()) {
            PluginLogger.configurationError(fileName, questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }
}
