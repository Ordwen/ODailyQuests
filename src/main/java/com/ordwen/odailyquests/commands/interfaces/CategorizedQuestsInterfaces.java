package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.logging.Logger;

public class CategorizedQuestsInterfaces {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     * @param configurationFiles configuration files class.
     */
    public CategorizedQuestsInterfaces(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /* init items */
    private static Inventory easyQuestsInventory;
    private static Inventory mediumQuestsInventory;
    private static Inventory hardQuestsInventory;

    ItemStack emptyCaseItem;

    ItemStack itemStack;
    ItemMeta itemMeta;

    /**
     * Load all categorized interfaces.
     */
    public void loadCategorizedInterfaces() {
        /* Easy quests inventory */
        emptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.easy_quests").getString(".empty_item")));
        easyQuestsInventory = loadSelectedInterface(InterfacesManager.getEasyQuestsInventoryName(), emptyCaseItem, LoadQuests.getEasyQuests());

        /* Medium quests inventory */
        emptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.medium_quests").getString(".empty_item")));
        mediumQuestsInventory = loadSelectedInterface(InterfacesManager.getMediumQuestsInventoryName(), emptyCaseItem, LoadQuests.getMediumQuests());

        /* Hard quests inventory */
        emptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.hard_quests").getString(".empty_item")));
        hardQuestsInventory = loadSelectedInterface(InterfacesManager.getHardQuestsInventoryName(), emptyCaseItem, LoadQuests.getHardQuests());
    }

    /**
     * Load specified interface.
     * @param inventoryName name of interface.
     * @param emptyCaseItem item for empty-cases.
     * @param quests list of quests.
     * @return loaded interface.
     */
    public Inventory loadSelectedInterface(String inventoryName, ItemStack emptyCaseItem, ArrayList<Quest> quests) {
        Inventory inventory = Bukkit.createInventory(null, 54, inventoryName);

        /* add quests items on slots */
        for (Quest quest : quests) {
            itemStack = quest.getItemRequired();
            itemMeta = itemStack.getItemMeta();

            assert itemMeta != null;
            itemMeta.setDisplayName(quest.getQuestName());
            itemMeta.setLore(quest.getQuestDesc());
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(quests.indexOf(quest), itemStack);
        }

        /* fill empty slots */
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, emptyCaseItem);
        }

        return inventory;
    }

    /**
     * Get easy quests inventory.
     * @return easy quests inventory.
     */
    public static Inventory getEasyQuestsInterface() {
        return easyQuestsInventory;
    }

    /**
     * Get medium quests inventory.
     * @return medium quests inventory.
     */
    public static Inventory getMediumQuestsInterface() {
        return mediumQuestsInventory;
    }

    /**
     * Get hard quests inventory.
     * @return hard quests inventory.
     */
    public static Inventory getHardQuestsInterface() {
        return hardQuestsInventory;
    }
}
