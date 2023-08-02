package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GlobalQuestsInterface {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public GlobalQuestsInterface(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /* init items */
    private final List<Inventory> inventories = new ArrayList<>();
    private static ItemStack emptyCaseItem;

    /**
     * Load global quests interface.
     */
    public void loadGlobalQuestsInterface() {

        boolean allQuestsLoaded = false;
        int currentQuestIndex = 0;

        emptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests").getString(".empty_item")));
        float invSize = 45;
        int neededInventories = (int) Math.ceil(CategoriesLoader.getGlobalQuests().size() / invSize);

        for (int i = 0; i < neededInventories; i++) {
            Inventory inv = Bukkit.createInventory(null, 54, InterfacesManager.getGlobalQuestsInventoryName() + " - " + (i + 1));

            if (i > 0) {
                inv.setItem(45, Buttons.getPreviousButton());
            }
            if (i < neededInventories - 1) {
                inv.setItem(53, Buttons.getNextButton());
            }

            inventories.add(inv);
        }

        for (Inventory inv : inventories) {
            int i = 0;

            /* add quests items on slots */
            while (i < invSize && !allQuestsLoaded) {

                if (currentQuestIndex < CategoriesLoader.getGlobalQuests().size()) {
                    AbstractQuest quest = CategoriesLoader.getGlobalQuests().get(currentQuestIndex);

                    ItemStack itemStack = quest.getMenuItem();
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    assert itemMeta != null;
                    itemMeta.setDisplayName(quest.getQuestName());
                    itemMeta.setLore(quest.getQuestDesc());
                    itemStack.setItemMeta(itemMeta);

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
        }
        PluginLogger.fine("Global quests interface successfully loaded.");
    }

    /**
     * Get global quests inventory first page.
     *
     * @return global quests inventory first page.
     */
    public Inventory getGlobalQuestsInterfaceFirstPage() {
        return inventories.get(0);
    }

    /**
     * Get global quests inventory next page.
     *
     * @return global quests inventory next page.
     */
    public Inventory getGlobalQuestsNextPage(int page) {
        return inventories.get(page);
    }

    /**
     * Get global quests inventory previous page.
     *
     * @return global quests inventory previous page.
     */
    public Inventory getGlobalQuestsPreviousPage(int page) {
        return inventories.get(page - 2);
    }

    /**
     * Get empty case item material.
     *
     * @return material.
     */
    public static ItemStack getEmptyCaseItem() {
        return emptyCaseItem;
    }
}
