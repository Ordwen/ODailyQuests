package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.ItemType;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.PlayerHead;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters.InterfaceItemGetter;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.ItemUtils;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import com.ordwen.odailyquests.tools.TimeRemain;
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

import java.util.*;
import java.util.regex.Pattern;

public class PlayerQuestsInterface extends InterfaceItemGetter {

    private static final String ERROR_OCCURRED = "An error occurred when loading the player interface.";
    private static final String OUT_OF_BOUNDS = " is out of bounds.";

    private static final String PROGRESS = "%progress%";
    private static final String PROGRESS_BAR = "%progressBar%";
    private static final String REQUIRED = "%required%";
    private static final String ACHIEVED = "%achieved%";
    private static final String DRAW_IN = "%drawIn%";
    private static final String STATUS = "%status%";
    private static final String MATERIAL = "material";

    /* instances */
    private final PlayerInterfaceFile playerInterfaceFile;
    private final PlayerHead playerHead;

    /* item slots */
    private final Map<Integer, List<Integer>> slotQuests = new HashMap<>();

    /* item lists */
    private final Set<ItemStack> fillItems = new HashSet<>();
    private final Set<ItemStack> closeItems = new HashSet<>();
    private final Map<Integer, List<String>> playerCommandsItems = new HashMap<>();
    private final Map<Integer, List<String>> consoleCommandsItems = new HashMap<>();

    /* items with placeholders */
    private final Map<Integer, ItemStack> papiItems = new HashMap<>();

    /* init variables */
    private String interfaceName;
    private Inventory playerQuestsInventoryBase;
    private int size;
    private String achievedStr;
    private String statusStr;
    private String progressStr;
    private String completeGetTypeStr;
    private boolean isGlowingEnabled;
    private boolean isStatusDisabled;

    public PlayerQuestsInterface(PlayerInterfaceFile playerInterfaceFile) {
        this.playerInterfaceFile = playerInterfaceFile;
        this.playerHead = new PlayerHead(playerInterfaceFile);
    }

    /**
     * Load player quests interface.
     */
    public void load() {
        final ConfigurationSection section = playerInterfaceFile.getConfig().getConfigurationSection("player_interface");
        if (section == null) {
            PluginLogger.error(ERROR_OCCURRED);
            PluginLogger.error("The playerInterface file is not correctly configured.");
            return;
        }

        loadVariables(section);

        final ConfigurationSection questsSection = section.getConfigurationSection("quests");
        if (questsSection == null) {
            PluginLogger.error(ERROR_OCCURRED);
            PluginLogger.error("The quests section is not defined in the playerInterface file.");
            return;
        }

        loadQuestsSlots(questsSection);

        final ConfigurationSection itemsSection = section.getConfigurationSection("items");
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
    private void loadVariables(ConfigurationSection interfaceConfig) {

        /* clear all lists, in case of reload */
        slotQuests.clear();
        fillItems.clear();
        closeItems.clear();
        playerCommandsItems.clear();
        consoleCommandsItems.clear();
        papiItems.clear();

        /* load player head */
        playerHead.load();

        /* get inventory name */
        interfaceName = TextFormatter.format(interfaceConfig.getString(".inventory_name"));

        /* get booleans */
        isGlowingEnabled = interfaceConfig.getBoolean("glowing_if_achieved");
        isStatusDisabled = interfaceConfig.getBoolean("disable_status");

        /* create base of inventory */
        size = interfaceConfig.getInt(".size");
        playerQuestsInventoryBase = Bukkit.createInventory(null, size, "BASIC");

        /* load all texts */
        achievedStr = interfaceConfig.getString(".achieved");
        statusStr = interfaceConfig.getString(".status");
        progressStr = interfaceConfig.getString(".progress");
        completeGetTypeStr = interfaceConfig.getString(".complete_get_type");
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
            final ConfigurationSection elementSection = itemsSection.getConfigurationSection(element);
            if (elementSection == null) {
                configurationError(element, "item", "The item is not defined.");
                continue;
            }

            final ConfigurationSection itemSection = elementSection.getConfigurationSection("item");
            if (itemSection == null) {
                configurationError(element, "item", "The item is not defined.");
                continue;
            }

            /* load item */
            final String material = itemSection.getString(MATERIAL);
            if (material == null) {
                configurationError(element, MATERIAL, "The material of the item is not defined.");
                continue;
            }

            /* get item */
            final ItemStack item = getItemStack(element, material, itemSection);

            /* get slot(s) */
            final List<Integer> slots = getSlots(itemSection);

            /* load item depending on its type */
            loadItemType(elementSection, item, itemSection, slots);

            /* add item to placeholders list if applicable */
            loadPlaceholderItem(slots, item);

            /* add loaded items into base inventory */
            addIntoBaseInventory(element, slots, item);
        }
    }

    /**
     * Get the slots where the item should be added.
     *
     * @param itemSection configuration section of the item.
     * @return slots where the item should be added.
     */
    private static List<Integer> getSlots(ConfigurationSection itemSection) {
        final List<Integer> slots;
        if (itemSection.isList("slot")) {
            slots = itemSection.getIntegerList("slot");
        } else {
            slots = List.of(itemSection.getInt("slot"));
        }
        return slots;
    }

    /**
     * Get the item stack.
     *
     * @param element     name of the item.
     * @param material    material of the item.
     * @param itemSection configuration section of the item.
     * @return item stack.
     */
    private ItemStack getItemStack(String element, String material, ConfigurationSection itemSection) {
        ItemStack item;
        if (material.equals("CUSTOM_HEAD")) {
            final String texture = itemSection.getString("texture");
            item = ItemUtils.getCustomHead(texture);

        } else if (material.contains(":")) {
            item = getItem(material, element, MATERIAL);

        } else item = new ItemStack(Material.valueOf(material));

        if (item == null) item = new ItemStack(Material.BARRIER);
        return item;
    }

    /**
     * Add the loaded item into the base inventory.
     *
     * @param element name of the item.
     * @param slots   slots where the item should be added.
     * @param item    item to add.
     */
    private void addIntoBaseInventory(String element, List<Integer> slots, ItemStack item) {
        for (int slot : slots) {
            if (slot >= 0 && slot <= size) {
                playerQuestsInventoryBase.setItem(slot - 1, item);
            } else {
                PluginLogger.error(ERROR_OCCURRED);
                PluginLogger.error("The slot defined for the item " + element + OUT_OF_BOUNDS);
            }
        }
    }

    /**
     * Check if the item needs to be loaded with placeholders. If so add it to the placeholders list.
     *
     * @param slots slots where the item should be added.
     * @param item  item to add.
     */
    private void loadPlaceholderItem(List<Integer> slots, ItemStack item) {
        boolean hasPlaceholders = false;

        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();

            if (meta.hasDisplayName() && containsPlaceholder(meta.getDisplayName())) {
                hasPlaceholders = true;
            }

            if (!hasPlaceholders && meta.hasLore()) {
                for (String line : meta.getLore()) {
                    if (containsPlaceholder(line)) {
                        hasPlaceholders = true;
                        break;
                    }
                }
            }
        }

        if (hasPlaceholders) {
            slots.forEach(slot -> papiItems.put(slot - 1, item));
        }
    }

    /**
     * Check if the given text contains placeholders.
     *
     * @param text text to check.
     * @return true if the text contains placeholders, false otherwise.
     */
    private boolean containsPlaceholder(String text) {
        if (text == null) {
            return false;
        }
        final Pattern pattern = Pattern.compile("%\\w+%");
        return pattern.matcher(text).find();
    }

    /**
     * Load the item depending on its type.
     *
     * @param elementSection configuration section of the element.
     * @param item           item to load.
     * @param itemSection    configuration section of the item.
     * @param slots          slots where the item should be added.
     */
    private void loadItemType(ConfigurationSection elementSection, ItemStack item, ConfigurationSection itemSection, List<Integer> slots) {
        final String itemType = elementSection.getString("type");
        switch (ItemType.valueOf(itemType)) {
            case FILL -> {
                final ItemMeta fillItemMeta = item.getItemMeta();
                if (fillItemMeta == null) return;

                fillItemMeta.setDisplayName(ChatColor.RESET + "");
                item.setItemMeta(fillItemMeta);
                fillItems.add(item);
            }

            case CLOSE -> {
                item.setItemMeta(getItemMeta(item, itemSection));
                closeItems.add(item);
            }

            case PLAYER_COMMAND -> {
                final List<String> commands = elementSection.getStringList("commands");
                item.setItemMeta(getItemMeta(item, itemSection));

                for (int slot : slots) {
                    playerCommandsItems.put(slot - 1, commands);
                }
            }

            case CONSOLE_COMMAND -> {
                final List<String> commands = elementSection.getStringList("commands");
                item.setItemMeta(getItemMeta(item, itemSection));

                for (int slot : slots) {
                    consoleCommandsItems.put(slot - 1, commands);
                }
            }
        }
    }

    /**
     * Load the player quests inventory for the given player.
     *
     * @param player player to load the inventory.
     * @return player quests inventory.
     */
    public Inventory getPlayerQuestsInterface(Player player) {

        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        if (!activeQuests.containsKey(player.getName())) {
            PluginLogger.error("Impossible to find the player " + player.getName() + " in the active quests.");
            PluginLogger.error("It can happen if the player try to open the interface while the server/plugin is reloading.");
            PluginLogger.error("If the problem persist, please contact the developer.");
            return null;
        }

        final PlayerQuests playerQuests = activeQuests.get(player.getName());

        if (QuestLoaderUtils.isTimeToRenew(player, activeQuests)) return getPlayerQuestsInterface(player);

        final Map<AbstractQuest, Progression> questsMap = playerQuests.getQuests();

        final Inventory playerQuestsInventoryIndividual = Bukkit.createInventory(null, size, TextFormatter.format(player, interfaceName));
        playerQuestsInventoryIndividual.setContents(playerQuestsInventoryBase.getContents());

        if (!papiItems.isEmpty()) {
            applyPapiItems(player, playerQuests, playerQuestsInventoryIndividual);
        }

        /* load player head */
        playerQuestsInventoryIndividual.setContents(playerHead.setPlayerHead(playerQuestsInventoryIndividual, player, size).getContents());

        /* load quests */
        applyQuestsItems(player, questsMap, playerQuests, playerQuestsInventoryIndividual);

        return playerQuestsInventoryIndividual;
    }

    /**
     * Apply the quests items to the inventory.
     *
     * @param player       player to apply the items.
     * @param questsMap    quests to apply.
     * @param playerQuests player quests.
     * @param inventory    inventory to apply the items.
     */
    private void applyQuestsItems(Player player, Map<AbstractQuest, Progression> questsMap, PlayerQuests playerQuests, Inventory inventory) {
        int i = 0;
        for (Map.Entry<AbstractQuest, Progression> entry : questsMap.entrySet()) {
            final AbstractQuest quest = entry.getKey();
            final Progression playerProgression = entry.getValue();

            final ItemStack itemStack = getQuestItem(quest, playerProgression);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) continue;

            configureItemMeta(itemMeta, quest, playerProgression, player, playerQuests);
            itemStack.setItemMeta(itemMeta);
            placeItemInInventory(i, itemStack, inventory);

            i++;
        }
    }

    /**
     * Get the item of the quest depending on its progression.
     *
     * @param quest             the quest.
     * @param playerProgression the player progression of the quest.
     * @return the item of the quest.
     */
    private ItemStack getQuestItem(AbstractQuest quest, Progression playerProgression) {
        return playerProgression.isAchieved() ? quest.getAchievedItem().clone() : quest.getMenuItem().clone();
    }

    /**
     * Configure the ItemMeta of the item.
     *
     * @param itemMeta          the item meta to configure.
     * @param quest             the quest of the item.
     * @param playerProgression the player progression of the quest.
     * @param player            the player.
     * @param playerQuests      the player quests.
     */
    private void configureItemMeta(ItemMeta itemMeta, AbstractQuest quest, Progression playerProgression, Player player, PlayerQuests playerQuests) {
        final String displayName = TextFormatter.format(player, quest.getQuestName())
                .replace(REQUIRED, String.valueOf(playerProgression.getRequiredAmount()));

        itemMeta.setDisplayName(displayName);

        final List<String> lore = generateLore(quest, playerProgression, player, playerQuests);
        itemMeta.setLore(lore);

        if (playerProgression.isAchieved() && isGlowingEnabled) {
            itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        }

        itemMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,
                new AttributeModifier(UUID.randomUUID(), "dummy", 0, AttributeModifier.Operation.ADD_NUMBER));
        itemMeta.addItemFlags(ItemFlag.values());
    }

    /**
     * Generate the lore of the item.
     *
     * @param quest             the quest of the item.
     * @param playerProgression the player progression of the quest.
     * @param player            the player.
     * @param playerQuests      the player quests.
     * @return the lore of the item.
     */
    private List<String> generateLore(AbstractQuest quest, Progression playerProgression, Player player, PlayerQuests playerQuests) {
        final List<String> lore = new ArrayList<>(quest.getQuestDesc());

        lore.replaceAll(str -> TextFormatter.format(player, str)
                .replace(PROGRESS, String.valueOf(playerProgression.getAdvancement()))
                .replace(PROGRESS_BAR, ProgressBar.getProgressBar(playerProgression.getAdvancement(), playerProgression.getRequiredAmount()))
                .replace(REQUIRED, String.valueOf(playerProgression.getRequiredAmount()))
                .replace(ACHIEVED, String.valueOf(playerQuests.getAchievedQuests()))
                .replace(DRAW_IN, TimeRemain.timeRemain(player.getName()))
                .replace(STATUS, getQuestStatus(playerProgression, player)));

        if (!statusStr.isEmpty() && !isStatusDisabled) {
            lore.add(TextFormatter.format(TextFormatter.format(player, statusStr)));
        }

        if (playerProgression.isAchieved() && !achievedStr.isEmpty() && !isStatusDisabled) {
            lore.add(TextFormatter.format(achievedStr));
        } else if (!progressStr.isEmpty() && !isStatusDisabled) {
            lore.add(TextFormatter.format(TextFormatter.format(player, progressStr)
                    .replace(PROGRESS, String.valueOf(playerProgression.getAdvancement()))
                    .replace(REQUIRED, String.valueOf(playerProgression.getRequiredAmount()))
                    .replace(PROGRESS_BAR, ProgressBar.getProgressBar(playerProgression.getAdvancement(), playerProgression.getRequiredAmount()))));
        }

        return lore;
    }

    /**
     * Depending on the quest index, place the item in the inventory.
     *
     * @param questIndex quest index.
     * @param itemStack  item stack to place.
     * @param inventory  inventory to place the item.
     */
    private void placeItemInInventory(int questIndex, ItemStack itemStack, Inventory inventory) {
        final List<Integer> slots = slotQuests.get(questIndex);
        if (slots == null) {
            PluginLogger.error(ERROR_OCCURRED + " Slot not defined for quest " + (questIndex + 1));
            return;
        }
        for (int slot : slots) {
            if (slot >= 0 && slot <= size) {
                inventory.setItem(slot - 1, itemStack);
            } else {
                PluginLogger.error(ERROR_OCCURRED + " Slot " + slot + " for quest " + (questIndex + 1) + OUT_OF_BOUNDS);
            }
        }
    }

    /**
     * Apply placeholders to items.
     *
     * @param player       player to apply placeholders.
     * @param playerQuests player quests.
     * @param inventory    player quests inventory.
     */
    private void applyPapiItems(Player player, PlayerQuests playerQuests, Inventory inventory) {
        for (Map.Entry<Integer, ItemStack> entry : papiItems.entrySet()) {
            final Integer slot = entry.getKey();
            final ItemStack itemCopy = entry.getValue().clone();

            if (slot < 0 || slot >= size) {
                PluginLogger.error(ERROR_OCCURRED);
                PluginLogger.error("A placeholder at slot " + slot + OUT_OF_BOUNDS);
                continue;
            }

            final ItemMeta meta = itemCopy.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(TextFormatter.format(player, meta.getDisplayName()));

                final List<String> lore = meta.getLore();
                if (lore != null) {
                    for (String str : lore) {
                        lore.set(lore.indexOf(str), TextFormatter.format(player, str)
                                .replace(ACHIEVED, String.valueOf(playerQuests.getAchievedQuests()))
                                .replace(DRAW_IN, TimeRemain.timeRemain(player.getName())));
                    }
                }

                meta.setLore(lore);
                itemCopy.setItemMeta(meta);
                inventory.setItem(slot, itemCopy);
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
            meta.setDisplayName(TextFormatter.format(name));
        }

        final List<String> lore = section.getStringList("lore");
        for (String str : lore) {
            lore.set(lore.indexOf(str), TextFormatter.format(str));
        }
        meta.setLore(lore);

        return meta;
    }

    /**
     * Get the corresponding text for the quest status.
     *
     * @param progression the current progression of the quest.
     * @param player      the player.
     * @return the achieved message or the progress message.
     */
    private String getQuestStatus(Progression progression, Player player) {
        if (progression.isAchieved()) {
            return TextFormatter.format(player, getAchievedStr());
        } else {
            return TextFormatter.format(player, getProgressStr()
                    .replace(PROGRESS, String.valueOf(progression.getAdvancement()))
                    .replace(REQUIRED, String.valueOf(progression.getRequiredAmount()))
                    .replace(PROGRESS_BAR, ProgressBar.getProgressBar(progression.getAdvancement(), progression.getRequiredAmount())));
        }
    }

    /**
     * Get the corresponding text for the interface name.
     *
     * @param player player to get the interface name.
     * @return the interface name.
     */
    public String getInterfaceName(Player player) {
        return TextFormatter.format(player, interfaceName);
    }

    /**
     * Check if the item is used to fill the inventory.
     *
     * @param itemStack item to check.
     * @return true if the item is used to fill the inventory, false otherwise.
     */
    public boolean isFillItem(ItemStack itemStack) {
        return fillItems.contains(itemStack);
    }

    /**
     * Check if the item is used to close the inventory.
     *
     * @param itemStack item to check.
     * @return true if the item is used to close the inventory, false otherwise.
     */
    public boolean isCloseItem(ItemStack itemStack) {
        return closeItems.contains(itemStack);
    }

    /**
     * Check if the item is used to execute a command as a player.
     *
     * @param slot slot of the item.
     * @return true if the item is used to execute a player command, false otherwise.
     */
    public boolean isPlayerCommandItem(int slot) {
        return playerCommandsItems.containsKey(slot);
    }

    /**
     * Check if the item is used to execute a command as a console.
     *
     * @param slot slot of the item.
     * @return true if the item is used to execute a console command, false otherwise.
     */
    public boolean isConsoleCommandItem(int slot) {
        return consoleCommandsItems.containsKey(slot);
    }

    /**
     * Get all player commands that can be executed by the item in the given slot.
     *
     * @param slot slot of the item.
     * @return the player commands of the item.
     */
    public List<String> getPlayerCommands(int slot) {
        return playerCommandsItems.get(slot);
    }

    /**
     * Get all console commands that can be executed by the item in the given slot.
     *
     * @param slot slot of the item.
     * @return the console commands of the item.
     */
    public List<String> getConsoleCommands(int slot) {
        return consoleCommandsItems.get(slot);
    }

    /**
     * Get the achieved string defined in the configuration.
     *
     * @return the achieved string.
     */
    public String getAchievedStr() {
        return achievedStr;
    }

    /**
     * Get the status string defined in the configuration.
     *
     * @return the status string.
     */
    public String getProgressStr() {
        return progressStr;
    }

    /**
     * Get the complete get type string defined in the configuration.
     *
     * @return the complete get type string.
     */
    public String getCompleteGetTypeStr() {
        return completeGetTypeStr;
    }
}
