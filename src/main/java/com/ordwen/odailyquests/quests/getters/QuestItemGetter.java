package com.ordwen.odailyquests.quests.getters;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters.ItemGetter;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;

public class QuestItemGetter extends ItemGetter implements IQuestItem {

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
    public ItemStack getItem(String material, String fileName, String questIndex, String parameter) {
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
    public ItemStack getOraxenItem(String namespace, String fileName, String questIndex, String parameter) {
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
    public ItemStack getItemsAdderItem(String namespace, String fileName, String questIndex, String parameter) {
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
    public ItemStack getMMOItemsItem(String namespace, String fileName, String questIndex, String parameter) {
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
    public ItemStack getCustomModelDataItem(String customModelData, String fileName, String questIndex, String parameter) {
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
    public ItemStack getCustomHead(String texture, String fileName, String questIndex, String parameter) {
        final Pair<String, ItemStack> result = super.getCustomHead(texture);
        if (!result.first().isEmpty()) {
            PluginLogger.configurationError(fileName, questIndex, parameter, result.first());
            return null;
        }

        return result.second();
    }

    /**
     * Load a required item with custom name and lore.
     *
     * @param provided quest configuration section
     * @param file     file name
     * @param index    quest index
     * @return the custom item
     */
    public ItemStack loadCustomItem(ConfigurationSection provided, String file, String index) {
        final ConfigurationSection section = provided.getConfigurationSection(".custom_item");
        if (section == null) {
            PluginLogger.configurationError(file, index, null, "The custom item is not defined.");
            return null;
        }

        final String type = section.getString(".type");
        if (type == null) {
            PluginLogger.configurationError(file, index, "type", "The type of the custom item is not defined.");
            return null;
        }

        final ItemStack requiredItem = getItemStackFromMaterial(type, file, index, "type (CUSTOM_ITEM)");
        if (requiredItem == null) return null;

        final ItemMeta meta = requiredItem.getItemMeta();
        if (meta == null) {
            PluginLogger.configurationError(file, index, null, "The custom item cannot have a custom name or lore.");
            return null;
        }

        meta.setDisplayName(TextFormatter.format(section.getString(".name")));

        final List<String> lore = section.getStringList(".lore");
        for (String str : lore) {
            lore.set(lore.indexOf(str), TextFormatter.format(str));
        }
        meta.setLore(lore);
        requiredItem.setItemMeta(meta);

        return requiredItem;
    }

    /**
     * Get an item stack from a material.
     *
     * @param material   the material to get
     * @param fileName   the file name
     * @param questIndex the quest index
     * @return the item stack
     */
    public ItemStack getItemStackFromMaterial(String material, String fileName, String questIndex, String parameter) {
        final ItemStack requiredItem;

        if (material.contains(":")) {
            requiredItem = getItem(material, fileName, questIndex, parameter);
            if (requiredItem == null) {
                PluginLogger.configurationError(fileName, questIndex, parameter, "Invalid material type detected.");
                return null;
            }
        } else {
            try {
                requiredItem = new ItemStack(Material.valueOf(material));
            } catch (Exception e) {
                PluginLogger.configurationError(fileName, questIndex, parameter, "Invalid material type detected.");
                return null;
            }
        }

        return requiredItem;
    }

    /**
     * Load the potion attributes.
     *
     * @param section      quest section
     * @param fileName     file name where the quest is
     * @param questIndex   quest index in the file
     * @param requiredItem current required item
     * @return potion meta
     */
    public PotionMeta loadPotionItem(ConfigurationSection section, String fileName, String questIndex, ItemStack requiredItem) {
        PotionMeta potionMeta = null;

        PotionType potionType;
        boolean upgraded = false;
        boolean extended = false;

        final ConfigurationSection potionSection = section.getConfigurationSection(".potion");
        if (potionSection == null) return null;

        if (potionSection.contains("type")) {
            try {
                potionType = PotionType.valueOf(potionSection.getString("type"));
            } catch (IllegalArgumentException e) {
                PluginLogger.configurationError(fileName, questIndex, "type", "Invalid potion type.");
                return null;
            }
        } else {
            PluginLogger.configurationError(fileName, questIndex, "type", "Potion type is not defined.");
            return null;
        }

        if (potionSection.contains("upgraded")) upgraded = potionSection.getBoolean("upgraded");
        if (potionSection.contains("extended")) extended = potionSection.getBoolean("extended");

        if (upgraded && extended) {
            PluginLogger.configurationError(fileName, questIndex, null, "Potion cannot be both upgraded and extended.");
            return null;
        }

        if (requiredItem.getType() == Material.POTION
                || requiredItem.getType() == Material.SPLASH_POTION
                || requiredItem.getType() == Material.LINGERING_POTION) {

            potionMeta = (PotionMeta) requiredItem.getItemMeta();
            if (potionMeta == null) return null;

            potionMeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
        }

        return potionMeta;
    }
}
