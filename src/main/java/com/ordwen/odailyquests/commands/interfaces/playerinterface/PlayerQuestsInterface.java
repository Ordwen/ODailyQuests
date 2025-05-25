package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.ItemType;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.PlayerHead;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters.InterfaceItemGetter;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import com.ordwen.odailyquests.tools.TimeRemain;
import com.ordwen.odailyquests.tools.BedrockTextureMapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.*;

public class PlayerQuestsInterface extends InterfaceItemGetter {

    /* item slots */
    private static final Set<Integer> slotsPlayerHead = new HashSet<>();
    private static final HashMap<Integer, List<Integer>> slotQuests = new HashMap<>();
    /* item lists */
    private static final Set<ItemStack> fillItems = new HashSet<>();
    private static final Set<ItemStack> closeItems = new HashSet<>();
    private static final Map<Integer, List<String>> playerCommandsItems = new HashMap<>();
    private static final Map<Integer, List<String>> consoleCommandsItems = new HashMap<>();
    /* items with placeholders */
    private static final Map<Integer, ItemStack> papiItems = new HashMap<>();
    /* init variables */
    private static String interfaceName;
    private static String formName;
    private static String formDescription;
    private static Inventory playerQuestsInventoryBase;
    private static int size;
    private static String achieved;
    private static String status;
    private static String progression;
    private static String completeGetType;
    private static boolean isPlayerHeadEnabled;
    private static boolean isGlowingEnabled;
    private static boolean isStatusDisabled;

    /**
     * Get player quests inventory.
     *
     * @return player quests inventory.
     */
    public static Inventory getPlayerQuestsInterface(Player player) {

        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        if (!activeQuests.containsKey(player.getName())) {
            PluginLogger.error("Impossible to find the player " + player.getName() + " in the active quests.");
            PluginLogger.error("It can happen if the player try to open the interface while the server/plugin is reloading.");
            PluginLogger.error("If the problem persist, please contact the developer.");
            return null;
        }

        final PlayerQuests playerQuests = activeQuests.get(player.getName());

        if (QuestLoaderUtils.isTimeToRenew(player, activeQuests)) return getPlayerQuestsInterface(player);

        final Map<AbstractQuest, Progression> questsMap = playerQuests.getPlayerQuests();

        final Inventory playerQuestsInventoryIndividual = Bukkit.createInventory(null, size, PAPIHook.getPlaceholders(player, interfaceName));
        playerQuestsInventoryIndividual.setContents(playerQuestsInventoryBase.getContents());

        if (!papiItems.isEmpty()) {
            for (Integer slot : papiItems.keySet()) {

                if (slot < 0 || slot >= size) {
                    PluginLogger.error("An error occurred when loading the player interface.");
                    PluginLogger.error("A placeholder at slot " + slot + " is out of bounds.");
                    continue;
                }

                final ItemStack itemCopy = papiItems.get(slot).clone();
                final ItemMeta meta = itemCopy.getItemMeta();
                final List<String> lore = meta.getLore();

                meta.setDisplayName(PAPIHook.getPlaceholders(player, meta.getDisplayName()));

                for (String str : lore) {
                    lore.set(lore.indexOf(str), PAPIHook.getPlaceholders(player, str)
                            .replace("%achieved%", String.valueOf(playerQuests.getAchievedQuests()))
                            .replace("%drawIn%", TimeRemain.timeRemain(player.getName())));
                }

                meta.setLore(lore);
                itemCopy.setItemMeta(meta);
                playerQuestsInventoryIndividual.setItem(slot, itemCopy);
            }
        }

        /* load player head */
        if (isPlayerHeadEnabled) {
            final ItemStack playerHead = PlayerHead.getPlayerHead(player);
            for (int slot : slotsPlayerHead) {
                if (slot >= 0 && slot <= size) {
                    playerQuestsInventoryIndividual.setItem(slot, playerHead);
                } else {
                    PluginLogger.error("An error occurred when loading the player interface.");
                    PluginLogger.error("The slot defined for the player head is out of bounds.");
                }
            }
        }

        /* load quests */
        int i = 0;
        for (AbstractQuest quest : questsMap.keySet()) {

            ItemStack itemStack;
            if (questsMap.get(quest).isAchieved()) {
                itemStack = quest.getAchievedItem().clone();
            } else {
                itemStack = quest.getMenuItem().clone();
            }

            final ItemMeta itemMeta = itemStack.getItemMeta().clone();
            itemMeta.setDisplayName(quest.getQuestName());

            final List<String> lore = new ArrayList<>(quest.getQuestDesc());

            if (quest.isUsingPlaceholders()) {
                final Progression progression = questsMap.get(quest);

                for (String str : lore) {
                    lore.set(
                            lore.indexOf(str),
                            PAPIHook.getPlaceholders(player, str)
                                    .replace("%progress%", String.valueOf(progression.getProgression()))
                                    .replace("%progressBar%", ProgressBar.getProgressBar(progression.getProgression(), quest.getAmountRequired()))
                                    .replace("%required%", String.valueOf(quest.getAmountRequired()))
                                    .replace("%achieved%", String.valueOf(playerQuests.getAchievedQuests()))
                                    .replace("%drawIn%", TimeRemain.timeRemain(player.getName()))
                                    .replace("%status%", getQuestStatus(progression, quest, player))
                    );
                }

                itemMeta.setDisplayName(PAPIHook.getPlaceholders(player, itemMeta.getDisplayName()));
            }

            if (!status.isEmpty() && !isStatusDisabled)
                lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, status)));

            if (questsMap.get(quest).isAchieved()) {

                if (isGlowingEnabled) {
                    itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
                }

                if (!achieved.isEmpty() && !isStatusDisabled) {
                    lore.add(ColorConvert.convertColorCode(achieved));
                }
            } else {

                if (quest.getQuestType().equals("GET")) {
                    if (!completeGetType.isEmpty())
                        lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, completeGetType)
                                .replace("%progress%", String.valueOf(questsMap.get(quest).getProgression()))
                                .replace("%required%", String.valueOf(quest.getAmountRequired()))
                                .replace("%progressBar%", ProgressBar.getProgressBar(questsMap.get(quest).getProgression(), quest.getAmountRequired()))
                        ));
                } else {
                    if (!progression.isEmpty() && !isStatusDisabled) {
                        lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, progression)
                                .replace("%progress%", String.valueOf(questsMap.get(quest).getProgression()))
                                .replace("%required%", String.valueOf(quest.getAmountRequired()))
                                .replace("%progressBar%", ProgressBar.getProgressBar(questsMap.get(quest).getProgression(), quest.getAmountRequired()))
                        ));
                    }
                }
            }

            // fix for flags not working on Paper +1.21
            AttributeModifier dummyModifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "dummy",
                    0,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            itemMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, dummyModifier);

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
                    if (slot >= 0 && slot <= size) {
                    playerQuestsInventoryIndividual.setItem(slot - 1, itemStack);
                    } else {
                        PluginLogger.error("An error occurred when loading the player interface.");
                        PluginLogger.error("The slot defined for the quest number " + (i + 1) + " is out of bounds.");
                    }
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
     * Get player quests inventory.
     *
     * @return player quests form.
     */
    public static SimpleForm getPlayerQuestsForm(Player player) {
        SimpleForm.Builder form = SimpleForm.builder();
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        if (!activeQuests.containsKey(player.getName())) {
            PluginLogger.error("Impossible to find the player " + player.getName() + " in the active quests.");
            PluginLogger.error("It can happen if the player try to open the interface while the server/plugin is reloading.");
            PluginLogger.error("If the problem persist, please contact the developer.");
            return null;
        }

        final PlayerQuests playerQuests = activeQuests.get(player.getName());

        if (QuestLoaderUtils.isTimeToRenew(player, activeQuests)) return getPlayerQuestsForm(player);

        final Map<AbstractQuest, Progression> questsMap = playerQuests.getPlayerQuests();

        /* load quests */
        for (AbstractQuest quest : questsMap.keySet()) {
            form.button(quest.getQuestName(), FormImage.Type.PATH, BedrockTextureMapper.getTexturePath(quest.getMenuItem().getType()));
        }

        form.title(formName);
        form.content(formDescription);

        return form.build();
    }

    /**
     * Get the corresponding text for the quest status.
     *
     * @param progression the current progression of the quest.
     * @param quest       the quest.
     * @param player      the player.
     * @return the achieved message or the progress message.
     */
    private static String getQuestStatus(Progression progression, AbstractQuest quest, Player player) {
        if (progression.isAchieved()) {
            return PAPIHook.getPlaceholders(player, getAchieved());
        } else {
            return PAPIHook.getPlaceholders(player, getProgression()
                    .replace("%progress%", String.valueOf(progression.getProgression()))
                    .replace("%required%", String.valueOf(quest.getAmountRequired()))
                    .replace("%progressBar%", ProgressBar.getProgressBar(progression.getProgression(), quest.getAmountRequired()))
            );
        }
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
     * Get the "achieved" message.
     *
     * @return "achieved" message.
     */
    public static String getAchieved() {
        return achieved;
    }

    /**
     * Get the "progression" message.
     *
     * @return "progression" message.
     */
    public static String getProgression() {
        return progression;
    }

    /**
     * Get the "completeGetType" message.
     *
     * @return "completeGetType" message.
     */
    public static String getCompleteGetType() {
        return completeGetType;
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
            PluginLogger.warn("The items section is not defined in the playerInterface file.");
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
        formName = ColorConvert.convertColorCode(interfaceConfig.getString(".bedrock_name"));
        formDescription = ColorConvert.convertColorCode(interfaceConfig.getString(".bedrock_description"));

        /* get booleans */
        isPlayerHeadEnabled = interfaceConfig.getConfigurationSection("player_head").getBoolean(".enabled");
        isGlowingEnabled = interfaceConfig.getBoolean("glowing_if_achieved");
        isStatusDisabled = interfaceConfig.getBoolean("disable_status");

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
                    closeItems.add(item);
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
                    papiItems.put(slot - 1, item);
                }
            }

            for (int slot : slots) {
                if (slot >= 0 && slot <= size) {
                    playerQuestsInventoryBase.setItem(slot - 1, item);
                }
                else {
                    PluginLogger.error("An error occurred when loading the player interface.");
                    PluginLogger.error("The slot defined for the item " + element + " is out of bounds.");
                }
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

}
