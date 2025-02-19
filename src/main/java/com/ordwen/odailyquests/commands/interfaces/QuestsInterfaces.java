package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class QuestsInterfaces {

    private static final float INV_SIZE = 45;
    private static final String EMPTY_ITEM = "empty_item";

    private final NamespacedKey usePlaceholdersKey = new NamespacedKey(ODailyQuests.INSTANCE, "odq_interface_use_placeholders");
    private final NamespacedKey requiredKey = new NamespacedKey(ODailyQuests.INSTANCE, "odq_interface_required");

    private final ConfigurationFile configurationFile;
    private final Buttons buttons;

    private String nextPageItemName;
    private String previousPageItemName;

    private String globalQuestsInventoryName;
    private String easyQuestsInventoryName;
    private String mediumQuestsInventoryName;
    private String hardQuestsInventoryName;

    private final List<ItemStack> emptyCaseItems = new ArrayList<>();
    private final Map<String, Pair<String, List<Inventory>>> categorizedInterfaces = new HashMap<>();

    public QuestsInterfaces(ConfigurationFile configurationFile, Buttons buttons) {
        this.configurationFile = configurationFile;
        this.buttons = buttons;
    }

    public void loadAll() {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("interfaces");
        if (section == null) {
            PluginLogger.error("Interfaces section not found in the configuration file.");
            return;
        }

        initPaginationItemNames(section);
        initInventoryNames(section);

        if (Modes.getQuestsMode() == 2) {
            loadCategorizedInterfaces(section);
        } else loadGlobalInterface(section);
    }

    public void initPaginationItemNames(ConfigurationSection section) {
        nextPageItemName = ColorConvert.convertColorCode(section.getString(".next_item_name"));
        previousPageItemName = ColorConvert.convertColorCode(section.getString(".previous_item_name"));
    }

    public void initInventoryNames(ConfigurationSection section) {
        globalQuestsInventoryName = ColorConvert.convertColorCode(section.getString(".global_quests.inventory_name"));
        easyQuestsInventoryName = ColorConvert.convertColorCode(section.getString(".easy_quests.inventory_name"));
        mediumQuestsInventoryName = ColorConvert.convertColorCode(section.getString(".medium_quests.inventory_name"));
        hardQuestsInventoryName = ColorConvert.convertColorCode(section.getString(".hard_quests.inventory_name"));

        PluginLogger.fine("Interfaces names successfully loaded.");
    }

    /**
     * Load the global quests interface.
     */
    public void loadGlobalInterface(ConfigurationSection section) {
        categorizedInterfaces.clear();

        /* Init empty case items */
        final ConfigurationSection globalSection = section.getConfigurationSection("global_quests");
        if (globalSection == null) {
            PluginLogger.error("Global quests section not found in the configuration file.");
            return;
        }

        final ItemStack globalEmptyCaseItem = new ItemStack(Material.valueOf(globalSection.getString(EMPTY_ITEM)));
        emptyCaseItems.add(globalEmptyCaseItem);

        /* Global quests inventory */
        int neededInventories = (int) Math.ceil(CategoriesLoader.getGlobalQuests().size() / INV_SIZE);
        loadSelectedInterface("global", globalQuestsInventoryName, globalEmptyCaseItem, neededInventories, CategoriesLoader.getGlobalQuests());
    }

    /**
     * Load all categorized interfaces.
     */
    public void loadCategorizedInterfaces(ConfigurationSection section) {
        categorizedInterfaces.clear();

        /* Init empty case items */
        final ConfigurationSection easySection = section.getConfigurationSection("easy_quests");
        if (easySection == null) {
            PluginLogger.error("Easy quests section not found in the configuration file.");
            return;
        }
        final ItemStack easyEmptyCaseItem = new ItemStack(Material.valueOf(easySection.getString(EMPTY_ITEM)));

        final ConfigurationSection mediumSection = section.getConfigurationSection("medium_quests");
        if (mediumSection == null) {
            PluginLogger.error("Medium quests section not found in the configuration file.");
            return;
        }
        final ItemStack mediumEmptyCaseItem = new ItemStack(Material.valueOf(mediumSection.getString(EMPTY_ITEM)));

        final ConfigurationSection hardSection = section.getConfigurationSection("hard_quests");
        if (hardSection == null) {
            PluginLogger.error("Hard quests section not found in the configuration file.");
            return;
        }
        final ItemStack hardEmptyCaseItem = new ItemStack(Material.valueOf(hardSection.getString(EMPTY_ITEM)));

        emptyCaseItems.addAll(Arrays.asList(easyEmptyCaseItem, mediumEmptyCaseItem, hardEmptyCaseItem));

        /* Easy quests inventory */
        int neededInventories = (int) Math.ceil(CategoriesLoader.getEasyQuests().size() / INV_SIZE);
        loadSelectedInterface("easy", easyQuestsInventoryName, easyEmptyCaseItem, neededInventories, CategoriesLoader.getEasyQuests());

        /* Medium quests inventory */
        neededInventories = (int) Math.ceil(CategoriesLoader.getMediumQuests().size() / INV_SIZE);
        loadSelectedInterface("medium", mediumQuestsInventoryName, mediumEmptyCaseItem, neededInventories, CategoriesLoader.getMediumQuests());

        /* Hard quests inventory */
        neededInventories = (int) Math.ceil(CategoriesLoader.getHardQuests().size() / INV_SIZE);
        loadSelectedInterface("hard", hardQuestsInventoryName, hardEmptyCaseItem, neededInventories, CategoriesLoader.getHardQuests());
    }

    /**
     * Load specified interface.
     *
     * @param inventoryName name of interface.
     * @param emptyCaseItem item for empty-cases.
     * @param quests        list of quests.
     */
    public void loadSelectedInterface(String category, String inventoryName, ItemStack emptyCaseItem, int neededInventories, List<AbstractQuest> quests) {
        final List<Inventory> questsInventories = createInventories(inventoryName, neededInventories);
        populateInventories(questsInventories, emptyCaseItem, quests);
        categorizedInterfaces.put(category, new Pair<>(inventoryName, questsInventories));
        PluginLogger.fine("Categorized quests interface named " + inventoryName + " successfully loaded.");
    }

    /**
     * Create a list of inventories.
     *
     * @param inventoryName     name of the inventory.
     * @param neededInventories number of inventories needed.
     * @return list of inventories.
     */
    private List<Inventory> createInventories(String inventoryName, int neededInventories) {
        final List<Inventory> inventories = new ArrayList<>();
        for (int i = 0; i < neededInventories; i++) {
            final Inventory inv = Bukkit.createInventory(null, 54, inventoryName + " - " + (i + 1));
            if (i > 0) inv.setItem(45, buttons.getPreviousButton());
            if (i < neededInventories - 1) inv.setItem(53, buttons.getNextButton());
            inventories.add(inv);
        }
        return inventories;
    }

    /**
     * Add all items related to quests in the inventories.
     *
     * @param inventories   list of inventories.
     * @param emptyCaseItem item for empty-cases.
     * @param quests        list of quests.
     */
    private void populateInventories(List<Inventory> inventories, ItemStack emptyCaseItem, List<AbstractQuest> quests) {
        int currentQuestIndex = 0;
        boolean allQuestsLoaded = false;

        for (Inventory inv : inventories) {
            int slotIndex = 0;
            while (slotIndex < INV_SIZE && !allQuestsLoaded) {
                if (currentQuestIndex < quests.size()) {
                    inv.setItem(slotIndex, createQuestItem(quests.get(currentQuestIndex)));
                    slotIndex++;
                    currentQuestIndex++;
                } else {
                    allQuestsLoaded = true;
                }
            }
            fillEmptySlots(inv, emptyCaseItem);
        }
    }

    /**
     * Create an item for a quest.
     *
     * @param quest quest to create item for.
     * @return item for the quest.
     */
    private ItemStack createQuestItem(AbstractQuest quest) {
        ItemStack itemStack = quest.getMenuItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(quest.getQuestName());
            itemMeta.setLore(quest.getQuestDesc());

            if (quest.isUsingPlaceholders()) {
                itemMeta.getPersistentDataContainer().set(usePlaceholdersKey, PersistentDataType.BYTE, (byte) 1);
                itemMeta.getPersistentDataContainer().set(requiredKey, PersistentDataType.INTEGER, quest.getAmountRequired());
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    /**
     * Fill empty slots with empty-case items.
     *
     * @param inv           inventory to fill.
     * @param emptyCaseItem item for empty-cases.
     */
    private void fillEmptySlots(Inventory inv, ItemStack emptyCaseItem) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, emptyCaseItem);
        }
    }

    /**
     * Get the inventory for the specified category and page.
     *
     * @param category category of the interface.
     * @param page     page of the interface.
     * @param player   player to get the interface for.
     * @return inventory for the specified category and page.
     */
    public Inventory getInterfacePage(String category, int page, Player player) {
        final Inventory inventory = categorizedInterfaces.get(category).second().get(page);

        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);
            if (item != null && item.getItemMeta() != null) {
                final ItemMeta itemMeta = item.getItemMeta();
                final PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                if (pdc.has(usePlaceholdersKey, PersistentDataType.BYTE)) {
                    final List<String> lore = itemMeta.getLore();
                    if (lore == null) continue;

                    final int required = pdc.get(requiredKey, PersistentDataType.INTEGER);

                    lore.replaceAll(s -> s.replace("%progress%", String.valueOf(0)));
                    lore.replaceAll(s -> s.replace("%progressBar%", ProgressBar.getProgressBar(0, required)));
                    lore.replaceAll(s -> s.replace("%required%", String.valueOf(required)));
                    lore.replaceAll(s -> s.replace("%drawIn%", "~"));
                    lore.replaceAll(s -> PAPIHook.getPlaceholders(player, s));

                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
        }
        return inventory;
    }

    public Inventory getInterfaceFirstPage(String category, Player player) {
        return getInterfacePage(category, 0, player);
    }

    public Inventory getInterfaceNextPage(String category, int page, Player player) {
        return getInterfacePage(category, page + 1, player);
    }

    public Inventory getInterfacePreviousPage(String category, int page, Player player) {
        return getInterfacePage(category, page - 1, player);
    }

    public boolean isEmptyCaseItem(ItemStack itemStack) {
        return emptyCaseItems.contains(itemStack);
    }

    public String getGlobalQuestsInventoryName() {
        return globalQuestsInventoryName;
    }

    public String getEasyQuestsInventoryName() {
        return easyQuestsInventoryName;
    }

    public String getMediumQuestsInventoryName() {
        return mediumQuestsInventoryName;
    }

    public String getHardQuestsInventoryName() {
        return hardQuestsInventoryName;
    }

    public String getNextPageItemName() {
        return nextPageItemName;
    }

    public String getPreviousPageItemName() {
        return previousPageItemName;
    }
}
