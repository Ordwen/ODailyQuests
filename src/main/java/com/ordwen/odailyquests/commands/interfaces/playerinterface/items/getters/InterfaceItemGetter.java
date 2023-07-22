package com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters;

import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InterfaceItemGetter extends ItemGetter implements IInterfaceItem {

    /**
     * Get an item from a string.
     * @param material the material of the item
     * @param itemIndex the index of the item in the file
     * @param parameter the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getItem(String material, String itemIndex, String parameter) {
        final String[] split = material.split(":", 2);
        return switch (split[0]) {
            case "oraxen" -> this.getOraxenItem(split[1], itemIndex, parameter);
            case "itemsadder" -> this.getItemsAdderItem(split[1], itemIndex, parameter);
            case "customhead" -> this.getCustomHead(split[1], itemIndex, parameter);
            case "custommodeldata" -> this.getCustomModelDataItem(split[1], itemIndex, parameter);
            default -> null;
        };
    }

    /**
     * Get an item from Oraxen.
     * @param namespace the namespace of the item
     * @param itemIndex the index of the item in the file
     * @param parameter the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getOraxenItem(String namespace, String itemIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getOraxenItem(namespace);
        if (!result.first().isEmpty()) {
            configurationError(itemIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Get an item from ItemsAdder.
     * @param namespace the namespace of the item
     * @param itemIndex the index of the item in the file
     * @param parameter the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getItemsAdderItem(String namespace, String itemIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getItemsAdderItem(namespace);
        if (!result.first().isEmpty()) {
            configurationError(itemIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Get a custom head.
     * @param texture the texture of the head
     * @param questIndex the index of the quest in the file
     * @param parameter the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getCustomHead(String texture, String questIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getCustomHead(texture);
        if (!result.first().isEmpty()) {
            configurationError(questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Get an item with custom model data.
     * @param customModelData the custom model data of the item
     * @param questIndex the index of the quest in the file
     * @param parameter the parameter involved
     * @return the ItemStack or null if the item cannot be loaded
     */
    @Override
    public ItemStack getCustomModelDataItem(String customModelData, String questIndex, String parameter) {

        final String[] split = customModelData.split(":");
        if (split.length != 2) {
            configurationError(questIndex, parameter, "You need to provide the item and the custom model data.");
            return null;
        }

        final Material material = Material.getMaterial(split[0].toUpperCase());
        if (material == null) {
            configurationError(questIndex, parameter, "The material " + split[0] + " does not exist.");
            return null;
        }

        int cmd;
        try {
            cmd = Integer.parseInt(customModelData);
        } catch (Exception e) {
            configurationError(questIndex, parameter, customModelData + " is not a number!");
            return null;
        }

        final Pair<String, ItemStack> result = super.getCustomModelDataItem(material, cmd);
        if (!result.first().isEmpty()) {
            configurationError(questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Display an error message in the console when an interface item cannot be loaded.
     *
     * @param itemIndex the index of the item in the file
     * @param parameter  the parameter that caused the error
     * @param reason     the reason of the error
     */
    public void configurationError(String itemIndex, String parameter, String reason) {
        PluginLogger.error("-----------------------------------");
        PluginLogger.error("Invalid player interface configuration detected.");
        PluginLogger.error("Item index : " + itemIndex);
        PluginLogger.error("Reason : " + reason);

        if (parameter != null) {
            PluginLogger.error("Parameter : " + parameter);
        }

        PluginLogger.error("-----------------------------------");
    }
}
