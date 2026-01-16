package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.ItemType;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.PlayerHead;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters.InterfaceItemGetter;
import com.ordwen.odailyquests.configuration.functionalities.CompleteOnlyOnClick;
import com.ordwen.odailyquests.files.implementations.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.*;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Handles the loading, preparation and dynamic rendering of the player quests interface.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Parsing the player interface configuration file</li>
 *     <li>Loading static items (fillers, buttons, command triggers...)</li>
 *     <li>Assigning quests to inventory slots</li>
 *     <li>Replacing placeholders dynamically for each player</li>
 *     <li>Applying custom model data and item model identifiers</li>
 *     <li>Building the final inventory instance for a specific player</li>
 *     <li>Executing associated commands when specific items are clicked</li>
 * </ul>
 * <p>
 * The interface supports:
 * <ul>
 *     <li>Custom textures (heads)</li>
 *     <li>Vanilla materials</li>
 *     <li>Namespaced items (ItemsAdder/Oraxen/etc.)</li>
 *     <li>PAPI placeholders in name and lore</li>
 *     <li>Quest progression placeholders: %progress%, %required%, %progressBar%...</li>
 * </ul>
 *
 * <strong>Important:</strong>
 * Static items are stored in {@code playerQuestsInventoryBase}, while dynamic quest items
 * and PAPI-dependent items are merged into a fresh cloned inventory for each player.
 */
public class PlayerQuestsInterface extends InterfaceItemGetter {

    private static final String ERROR_OCCURRED = "An error occurred when loading the player interface. ";
    private static final String OUT_OF_BOUNDS = " is out of bounds (slots must be between 1 and defined size).";

    private static final String MATERIAL = "material";

    /* instances */
    private final PlayerInterfaceFile playerInterfaceFile;
    private final PlayerHead playerHead;

    /* item slots */
    private final Map<Integer, List<Integer>> slotQuests = new HashMap<>();
    private final Map<String, List<Integer>> categorySlots = new HashMap<>();

    /* item lists */
    private final Set<ItemStack> fillItems = new HashSet<>();
    private final Set<ItemStack> closeItems = new HashSet<>();
    private final Map<Integer, List<String>> playerCommandsItems = new HashMap<>();
    private final Map<Integer, List<String>> consoleCommandsItems = new HashMap<>();

    /* items with placeholders */
    private final Map<Integer, ItemStack> papiItems = new HashMap<>();

    /* items behavior */
    private final Set<Integer> closeOnClickSlots = new HashSet<>();

    /* init variables */
    private String interfaceName;
    private Inventory playerQuestsInventoryBase;
    private int size;
    private String achievedStr;
    private String notAchievedStr;
    private String statusStr;
    private String progressStr;
    private String completeGetTypeStr;
    private boolean isGlowingEnabled;
    private boolean isStatusDisabled;

    /**
     * Creates a new player quests interface loader.
     *
     * @param playerInterfaceFile the configuration file defining layout, items and settings
     */
    public PlayerQuestsInterface(PlayerInterfaceFile playerInterfaceFile) {
        this.playerInterfaceFile = playerInterfaceFile;
        this.playerHead = new PlayerHead(playerInterfaceFile);
    }

    /**
     * Loads and parses the entire player interface definition.
     * <p>
     * This method:
     * <ul>
     *     <li>Reads global variables</li>
     *     <li>Loads the mapping between quests and interface slots</li>
     *     <li>Loads static items (fillers, buttons, command items...)</li>
     *     <li>Handles item models, flags, names and lore</li>
     * </ul>
     * <p>
     * If misconfigured sections are found, appropriate errors are logged
     * and the interface will be partially or fully disabled.
     */
    public void load() {
        final ConfigurationSection section = playerInterfaceFile.getConfig().getConfigurationSection("player_interface");
        if (section == null) {
            PluginLogger.error(ERROR_OCCURRED + "The playerInterface file is not correctly configured.");
            return;
        }

        loadVariables(section);

        final ConfigurationSection questsSection = section.getConfigurationSection("quests");
        if (questsSection == null) {
            PluginLogger.error(ERROR_OCCURRED + "The quests section is not defined in the playerInterface file.");
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
     * Builds and returns the complete, fully-rendered quests inventory for a specific player.
     * <p>
     * The returned inventory:
     * <ul>
     *     <li>Starts from the static base inventory</li>
     *     <li>Applies dynamic placeholders on PAPI-enabled items</li>
     *     <li>Injects the player's head (if configured)</li>
     *     <li>Places quest items depending on progression</li>
     * </ul>
     * <p>
     * If the player has no loaded quests (e.g., reload during session), errors are logged.
     *
     * @param player the player for whom the inventory is generated
     * @return the final rendered inventory, or {@code null} if generation failed
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
     * Loads global interface-level variables:
     * <ul>
     *     <li>Inventory name</li>
     *     <li>Size</li>
     *     <li>Text components</li>
     *     <li>Flags (glowing_if_achieved, disable_status)</li>
     * </ul>
     * <p>
     * Also resets internal caches to support full hot reload.
     *
     * @param interfaceConfig the "player_interface" section of the configuration
     */
    private void loadVariables(ConfigurationSection interfaceConfig) {

        /* clear all lists, in case of reload */
        slotQuests.clear();
        categorySlots.clear();
        fillItems.clear();
        closeItems.clear();
        playerCommandsItems.clear();
        consoleCommandsItems.clear();
        papiItems.clear();
        closeOnClickSlots.clear();

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
        notAchievedStr = interfaceConfig.getString(".not_achieved");
        statusStr = interfaceConfig.getString(".status");
        progressStr = interfaceConfig.getString(".progress");
        completeGetTypeStr = interfaceConfig.getString(".complete_get_type");
    }

    /**
     * Reads the "quests:" section and maps each slot index
     * to the quest indices that should appear in it.
     * <p>
     * Supports:
     * <ul>
     *     <li>Single slot → single quest index</li>
     *     <li>Single slot → list of quest indices</li>
     * </ul>
     *
     * @param questsSection the configuration section defining slot → quest mapping
     */
    private void loadQuestsSlots(ConfigurationSection questsSection) {
        final ConfigurationSection categoriesSection = questsSection.getConfigurationSection("categories");
        if (categoriesSection != null) {
            loadCategorySlots(categoriesSection);
        }

        for (String index : questsSection.getKeys(false)) {
            if (index.equalsIgnoreCase("categories")) {
                continue;
            }

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
     * Loads slot positions grouped by quest category.
     *
     * @param categoriesSection configuration section containing category → slots mapping.
     */
    private void loadCategorySlots(ConfigurationSection categoriesSection) {
        for (String category : categoriesSection.getKeys(false)) {
            final List<Integer> slots = categoriesSection.getIntegerList(category);
            if (slots.isEmpty()) {
                PluginLogger.error(ERROR_OCCURRED + "No slots defined for category " + category + ".");
                continue;
            }

            categorySlots.put(category.toLowerCase(Locale.ROOT), slots);
        }
    }

    /**
     * Parses and loads every static item defined in the "items:" section.
     * <p>
     * For each item:
     * <ul>
     *     <li>Material detection (vanilla, namespaced, custom head)</li>
     *     <li>Meta loading (name, lore, CMD, custom_model_data, item_model)</li>
     *     <li>Flags parsing</li>
     *     <li>Type-specific behavior (FILL, CLOSE, COMMAND...)</li>
     *     <li>Placeholder detection for dynamic updates</li>
     * </ul>
     * <p>
     * Errors in configuration disable that specific item.
     *
     * @param itemsSection the "items" config section
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

            /* parse and validate item flags */
            final List<ItemFlag> flags = getItemFlags(element, itemSection);
            if (flags == null) {
                continue;
            }

            /* load item depending on its type */
            loadItemType(elementSection, item, itemSection, slots, flags);

            /* add item to placeholders list if applicable */
            loadPlaceholderItem(slots, item);

            /* add loaded items into base inventory */
            addIntoBaseInventory(element, slots, item);

            /* close on click (optional) */
            loadCloseOnClickItems(elementSection, slots);
        }
    }

    private void loadCloseOnClickItems(ConfigurationSection elementSection, List<Integer> slots) {
        /* close on click (optional) */
        if (elementSection.getBoolean("close_on_click", false)) {
            for (int slot : slots) {
                if (slot > 0 && slot <= size) {
                    closeOnClickSlots.add(slot - 1); // internal is 0-based
                }
            }
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
     * Parse and validate item flags from configuration.
     *
     * @param element     name of the item element (for logging).
     * @param itemSection configuration section of the item.
     * @return list of ItemFlag, or null if an invalid flag has been found (error logged).
     */
    private @Nullable List<ItemFlag> getItemFlags(String element, ConfigurationSection itemSection) {
        if (!itemSection.isList("flags")) {
            return Collections.emptyList();
        }
        final List<String> rawFlags = itemSection.getStringList("flags");
        final List<ItemFlag> flags = new ArrayList<>(rawFlags.size());

        for (String f : rawFlags) {
            if (f == null) continue;
            final String normalized = f.trim().toUpperCase();
            try {
                flags.add(ItemFlag.valueOf(normalized));
            } catch (IllegalArgumentException ex) {
                configurationError(element, "item.flags", normalized + " is not a valid ItemFlag.");
                return null;
            }
        }
        return flags;
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
            if (slot > 0 && slot <= size) {
                playerQuestsInventoryBase.setItem(slot - 1, item);
            } else {
                PluginLogger.error(ERROR_OCCURRED + "The slot defined for the item " + element + OUT_OF_BOUNDS);
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
     * Checks if a string contains any placeholder of the form %xxx%.
     *
     * @param text the input string
     * @return true if a placeholder is detected
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
     * @param flags          item flags to apply to the item.
     */
    private void loadItemType(ConfigurationSection elementSection, ItemStack item, ConfigurationSection itemSection, List<Integer> slots, List<ItemFlag> flags) {
        final String itemTypeRaw = elementSection.getString("type");
        if (itemTypeRaw == null) {
            configurationError(elementSection.getName(), "type", "The item type is not defined.");
            return;
        }

        final ItemType itemType;
        try {
            itemType = ItemType.valueOf(itemTypeRaw);
        } catch (IllegalArgumentException ex) {
            configurationError(elementSection.getName(), "type", itemTypeRaw + " is not a valid ItemType.");
            return;
        }

        final ItemMeta baseMeta = getItemMeta(item, itemSection, flags);

        switch (itemType) {
            case FILL -> {
                if (baseMeta == null) return;
                baseMeta.setDisplayName(ChatColor.RESET + "");
                item.setItemMeta(baseMeta);
                fillItems.add(item);
            }
            case CLOSE -> {
                if (baseMeta == null) return;
                item.setItemMeta(baseMeta);
                closeItems.add(item);
            }
            case PLAYER_COMMAND -> {
                if (baseMeta == null) return;
                final List<String> commands = elementSection.getStringList("commands");
                item.setItemMeta(baseMeta);

                for (int slot : slots) {
                    playerCommandsItems.put(slot - 1, commands);
                }
            }
            case CONSOLE_COMMAND -> {
                if (baseMeta == null) return;
                final List<String> commands = elementSection.getStringList("commands");
                item.setItemMeta(baseMeta);

                for (int slot : slots) {
                    consoleCommandsItems.put(slot - 1, commands);
                }
            }
        }
    }

    /**
     * Places each quest item (menu or achieved version)
     * into its configured slots, and applies:
     * <ul>
     *     <li>Dynamic display name</li>
     *     <li>Dynamic lore</li>
     *     <li>Progression and requirement placeholders</li>
     *     <li>Glowing effect for achieved quests</li>
     * </ul>
     *
     * @param player       target player
     * @param questsMap    the player's quests with their progression
     * @param playerQuests the player's quest container
     * @param inventory    the target inventory
     */
    private void applyQuestsItems(Player player, Map<AbstractQuest, Progression> questsMap, PlayerQuests playerQuests, Inventory inventory) {
        int i = 0;
        final Map<String, Integer> categoryUsage = new HashMap<>();
        for (Map.Entry<AbstractQuest, Progression> entry : questsMap.entrySet()) {
            final AbstractQuest quest = entry.getKey();
            final Progression playerProgression = entry.getValue();
            final ItemStack itemStack = getQuestItem(quest, playerProgression);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) continue;

            configureItemMeta(itemMeta, quest, playerProgression, player, playerQuests);
            itemStack.setItemMeta(itemMeta);

            final int menuItemAmount = quest.getMenuItemAmount();
            if (menuItemAmount == 0) {
                itemStack.setAmount(playerProgression.getRequiredAmount());
            } else {
                itemStack.setAmount(menuItemAmount);
            }

            final List<Integer> slots = resolveSlotsForQuest(quest.getCategoryName(), i, categoryUsage);
            placeItemInInventory(i, slots, itemStack, inventory);

            i++;
        }
    }

    /**
     * Selects the correct menu item depending on quest progression.
     * The returned stack is always cloned to avoid metadata leaks.
     *
     * @param quest             the quest definition
     * @param playerProgression the player's progress
     * @return the item stack to display
     */
    private ItemStack getQuestItem(AbstractQuest quest, Progression playerProgression) {
        return playerProgression.isAchieved() ? quest.getAchievedItem().clone() : quest.getMenuItem().clone();
    }

    /**
     * Applies dynamic values to the item:
     * <ul>
     *     <li>Formatted name</li>
     *     <li>Lore with placeholders replaced</li>
     *     <li>Glowing enchant if achieved</li>
     *     <li>Hidden attributes</li>
     * </ul>
     *
     * @param itemMeta     item meta to update
     * @param quest        the quest
     * @param progression  the player's progression on that quest
     * @param player       the player
     * @param playerQuests the quest container (for %achieved% etc.)
     */
    private void configureItemMeta(ItemMeta itemMeta, AbstractQuest quest, Progression progression, Player player, PlayerQuests playerQuests) {
        String displayName = TextFormatter.format(player, quest.getQuestName());
        displayName = QuestPlaceholders.replaceQuestPlaceholders(displayName, player, quest, progression, playerQuests, null);
        itemMeta.setDisplayName(displayName);

        final List<String> lore = generateLore(quest, progression, player, playerQuests);
        itemMeta.setLore(lore);

        if (progression.isAchieved() && isGlowingEnabled) {
            itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        }

        itemMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "dummy", 0, AttributeModifier.Operation.ADD_NUMBER));
        itemMeta.addItemFlags(ItemFlag.values());
    }

    /**
     * Generates the lore for a quest item, inserting:
     * <ul>
     *     <li>%progress%</li>
     *     <li>%required%</li>
     *     <li>%progressBar%</li>
     *     <li>%status%</li>
     *     <li>%drawIn%</li>
     *     <li>%achieved%</li>
     *     <li>Manual completion hints when enabled</li>
     * </ul>
     *
     * @return the updated lore list
     */
    private List<String> generateLore(AbstractQuest quest, Progression playerProgression, Player player, PlayerQuests playerQuests) {
        final List<String> lore = new ArrayList<>(quest.getQuestDesc());

        final String status = getQuestStatus(playerProgression, player);

        final ListIterator<String> it = lore.listIterator();
        while (it.hasNext()) {
            final String str = it.next();
            String formatted = QuestPlaceholders.replaceQuestPlaceholders(str, player, quest, playerProgression, playerQuests, status);
            formatted = TextFormatter.format(player, formatted);
            it.set(formatted);
        }

        if (!statusStr.isEmpty() && !isStatusDisabled) {
            lore.add(TextFormatter.format(TextFormatter.format(player, statusStr)));
        }

        if (playerProgression.isAchieved() && !achievedStr.isEmpty() && !isStatusDisabled) {
            lore.add(TextFormatter.format(achievedStr));
        } else if (!progressStr.isEmpty() && !isStatusDisabled) {
            final String formattedProgress = TextFormatter.format(player, progressStr);
            lore.add(TextFormatter.format(QuestPlaceholders.replaceQuestPlaceholders(formattedProgress, player, quest, playerProgression, playerQuests, status)));
        }

        if (shouldDisplayManualCompletionHint(playerProgression)) {
            final String hint = getCompleteGetTypeStr();
            if (hint != null && !hint.isEmpty()) {
                lore.add(TextFormatter.format(player, hint));
            }
        }

        return lore;
    }

    /**
     * Depending on the quest index, place the item in the inventory.
     *
     * @param questIndex quest index.
     * @param slots      slots where the item should be placed.
     * @param itemStack  item stack to place.
     * @param inventory  inventory to place the item.
     */
    private void placeItemInInventory(int questIndex, List<Integer> slots, ItemStack itemStack, Inventory inventory) {
        if (slots == null) {
            PluginLogger.error(ERROR_OCCURRED + "Slot not defined for quest " + (questIndex + 1));
            return;
        }
        for (int slot : slots) {
            if (slot > 0 && slot <= size) {
                inventory.setItem(slot - 1, itemStack);
            } else {
                PluginLogger.error(ERROR_OCCURRED + "Slot " + slot + " for quest " + (questIndex + 1) + OUT_OF_BOUNDS);
            }
        }
    }

    /**
     * Resolves the slot(s) where a quest should be displayed.
     * <p>
     * If category-based slots are configured, the next available slot for the quest's category is returned.
     * Otherwise, the legacy quest-index mapping is used.
     *
     * @param categoryName   category of the quest.
     * @param questIndex     index of the quest in the player's list.
     * @param categoryUsage  tracker storing how many slots are already consumed per category for this inventory.
     * @return list of slot indices (1-based), or {@code null} if no slot is configured.
     */
    private @Nullable List<Integer> resolveSlotsForQuest(String categoryName, int questIndex, Map<String, Integer> categoryUsage) {
        if (!categorySlots.isEmpty()) {
            final String key = categoryName.toLowerCase(Locale.ROOT);
            final List<Integer> slots = categorySlots.get(key);

            if (slots == null || slots.isEmpty()) {
                PluginLogger.error(ERROR_OCCURRED + "Slot not defined for category " + categoryName + ".");
                return null;
            }

            final int usage = categoryUsage.getOrDefault(key, 0);
            if (usage >= slots.size()) {
                PluginLogger.error(ERROR_OCCURRED + "Not enough slots configured for category " + categoryName + ".");
                return null;
            }

            categoryUsage.put(key, usage + 1);
            return Collections.singletonList(slots.get(usage));
        }

        return slotQuests.get(questIndex);
    }

    /**
     * Applies PAPI-based placeholders on items that were detected
     * to contain placeholders in their name or lore.
     * <p>
     * This is executed per-player and may vary dynamically for:
     * <ul>
     *     <li>%achieved%</li>
     *     <li>%drawIn%</li>
     *     <li>Any PlaceholderAPI variable</li>
     * </ul>
     *
     * @param player       the player for placeholder context
     * @param playerQuests the player's quest data
     * @param inventory    the inventory where items must be updated
     */
    private void applyPapiItems(Player player, PlayerQuests playerQuests, Inventory inventory) {
        for (Map.Entry<Integer, ItemStack> entry : papiItems.entrySet()) {
            final Integer slot = entry.getKey();
            final ItemStack itemCopy = entry.getValue().clone();

            if (slot < 0 || slot >= size) {
                PluginLogger.error(ERROR_OCCURRED + "An item with placeholders defined for slot " + (slot + 1) + OUT_OF_BOUNDS);
                continue;
            }

            final ItemMeta meta = itemCopy.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(TextFormatter.format(player, meta.getDisplayName()));

                final List<String> lore = meta.getLore();
                if (lore != null) {
                    for (String str : lore) {
                        final String formatted = TextFormatter.format(player, str);
                        lore.set(lore.indexOf(str), QuestPlaceholders.replaceQuestPlaceholders(formatted, player, null, null, playerQuests, null));
                    }
                }

                meta.setLore(lore);
                itemCopy.setItemMeta(meta);
                inventory.setItem(slot, itemCopy);
            }
        }
    }

    /**
     * Loads an item's metadata from configuration:
     * <ul>
     *     <li>custom_model_data</li>
     *     <li>display name</li>
     *     <li>lore</li>
     *     <li>item_model (NMSHandler)</li>
     *     <li>item flags</li>
     * </ul>
     *
     * @param itemStack the base item
     * @param section   the item configuration section
     * @param flags     parsed item flags to apply
     * @return the fully configured ItemMeta
     */
    private ItemMeta getItemMeta(ItemStack itemStack, ConfigurationSection section, List<ItemFlag> flags) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        if (section.contains("custom_model_data")) {
            meta.setCustomModelData(section.getInt("custom_model_data"));
        }

        final String name = section.getString("name");
        if (name != null) {
            meta.setDisplayName(TextFormatter.format(name));
        }

        final List<String> lore = section.getStringList("lore");
        for (String str : lore) {
            lore.set(lore.indexOf(str), TextFormatter.format(str));
        }
        meta.setLore(lore);

        if (flags != null && !flags.isEmpty()) {
            meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }

        return meta;
    }

    /**
     * Determines the correct status message for a quest:
     * <ul>
     *     <li>Achieved message</li>
     *     <li>Progress message</li>
     *     <li>Manual completion hint (if enabled)</li>
     * </ul>
     *
     * @param progression quest progression
     * @param player      the player
     * @return the rendered status string
     */
    public String getQuestStatus(Progression progression, Player player) {
        if (progression.isAchieved()) {
            return TextFormatter.format(player, getAchievedStr());
        } else if (shouldDisplayManualCompletionHint(progression)) {
            final String hint = getCompleteGetTypeStr();
            if (hint == null || hint.isEmpty()) {
                final String formatted = QuestPlaceholders.replaceProgressPlaceholders(getProgressStr(), progression.getAdvancement(), progression.getRequiredAmount(), progression.getRewardAmount());
                return TextFormatter.format(player, formatted);
            }
            return TextFormatter.format(player, hint);
        } else {
            final String formatted = QuestPlaceholders.replaceProgressPlaceholders(getProgressStr(), progression.getAdvancement(), progression.getRequiredAmount(), progression.getRewardAmount());
            return TextFormatter.format(player, formatted);
        }
    }

    private boolean shouldDisplayManualCompletionHint(Progression progression) {
        return CompleteOnlyOnClick.isEnabled() && !progression.isAchieved() && progression.getAdvancement() >= progression.getRequiredAmount();
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
     * Get the not achieved string defined in the configuration.
     *
     * @return the not achieved string.
     */
    public String getNotAchievedStr() {
        return notAchievedStr;
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

    /**
     * Returns whether the clicked slot should close the inventory after handling the action.
     *
     * @param slot the raw inventory slot index (0-based)
     * @return true if the inventory must be closed
     */
    public boolean shouldCloseOnClick(int slot) {
        return closeOnClickSlots.contains(slot);
    }
}
