package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.ItemType;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.PlayerHead;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters.InterfaceItemGetter;
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

public class PlayerQuestsInterface extends InterfaceItemGetter {

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
    private static final Set<Integer> slotsPlayerHead = new HashSet<>();
    private static final HashMap<Integer, List<Integer>> slotQuests = new HashMap<>();

    /* item lists */
    private static final Set<ItemStack> fillItems = new HashSet<>();
    private static final Set<ItemStack> closeItems = new HashSet<>();
    private static final Map<Integer, List<String>> playerCommandsItems = new HashMap<>();
    private static final Map<Integer, List<String>> consoleCommandsItems = new HashMap<>();

    /* items with placeholders */
    private static final Map<ItemStack, Integer> papiItems = new HashMap<>();

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        final ConfigurationSection interfaceConfig = PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface");
        if (interfaceConfig == null) {
            PluginLogger.error("An error occurred when loading the player interface.");
            PluginLogger.error("The playerInterface file is not correctly configured.");
            return;
        }

        initVariables(interfaceConfig);

        final ConfigurationSection questsSection = interfaceConfig.getConfigurationSection("quests");
        if (questsSection == null) {
            PluginLogger.error("An error occurred when loading the player interface.");
            PluginLogger.error("The quests section is not defined in the playerInterface file.");
            return;
        }

        loadQuestsSlots(questsSection);

        final ConfigurationSection itemsSection = interfaceConfig.getConfigurationSection("items");
        if (itemsSection == null) {
            PluginLogger.error("An error occurred when loading the player interface.");
            PluginLogger.error("The items section is not defined in the playerInterface file.");
            return;
        }

        loadItems(itemsSection);

        PluginLogger.fine("Player quests interface successfully loaded.");
    }

    /**
     * Reload player quests interface.
     *
     * @param interfaceConfig configuration section of the interface.
     */
    private void initVariables(ConfigurationSection interfaceConfig) {

        /* clear all lists, in case of reload */
        slotsPlayerHead.clear();
        slotQuests.clear();
        fillItems.clear();
        closeItems.clear();
        playerCommandsItems.clear();
        consoleCommandsItems.clear();
        papiItems.clear();

        /* get inventory name */
        interfaceName = ColorConvert.convertColorCode(interfaceConfig.getString(".inventory_name"));

        /* get booleans */
        isPlayerHeadEnabled = interfaceConfig.getConfigurationSection("player_head").getBoolean(".enabled");
        isGlowingEnabled = interfaceConfig.getBoolean("glowing_if_achieved");

        /* create base of inventory */
        size = interfaceConfig.getInt(".size");
        playerQuestsInventoryBase = Bukkit.createInventory(null, size, "BASIC");

        /* load all texts */
        achieved = interfaceConfig.getString(".achieved");
        status = interfaceConfig.getString(".status");
        progression = interfaceConfig.getString(".progress");
        completeGetType = interfaceConfig.getString(".complete_get_type");

        /* load player head slots */
        if (isPlayerHeadEnabled) {
            final ConfigurationSection section = interfaceConfig.getConfigurationSection("player_head");

            if (section.isList(".slot")) slotsPlayerHead.addAll(section.getIntegerList(".slot"));
            else slotsPlayerHead.add(section.getInt(".slot") - 1);
        }
    }

    /**
     * Load quests slots.
     *
     * @param questsSection configuration section of the quests.
     */
    private void loadQuestsSlots(ConfigurationSection questsSection) {
        for (String index : questsSection.getKeys(false)) {
            int slot = Integer.parseInt(index) - 1;
            if (questsSection.isList(index)) {
                final List<Integer> values = questsSection.getIntegerList(index);
                slotQuests.put(slot, values);
            } else {
                int value = questsSection.getInt(index);
                slotQuests.put(slot, Collections.singletonList(value));
            }
        }
    }

    /**
     * Load items.
     *
     * @param itemsSection configuration section of the items.
     */
    private void loadItems(ConfigurationSection itemsSection) {
        for (String element : itemsSection.getKeys(false)) {

            final ConfigurationSection itemData = itemsSection.getConfigurationSection(element + ".item");
            if (itemData == null) {
                configurationError(element, "item", "The item is not defined.");
                continue;
            }

            /* load item */
            final String material = itemData.getString("material");
            if (material == null) {
                configurationError(element, "material", "The material of the item is not defined.");
                continue;
            }

            ItemStack item;
            if (material.equals("CUSTOM_HEAD")) {
                final String texture = itemData.getString("texture");
                item = Buttons.getCustomHead(texture);

            } else if (material.contains(":")) {
                item = getItem(material, element, "material");

            } else item = new ItemStack(Material.valueOf(material));

            if (item == null) item = new ItemStack(Material.BARRIER);

            /* get slot(s) */
            final List<Integer> slots;
            if (itemData.isList("slot")) {
                slots = itemData.getIntegerList("slot");
            } else {
                slots = List.of(itemData.getInt("slot"));
            }

            /* affect item to slot(s) depending on the type */
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

                    for (int slot : slots) {
                        closeItems.add(item);
                    }
                }

                case PLAYER_COMMAND -> {
                    List<String> commands = itemsSection.getStringList(element + ".commands");
                    item.setItemMeta(getItemMeta(item, itemData));

                    for (int slot : slots) {
                        playerCommandsItems.put(slot - 1, commands);
                    }
                }

                case CONSOLE_COMMAND -> {
                    List<String> commands = itemsSection.getStringList(element + ".commands");
                    item.setItemMeta(getItemMeta(item, itemData));

                    for (int slot : slots) {
                        consoleCommandsItems.put(slot - 1, commands);
                    }
                }
            }

            if (itemsSection.contains(element + ".use_placeholders") && itemsSection.getBoolean(element + ".use_placeholders")) {
                for (int slot : slots) {
                    papiItems.put(item, slot - 1);
                }
            }

            for (int slot : slots) {
                playerQuestsInventoryBase.setItem(slot - 1, item);
            }
        }
    }

    /**
     * Load the ItemMeta of an item.
     *
     * @param itemStack item to load.
     * @param section   section of the item.
     * @return ItemMeta of the item.
     */
    private ItemMeta getItemMeta(ItemStack itemStack, ConfigurationSection section) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        if (section.contains("custom_model_data")) meta.setCustomModelData(section.getInt("custom_model_data"));

        final String name = section.getString("name");
        if (name != null) {
            meta.setDisplayName(ColorConvert.convertColorCode(name));
        }

        final List<String> lore = section.getStringList("lore");
        for (String str : lore) {
            lore.set(lore.indexOf(str), ColorConvert.convertColorCode(str));
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

        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        final Map<AbstractQuest, Progression> playerQuests = activeQuests.get(player.getName()).getPlayerQuests();

        /* load player head */
        if (isPlayerHeadEnabled) {
            final ItemStack playerHead = PlayerHead.getPlayerHead(player);
            for (int slot : slotsPlayerHead) {
                playerQuestsInventoryIndividual.setItem(slot, playerHead);
            }
        }

        /* load quests */
        int i = 0;
        for (AbstractQuest quest : playerQuests.keySet()) {

            ItemStack itemStack;
            if (playerQuests.get(quest).isAchieved()) {
                itemStack = quest.getAchievedItem().clone();
            } else {
                itemStack = quest.getMenuItem().clone();
            }

            final ItemMeta itemMeta = itemStack.getItemMeta().clone();
            itemMeta.setDisplayName(quest.getQuestName());

            final List<String> lore = new ArrayList<>(quest.getQuestDesc());

            if (quest.isUsingPlaceholders()) {
                for (String str : lore) {
                    lore.set(lore.indexOf(str), PAPIHook.getPlaceholders(player, str));
                }

                itemMeta.setDisplayName(PAPIHook.getPlaceholders(player, itemMeta.getDisplayName()));
            }

            lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, status)));

            if (playerQuests.get(quest).isAchieved()) {
                if (isGlowingEnabled) {
                    itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
                }
                lore.add(ColorConvert.convertColorCode(achieved));
            } else {
                if (quest.getQuestType() == QuestType.GET) {
                    lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, completeGetType)
                            .replace("%progress%", String.valueOf(playerQuests.get(quest).getProgression()))
                            .replace("%required%", String.valueOf(quest.getAmountRequired()))
                    ));
                } else {
                    lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, progression)
                            .replace("%progress%", String.valueOf(playerQuests.get(quest).getProgression()))
                            .replace("%required%", String.valueOf(quest.getAmountRequired()))));
                }
            }

            itemMeta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_POTION_EFFECTS,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_DYE
            );

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            if (slotQuests.get(i) != null) {
                for (int slot : slotQuests.get(i)) {
                    playerQuestsInventoryIndividual.setItem(slot - 1, itemStack);
                }
            } else {
                PluginLogger.error("An error occurred when loading the player interface.");
                PluginLogger.error("The slot for the quest number " + (i + 1) + " is not defined in the playerInterface file.");
            }

            i++;
        }
        return playerQuestsInventoryIndividual;
    }

    /**
     * Get the name of the interface.
     *
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
    public static Set<ItemStack> getFillItems() {
        return fillItems;
    }

    /**
     * Get all player command items.
     *
     * @return player command items map.
     */
    public static Map<Integer, List<String>> getPlayerCommandsItems() {
        return playerCommandsItems;
    }

    /**
     * Get all console command items.
     *
     * @return console command items map.
     */
    public static Map<Integer, List<String>> getConsoleCommandsItems() {
        return consoleCommandsItems;
    }

    /**
     * Get all close items.
     *
     * @return close items set.
     */
    public static Set<ItemStack> getCloseItems() {
        return closeItems;
    }

}
