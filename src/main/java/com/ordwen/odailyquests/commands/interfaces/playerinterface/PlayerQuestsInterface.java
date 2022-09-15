package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.events.listeners.inventory.types.AbstractQuest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.AddDefault;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerQuestsInterface {

    /* init variables */
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
    private static HashMap<Integer, Integer> slotQuests = new HashMap<>();

    /* item lists */
    private static HashSet<ItemStack> fillItems;
    private static HashSet<ItemStack> closeItems;
    private static HashMap<ItemStack, List<String>> playerCommandsItems;
    private static HashMap<ItemStack, List<String>> consoleCommandsItems;

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        ConfigurationSection interfaceConfig = PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface");
        isPlayerHeadEnabled = interfaceConfig.getConfigurationSection("player_head").getBoolean(".enabled");

        // glowing_if_achieved - VER 1.3.2
        if (interfaceConfig.contains("glowing_if_achieved")) {
            isGlowingEnabled = interfaceConfig.getBoolean("glowing_if_achieved");
        } else
            AddDefault.addDefaultConfigItem("player_interface.glowing_if_achieved", true, PlayerInterfaceFile.getPlayerInterfaceFileConfiguration(), PlayerInterfaceFile.getPlayerInterfaceFile());

        /* load item slots */
        if (isPlayerHeadEnabled) {
            slotPlayerHead = interfaceConfig.getConfigurationSection("player_head").getInt(".slot") - 1;
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

        ConfigurationSection itemsSection = interfaceConfig.getConfigurationSection("quests");

        for (String i : itemsSection.getKeys(false)) {
            slotQuests.put(Integer.parseInt(i) - 1, itemsSection.getInt(i));
        }

        itemsSection = interfaceConfig.getConfigurationSection("items");

        if (itemsSection != null) {
            for (String element : itemsSection.getKeys(false)) {
                switch (ItemType.valueOf(itemsSection.getString(element + ".type"))) {
                    case FILL -> {
                        ItemStack fillItem = new ItemStack(Material.valueOf(itemsSection.getString(element + ".item.material")));
                        ItemMeta fillItemMeta = fillItem.getItemMeta();
                        fillItemMeta.setDisplayName(ChatColor.RESET + "");
                        fillItem.setItemMeta(fillItemMeta);
                        playerQuestsInventoryBase.setItem(itemsSection.getInt(element + ".item.slot") - 1, fillItem);
                        fillItems.add(fillItem);
                    }
                    case CLOSE -> {
                        ItemStack closeItem = new ItemStack(Material.valueOf(itemsSection.getString(element + ".item.material")));

                        ItemMeta meta = closeItem.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(itemsSection.getString(element + ".item.name"))));

                        List<String> lore = itemsSection.getStringList(element + ".item.lore");
                        for (String str : lore) {
                            lore.set(lore.indexOf(str), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(str)));
                        }
                        meta.setLore(lore);
                        closeItem.setItemMeta(meta);

                        playerQuestsInventoryBase.setItem(itemsSection.getInt(element + ".item.slot") - 1, closeItem);
                        closeItems.add(closeItem);
                    }
                    case PLAYER_COMMAND -> {
                        ItemStack commandItem = new ItemStack(Material.valueOf(itemsSection.getString(element + ".item.material")));
                        List<String> commands = itemsSection.getStringList(element + ".commands");

                        ItemMeta meta = commandItem.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(itemsSection.getString(element + ".item.name"))));

                        List<String> lore = itemsSection.getStringList(element + ".item.lore");
                        for (String str : lore) {
                            lore.set(lore.indexOf(str), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(str)));
                        }
                        meta.setLore(lore);

                        commandItem.setItemMeta(meta);

                        playerQuestsInventoryBase.setItem(itemsSection.getInt(element + ".item.slot") - 1, commandItem);
                        playerCommandsItems.put(commandItem, commands);
                    }
                    case CONSOLE_COMMAND -> {
                        ItemStack commandItem = new ItemStack(Material.valueOf(itemsSection.getString(element + ".item.material")));
                        List<String> commands = itemsSection.getStringList(element + ".commands");

                        ItemMeta meta = commandItem.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(itemsSection.getString(element + ".item.name"))));

                        List<String> lore = itemsSection.getStringList(element + ".item.lore");
                        for (String str : lore) {
                            lore.set(lore.indexOf(str), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(str)));
                        }
                        meta.setLore(lore);

                        commandItem.setItemMeta(meta);

                        playerQuestsInventoryBase.setItem(itemsSection.getInt(element + ".item.slot") - 1, commandItem);
                        consoleCommandsItems.put(commandItem, commands);
                    }
                    default -> {
                        PluginLogger.error("An error occurred when loading the player interface.");
                        PluginLogger.error("Unexpected item type : " + itemsSection.getString(element + ".type"));
                        PluginLogger.error("At index : " + element);
                    }
                }
            }
        }

        PluginLogger.info(ChatColor.GREEN + "Player quests interface successfully loaded.");
    }

    /**
     * Get player quests inventory.
     *
     * @return player quests inventory.
     */
    public static Inventory getPlayerQuestsInterface(String playerName) {

        Inventory playerQuestsInventoryIndividual = Bukkit.createInventory(null, size, InterfacesManager.getPlayerQuestsInventoryName());
        playerQuestsInventoryIndividual.setContents(playerQuestsInventoryBase.getContents());

        HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        HashMap<AbstractQuest, Progression> playerQuests = activeQuests.get(playerName).getPlayerQuests();

        /* load player head */
        if (isPlayerHeadEnabled) {
            playerQuestsInventoryIndividual.setItem(slotPlayerHead, PlayerHead.getPlayerHead(Bukkit.getPlayer(playerName)));
        }

        /* load quests */
        int i = 0;
        for (AbstractQuest quest : playerQuests.keySet()) {
            ItemStack itemStack = quest.getMenuItem();
            ItemMeta itemMeta = itemStack.getItemMeta();

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

            playerQuestsInventoryIndividual.setItem(slotQuests.get(i) - 1, itemStack);

            i++;
        }
        return playerQuestsInventoryIndividual;
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
    public static HashMap<ItemStack, List<String>> getPlayerCommandsItems() {
        return playerCommandsItems;
    }

    /**
     * Get all console command items.
     *
     * @return console command items map.
     */
    public static HashMap<ItemStack, List<String>> getConsoleCommandsItems() {
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
