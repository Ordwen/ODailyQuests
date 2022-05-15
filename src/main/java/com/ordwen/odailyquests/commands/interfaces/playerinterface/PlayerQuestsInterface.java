package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.Quest;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerQuestsInterface {

    /**
     * Getting instance of classes.
     */
    private static PlayerInterfaceFile playerInterfaceFile;

    /**
     * Class instance constructor.
     *
     * @param playerInterfaceFile interfaceConfiguration files class.
     */
    public PlayerQuestsInterface(PlayerInterfaceFile playerInterfaceFile) {
        PlayerQuestsInterface.playerInterfaceFile = playerInterfaceFile;
    }

    /* init variables */
    private static Inventory playerQuestsInventoryBase;
    private static int size;
    private static String achieved;
    private static String status;
    private static String progression;
    private static String completeGetType;
    private static boolean isPlayerHeadEnabled;

    /* item slots */
    private static int slotPlayerHead = -1;
    private static int slotFirstQuest;
    private static int slotSecondQuest;
    private static int slotThirdQuest;

    /* item lists */
    private static List<ItemStack> fillItems;
    private static HashMap<ItemStack, List<String>> playerCommandsItems;
    private static HashMap<ItemStack, List<String>> consoleCommandsItems;

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        ConfigurationSection interfaceConfig = playerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface");
        isPlayerHeadEnabled = interfaceConfig.getConfigurationSection("player_head").getBoolean(".enabled");

        /* load item slots */
        if (isPlayerHeadEnabled) {
            slotPlayerHead = interfaceConfig.getConfigurationSection("player_head").getInt(".slot") - 1;
        }
        slotFirstQuest = interfaceConfig.getInt(".first_quest_slot") - 1;
        slotSecondQuest = interfaceConfig.getInt(".second_quest_slot") - 1;
        slotThirdQuest = interfaceConfig.getInt(".third_quest_slot") - 1;

        /* create base of inventory */
        size = interfaceConfig.getInt(".size");
        playerQuestsInventoryBase = Bukkit.createInventory(null, size, "BASIC");

        /* load all texts */
        achieved = interfaceConfig.getString(".achieved");
        status = interfaceConfig.getString(".status");
        progression = interfaceConfig.getString(".progress");
        completeGetType = interfaceConfig.getString(".complete_get_type");

        /* load all items */
        fillItems = new ArrayList<>();
        playerCommandsItems = new HashMap<>();
        consoleCommandsItems = new HashMap<>();

        ConfigurationSection itemsSection =  interfaceConfig.getConfigurationSection("items");

        for (String element : itemsSection.getKeys(false)) {
            switch(ItemType.valueOf(itemsSection.getString(element + ".type"))) {
                case FILL -> {
                    ItemStack fillItem = new ItemStack(Material.valueOf(itemsSection.getString(element + ".item.material")));
                    playerQuestsInventoryBase.setItem(itemsSection.getInt(element + ".item.slot") - 1, fillItem);
                    fillItems.add(fillItem);
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
        HashMap<Quest, Progression> playerQuests = activeQuests.get(playerName).getPlayerQuests();

        /* load player head */
        if (isPlayerHeadEnabled) {
            playerQuestsInventoryIndividual.setItem(slotPlayerHead, Items.getPlayerHead(Bukkit.getPlayer(playerName)));
        }

        /* load quests */
        int i = 0;
        for (Quest quest : playerQuests.keySet()) {

            ItemStack itemStack = quest.getMenuItem();
            ItemMeta itemMeta = itemStack.getItemMeta();

            assert itemMeta != null;
            itemMeta.setDisplayName(quest.getQuestName());

            List<String> lore = new ArrayList<>(quest.getQuestDesc());
            lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(status)));

            if (playerQuests.get(quest).isAchieved()) {
                itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            switch (i) {
                case 0 -> playerQuestsInventoryIndividual.setItem(slotFirstQuest, itemStack);
                case 1 -> playerQuestsInventoryIndividual.setItem(slotSecondQuest, itemStack);
                case 2 -> playerQuestsInventoryIndividual.setItem(slotThirdQuest, itemStack);
                default -> {
                    PluginLogger.error("Unexpected value on the load of the player quests interface.");
                    PluginLogger.error("Invalid index for quest place : " + i);
                    PluginLogger.error("Please inform the developer.");
                }
            }
            i++;
        }
        return playerQuestsInventoryIndividual;
    }

    /**
     * Get all fill items.
     * @return fill items list.
     */
    public static List<ItemStack> getFillItems() {
        return fillItems;
    }

    /**
     * Get all player command items.
     * @return player command items map.
     */
    public static HashMap<ItemStack, List<String>> getPlayerCommandsItems() {
        return playerCommandsItems;
    }

    /**
     * Get all console command items.
     * @return console command items map.
     */
    public static HashMap<ItemStack, List<String>> getConsoleCommandsItems() {
        return consoleCommandsItems;
    }
}
