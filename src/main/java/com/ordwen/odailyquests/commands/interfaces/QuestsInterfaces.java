package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class QuestsInterfaces {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFile configurationFile;

    /**
     * Class instance constructor.
     * @param configurationFile configuration files class.
     */
    public QuestsInterfaces(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    /* init items */
    private final float invSize = 45;
    private static final List<ItemStack> emptyCaseItems = new ArrayList<>();
    private final Map<String, Pair<String, List<Inventory>>> categorizedInterfaces = new HashMap<>();

    private final NamespacedKey usePlaceholdersKey = new NamespacedKey(ODailyQuests.INSTANCE, "odq_interface_use_placeholders");
    private final NamespacedKey requiredKey = new NamespacedKey(ODailyQuests.INSTANCE, "odq_interface_required");

    /**
     * Load the global quests interface.
     */
    public void loadGlobalInterface() {
        categorizedInterfaces.clear();

        /* Init empty case items */
        final ItemStack globalEmptyCaseItem = new ItemStack(Material.valueOf(configurationFile.getConfig().getConfigurationSection("interfaces.global_quests").getString(".empty_item")));
        emptyCaseItems.add(globalEmptyCaseItem);

        /* Global quests inventory */
        int neededInventories = (int) Math.ceil(CategoriesLoader.getGlobalQuests().size() / invSize);
        loadSelectedInterface("global", InterfacesManager.getGlobalQuestsInventoryName(), globalEmptyCaseItem, neededInventories, CategoriesLoader.getGlobalQuests());
    }

    /**
     * Load all categorized interfaces.
     */
    public void loadCategorizedInterfaces() {
        categorizedInterfaces.clear();

        /* Init empty case items */
        final ItemStack easyEmptyCaseItem = new ItemStack(Material.valueOf(configurationFile.getConfig().getConfigurationSection("interfaces.easy_quests").getString(".empty_item")));
        final ItemStack mediumEmptyCaseItem = new ItemStack(Material.valueOf(configurationFile.getConfig().getConfigurationSection("interfaces.medium_quests").getString(".empty_item")));
        final ItemStack hardEmptyCaseItem = new ItemStack(Material.valueOf(configurationFile.getConfig().getConfigurationSection("interfaces.hard_quests").getString(".empty_item")));
        emptyCaseItems.addAll(Arrays.asList(easyEmptyCaseItem, mediumEmptyCaseItem, hardEmptyCaseItem));

        /* Easy quests inventory */
        int neededInventories = (int) Math.ceil(CategoriesLoader.getEasyQuests().size() / invSize);
        loadSelectedInterface("easy", InterfacesManager.getEasyQuestsInventoryName(), easyEmptyCaseItem, neededInventories, CategoriesLoader.getEasyQuests());

        /* Medium quests inventory */
        neededInventories = (int) Math.ceil(CategoriesLoader.getMediumQuests().size() / invSize);
        loadSelectedInterface("medium", InterfacesManager.getMediumQuestsInventoryName(), mediumEmptyCaseItem, neededInventories, CategoriesLoader.getMediumQuests());

        /* Hard quests inventory */
        neededInventories = (int) Math.ceil(CategoriesLoader.getHardQuests().size() / invSize);
        loadSelectedInterface("hard", InterfacesManager.getHardQuestsInventoryName(), hardEmptyCaseItem, neededInventories, CategoriesLoader.getHardQuests());
    }

    /**
     * Load specified interface.
     * @param inventoryName name of interface.
     * @param emptyCaseItem item for empty-cases.
     * @param quests list of quests.
     */
    public void loadSelectedInterface(String category, String inventoryName, ItemStack emptyCaseItem, int neededInventories, ArrayList<AbstractQuest> quests) {

        boolean allQuestsLoaded = false;
        int currentQuestIndex = 0;

        final List<Inventory> questsInventories = new ArrayList<>();

        for (int i = 0; i < neededInventories; i++) {
            Inventory inv = Bukkit.createInventory(null, 54, inventoryName + " - " + (i + 1));
            if (i > 0) {
                inv.setItem(45, Buttons.getPreviousButton());
            }
            if (i < neededInventories - 1) {
                inv.setItem(53, Buttons.getNextButton());
            }
            questsInventories.add(inv);
        }

        for (Inventory inv : questsInventories) {
            int i = 0;

            /* add quests items on slots */
            while (i < invSize && !allQuestsLoaded) {
                if (currentQuestIndex < quests.size()) {
                    final AbstractQuest quest = quests.get(currentQuestIndex);

                    final ItemStack itemStack = quest.getMenuItem();
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta != null) {
                        itemMeta.setDisplayName(quest.getQuestName());
                        itemMeta.setLore(quest.getQuestDesc());

                        if (quest.isUsingPlaceholders()) {
                            itemMeta.getPersistentDataContainer().set(usePlaceholdersKey, PersistentDataType.BYTE, (byte) 1);
                            itemMeta.getPersistentDataContainer().set(requiredKey, PersistentDataType.INTEGER, quest.getAmountRequired());
                        }

                        itemStack.setItemMeta(itemMeta);
                    }

                    inv.setItem(i, itemStack);
                    i++;
                    currentQuestIndex++;
                } else {
                    allQuestsLoaded = true;
                }
            }

            /* fill empty slots */
            for (int j = 0; j < inv.getSize(); j++) {
                if (inv.getItem(j) == null) inv.setItem(j, emptyCaseItem);
            }

            categorizedInterfaces.put(category, new Pair<>(inventoryName, questsInventories));
        }

        PluginLogger.fine("Categorized quests interface named " + inventoryName + " successfully loaded.");
    }

    public Inventory getInterfacePage(String category, int page, Player player) {
        final Inventory inventory = categorizedInterfaces.get(category).second().get(page);

        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);
            if (item != null && item.getItemMeta() != null) {
                final ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.getPersistentDataContainer().has(usePlaceholdersKey, PersistentDataType.BYTE)) {
                    final List<String> lore = itemMeta.getLore();
                    if (lore == null) continue;

                    final int required = itemMeta.getPersistentDataContainer().get(requiredKey, PersistentDataType.INTEGER);

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

    /**
     * Get empty case item material.
     *
     * @return material.
     */
    public static List<ItemStack> getEmptyCaseItems() {
        return emptyCaseItems;
    }
}
