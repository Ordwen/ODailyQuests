package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.holder.CategoryHolder;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.TextFormatter;
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

    private final NamespacedKey requiredKey = new NamespacedKey(ODailyQuests.INSTANCE, "odq_interface_required");

    private final ConfigurationFile configurationFile;
    private final Buttons buttons;

    private String nextPageItemName;
    private String previousPageItemName;

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
        loadAllInterfaces(section);
    }

    public void initPaginationItemNames(ConfigurationSection section) {
        nextPageItemName = TextFormatter.format(section.getString(".next_item_name"));
        previousPageItemName = TextFormatter.format(section.getString(".previous_item_name"));
    }

    public void loadAllInterfaces(ConfigurationSection section) {
        categorizedInterfaces.clear();
        emptyCaseItems.clear();

        for (String category : CategoriesLoader.getAllCategories().keySet()) {
            final ConfigurationSection categorySection = section.getConfigurationSection(category);
            if (categorySection == null) {
                PluginLogger.error("Category section not found in the configuration file: " + category);
                continue;
            }

            final ItemStack emptyCaseItem = new ItemStack(Material.valueOf(categorySection.getString(EMPTY_ITEM)));
            emptyCaseItems.add(emptyCaseItem);

            int neededInventories = (int) Math.ceil(CategoriesLoader.getCategoryByName(category).size() / INV_SIZE);
            loadSelectedInterface(category, TextFormatter.format(categorySection.getString("inventory_name")), emptyCaseItem, neededInventories, CategoriesLoader.getCategoryByName(category));
        }
    }

    /**
     * Load specified interface.
     *
     * @param inventoryName name of interface.
     * @param emptyCaseItem item for empty-cases.
     * @param quests        list of quests.
     */
    public void loadSelectedInterface(String category, String inventoryName, ItemStack emptyCaseItem, int neededInventories, List<AbstractQuest> quests) {
        final List<Inventory> questsInventories = createInventories(category, inventoryName, neededInventories);
        populateInventories(questsInventories, emptyCaseItem, quests);
        categorizedInterfaces.put(category, new Pair<>(inventoryName, questsInventories));
        PluginLogger.fine("Categorized quests interface named " + inventoryName + " successfully loaded.");
    }

    /**
     * Create a list of inventories.
     *
     * @param category          category of the inventory.
     * @param inventoryName     name of the inventory.
     * @param neededInventories number of inventories needed.
     * @return list of inventories.
     */
    private List<Inventory> createInventories(String category, String inventoryName, int neededInventories) {
        final List<Inventory> inventories = new ArrayList<>();
        for (int i = 0; i < neededInventories; i++) {
            final CategoryHolder holder = new CategoryHolder(i, category);
            final Inventory inv = Bukkit.createInventory(holder, 54, inventoryName + " - " + (i + 1));

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

            itemMeta.getPersistentDataContainer().set(requiredKey, PersistentDataType.STRING, quest.getRequiredAmountRaw());

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

                final List<String> lore = itemMeta.getLore();
                if (lore == null) continue;

                final String required = pdc.get(requiredKey, PersistentDataType.STRING);

                lore.replaceAll(s -> s.replace("%progress%", String.valueOf(0)));
                lore.replaceAll(s -> s.replace("%progressBar%", getProgressBar(required)));
                lore.replaceAll(s -> s.replace("%required%", String.valueOf(required)));
                lore.replaceAll(s -> s.replace("%drawIn%", "~"));
                lore.replaceAll(s -> TextFormatter.format(player, s));

                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);

            }
        }
        return inventory;
    }

    /**
     * Get the progress bar for the specified required amount.
     * If the required amount is dynamic, return a progress bar with 0/1.
     * If the required amount is static, return a progress bar with 0/required amount.
     *
     * @param requiredAmountRaw required amount.
     * @return progress bar.
     */
    private String getProgressBar(String requiredAmountRaw) {
        if (requiredAmountRaw.contains("-")) {
            return ProgressBar.getProgressBar(0, 1);
        }

        return ProgressBar.getProgressBar(0, Integer.parseInt(requiredAmountRaw));
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

    public String getNextPageItemName() {
        return nextPageItemName;
    }

    public String getPreviousPageItemName() {
        return previousPageItemName;
    }
}
