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

import java.util.Objects;
import java.util.logging.Logger;

public class GlobalQuestsInterface {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     * @param configurationFiles configuration files class.
     */
    public GlobalQuestsInterface(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /* init items */
    private static Inventory globalQuestsInventory;

    ItemStack emptyCaseItem;

    ItemStack itemStack;
    ItemMeta itemMeta;

    /**
     * Load global quests interface.
     */
    public void loadGlobalQuestsInterface() {

        emptyCaseItem = new ItemStack(Material.valueOf(Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests")).getString(".empty_item")));
        globalQuestsInventory = Bukkit.createInventory(null, 54, InterfacesManager.getGlobalQuestsInventoryName());

        /* add quests items on slots */
        for (Quest quest : LoadQuests.getGlobalQuests()) {

            itemStack = quest.getItemRequired();
            itemMeta = itemStack.getItemMeta();

            assert itemMeta != null;
            itemMeta.setDisplayName(quest.getQuestName());
            itemMeta.setLore(quest.getQuestDesc());
            itemStack.setItemMeta(itemMeta);

            globalQuestsInventory.setItem(LoadQuests.getGlobalQuests().indexOf(quest), itemStack);
        }

        /* fill empty slots */
        for (int i = 0; i < globalQuestsInventory.getSize(); i++) {
            if (globalQuestsInventory.getItem(i) == null) globalQuestsInventory.setItem(i, emptyCaseItem);
        }

        logger.info(ChatColor.GREEN + "Global quests interface successfully loaded.");
    }

    /**
     * Get global quests inventory.
     * @return global quests inventory.
     */
    public static Inventory getGlobalQuestsInterface() {
        return globalQuestsInventory;
    }
}
