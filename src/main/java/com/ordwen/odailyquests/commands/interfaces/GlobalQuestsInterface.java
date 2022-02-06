package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginLogger;

import java.util.Objects;
import java.util.logging.Logger;

public class GlobalQuestsInterface implements Listener {

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
    Logger logger = PluginLogger.getLogger("ODailyQuests");

    /* init items */
    private static Inventory globalQuestsInventory;

    String inventoryName;
    ItemStack emptyCaseItem;

    ItemStack itemStack;
    ItemMeta itemMeta;

    /**
     * Load global quests inventory.
     */
    public void loadGlobalQuestsInventory() {

        inventoryName = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests")).getString(".inventory_name");
        emptyCaseItem = new ItemStack(Material.valueOf(Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests")).getString(".empty_item")));

        globalQuestsInventory = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', inventoryName));

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

        logger.info(ChatColor.GREEN + "Global quests inventory successfully loaded.");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        inventoryName = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests")).getString(".inventory_name");
        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', inventoryName))) {
            event.setCancelled(true);
        }
    }

    /**
     * Get global quests inventory.
     * @return global quests inventory.
     */
    public static Inventory getGlobalQuestsInventory() {
        return globalQuestsInventory;
    }

    // si trop de quêtes pour un seul menu
    // pouvoir config les menus

}
