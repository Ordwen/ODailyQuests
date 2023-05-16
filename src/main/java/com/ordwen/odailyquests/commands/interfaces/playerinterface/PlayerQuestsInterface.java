package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerQuestsInterface {

    /* init variables */
    private static String interfaceName;
    private static Inventory playerQuestsInventoryBase;
    private static int size;
    private static String achieved;
    private static String status;
    private static String progression;
    private static String completeGetType;
    private static boolean isPlayerHeadEnabled;
    private static boolean isGlowingEnabled;

    /* item slots */
    private static int slotPlayerHead = -1;
    private static final HashMap<Integer, List<Integer>> slotQuests = new HashMap<>();

    /* item lists */
    private static HashSet<ItemStack> fillItems;
    private static HashSet<ItemStack> closeItems;
    private static HashMap<Integer, List<String>> playerCommandsItems;
    private static HashMap<Integer, List<String>> consoleCommandsItems;

    /* items with placeholders */
    private static final HashMap<ItemStack, Integer> papiItems = new HashMap<>();

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        ConfigurationSection interfaceConfig = PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface");
        interfaceName = ColorConvert.convertColorCode(interfaceConfig.getString(".inventory_name"));
        isPlayerHeadEnabled = interfaceConfig.getConfigurationSection("player_head").getBoolean(".enabled");
        isGlowingEnabled = interfaceConfig.getBoolean("glowing_if_achieved");

        /* load item slots */
        if (isPlayerHeadEnabled) {
            final ConfigurationSection section = interfaceConfig.getConfigurationSection("player_head");
            slotPlayerHead = section.getInt(".slot") - 1;
        }

        /* create base of inventory */
        size = interfaceConfig.getInt(".size");
        playerQuestsInventoryBase = Bukkit.createInventory(null, size, "BASIC");

        /* load all texts */
        achieved = interfaceConfig.getString(".achieved");
        status = interfaceConfig.getString(".status");
        progression = interfaceConfig.getString(".progress");
        completeGetType = interfaceConfig.getString(".complete_get_type");

        /* load all items */
        fillItems = new HashSet<>();
        closeItems = new HashSet<>();
        playerCommandsItems = new HashMap<>();
        consoleCommandsItems = new HashMap<>();

        final ConfigurationSection questsSection = interfaceConfig.getConfigurationSection("quests");

        slotQuests.clear();
        for (String index : questsSection.getKeys(false)) {
            int slot = Integer.parseInt(index) - 1;
            if (questsSection.isList(index)) {
                final List<Integer> values = questsSection.getIntegerList(index);
                slotQuests.put(slot, values);
            }
            else {
                int value = questsSection.getInt(index);
                slotQuests.put(slot, Collections.singletonList(value));
            }
        }

        final ConfigurationSection itemsSection = interfaceConfig.getConfigurationSection("items");

        if (itemsSection != null) {
            for (String element : itemsSection.getKeys(false)) {

                final ConfigurationSection itemData = itemsSection.getConfigurationSection(element + ".item");
                final String material = itemData.getString("material");
                ItemStack item;

                if (material.equals("CUSTOM_HEAD")) {
                    final String texture = itemData.getString("texture");
                    item = Items.getCustomHead(texture);
                } else item = new ItemStack(Material.valueOf(material));

                int slot = itemData.getInt("slot") - 1;

                final String itemType = itemsSection.getString(element + ".type");
                switch (ItemType.valueOf(itemType)) {

                    case FILL -> {
                        ItemMeta fillItemMeta = item.getItemMeta();
                        fillItemMeta.setDisplayName(ChatColor.RESET + "");
                        item.setItemMeta(fillItemMeta);
                        fillItems.add(item);
                    }

                    case CLOSE -> {
                        item.setItemMeta(getItemMeta(item, itemData));
                        closeItems.add(item);
                    }

                    case PLAYER_COMMAND -> {
                        List<String> commands = itemsSection.getStringList(element + ".commands");
                        item.setItemMeta(getItemMeta(item, itemData));

                        playerCommandsItems.put(slot, commands);
                    }

                    case CONSOLE_COMMAND -> {
                        List<String> commands = itemsSection.getStringList(element + ".commands");
                        item.setItemMeta(getItemMeta(item, itemData));

                        consoleCommandsItems.put(slot, commands);
                    }
                }

                if (itemsSection.contains(element + ".use_placeholders") && itemsSection.getBoolean(element + ".use_placeholders")) {
                    papiItems.put(item, slot);
                }

                playerQuestsInventoryBase.setItem(slot, item);
            }
        }

        PluginLogger.fine("Player quests interface successfully loaded.");
    }

    /**
     * Load the ItemMeta of an item.
     *
     * @param itemStack item to load.
     * @param section section of the item.
     * @return ItemMeta of the item.
     */
    private ItemMeta getItemMeta(ItemStack itemStack, ConfigurationSection section) {
        ItemMeta meta = itemStack.getItemMeta();
        if (section.contains("custom_model_data")) meta.setCustomModelData(section.getInt("custom_model_data"));

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(section.getString("name"))));

        List<String> lore = section.getStringList("lore");
        for (String str : lore) {
            lore.set(lore.indexOf(str), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(str)));
        }
        meta.setLore(lore);

        return meta;
    }

    /**
     * Get player quests inventory.
     *
     * @return player quests inventory.
     */
    public static Inventory getPlayerQuestsInterface(Player player) {

        Inventory playerQuestsInventoryIndividual = Bukkit.createInventory(null, size, PAPIHook.getPlaceholders(player, interfaceName));
        playerQuestsInventoryIndividual.setContents(playerQuestsInventoryBase.getContents());

        if (!papiItems.isEmpty()) {
            for (ItemStack item : papiItems.keySet()) {

                final ItemStack itemCopy = item.clone();
                final ItemMeta meta = itemCopy.getItemMeta();
                final List<String> lore = meta.getLore();

                for (String str : lore) {
                    lore.set(lore.indexOf(str), PAPIHook.getPlaceholders(player, str));
                }
                meta.setLore(lore);
                itemCopy.setItemMeta(meta);
                playerQuestsInventoryIndividual.setItem(papiItems.get(item), itemCopy);
            }
        }

        HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        HashMap<AbstractQuest, Progression> playerQuests = activeQuests.get(player.getName()).getPlayerQuests();

        /* load player head */
        if (isPlayerHeadEnabled) {
            playerQuestsInventoryIndividual.setItem(slotPlayerHead, PlayerHead.getPlayerHead(player));
        }

        /* load quests */
        int i = 0;
        for (AbstractQuest quest : playerQuests.keySet()) {

            ItemStack itemStack = quest.getMenuItem().clone();

            ItemMeta itemMeta = itemStack.getItemMeta().clone();
            itemMeta.setDisplayName(quest.getQuestName());

            List<String> lore = new ArrayList<>(quest.getQuestDesc());
            lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(status)));

            if (playerQuests.get(quest).isAchieved()) {
                if (isGlowingEnabled) {
                    itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
                }
                lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(achieved)));
            } else {
                if (quest.getType() == QuestType.GET) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(completeGetType)));
                } else {
                    lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(progression))
                            .replace("%progress%", String.valueOf(playerQuests.get(quest).getProgression()))
                            .replace("%required%", String.valueOf(quest.getAmountRequired())));
                }
            }

            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            if (slotQuests.get(i) != null) {
                for (int slot : slotQuests.get(i)) {
                    playerQuestsInventoryIndividual.setItem(slot - 1, itemStack);
                }
            }
            else {
                PluginLogger.error("An error occurred when loading the player interface.");
                PluginLogger.error("The slot for the quest number " + (i+1) + " is not defined in the playerInterface file.");
            }

            i++;
        }
        return playerQuestsInventoryIndividual;
    }

    /**
     * Get the name of the interface.
     * @param player player to get the name.
     * @return name of the interface.
     */
    public static String getInterfaceName(Player player) {
        return PAPIHook.getPlaceholders(player, interfaceName);
    }

    /**
     * Get all fill items.
     *
     * @return fill items set.
     */
    public static HashSet<ItemStack> getFillItems() {
        return fillItems;
    }

    /**
     * Get all player command items.
     *
     * @return player command items map.
     */
    public static HashMap<Integer, List<String>> getPlayerCommandsItems() {
        return playerCommandsItems;
    }

    /**
     * Get all console command items.
     *
     * @return console command items map.
     */
    public static HashMap<Integer, List<String>> getConsoleCommandsItems() {
        return consoleCommandsItems;
    }

    /**
     * Get all close items.
     *
     * @return close items set.
     */
    public static HashSet<ItemStack> getCloseItems() {
        return closeItems;
    }

}
