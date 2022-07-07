package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerHead;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InterfacesManager implements Listener {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;
    private static GlobalQuestsInterface globalQuestsInterface;
    private static CategorizedQuestsInterfaces categorizedQuestsInterfaces;

    private static String nextPageItemName;
    private static String previousPageItemName;


    /**
     * Class instance constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public InterfacesManager(ODailyQuests oDailyQuests) {
        this.configurationFiles = oDailyQuests.getConfigurationFiles();
    }

    /* variables */
    private static List<ItemStack> emptyCaseItems;
    private static String playerQuestsInventoryName;
    private static String globalQuestsInventoryName;
    private static String easyQuestsInventoryName;
    private static String mediumQuestsInventoryName;
    private static String hardQuestsInventoryName;

    /**
     * Load all interfaces objects.
     */
    public void initAllObjects() {
        new Items(configurationFiles).initItems();

        initInventoryNames();
        loadInterfaces();
    }

    /**
     * Load all interfaces.
     */
    public void loadInterfaces() {
        loadPlayerQuestsInterface();
        loadQuestsInterfaces();
    }

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {
        new PlayerHead().initPlayerHead();
        new PlayerQuestsInterface().loadPlayerQuestsInterface();

        initEmptyCaseItems();
    }

    /**
     * Load categorized quests interface.
     */
    public void loadQuestsInterfaces() {
        initPaginationItemNames();

        if (configurationFiles.getConfigFile().getInt("quests_mode") == 2) {
            categorizedQuestsInterfaces = new CategorizedQuestsInterfaces(configurationFiles);
            categorizedQuestsInterfaces.loadCategorizedInterfaces();
        }
        else {
            globalQuestsInterface = new GlobalQuestsInterface(configurationFiles);
            globalQuestsInterface.loadGlobalQuestsInterface();
        }
    }
    /**
     * Init variables.
     */
    public void initInventoryNames() {
        playerQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface").getString(".inventory_name")));
        globalQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests").getString(".inventory_name")));
        easyQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.easy_quests").getString(".inventory_name")));
        mediumQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.medium_quests").getString(".inventory_name")));
        hardQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.hard_quests").getString(".inventory_name")));

        PluginLogger.info(ChatColor.GREEN + "Interfaces names successfully loaded.");
    }

    /**
     * Init empty case items.
     */
    public void initEmptyCaseItems() {
        emptyCaseItems = new ArrayList<>();
        emptyCaseItems.addAll(PlayerQuestsInterface.getFillItems());
        if (GlobalQuestsInterface.getEmptyCaseItem() != null) {
            emptyCaseItems.add(GlobalQuestsInterface.getEmptyCaseItem());
        }
        emptyCaseItems.addAll(CategorizedQuestsInterfaces.getEmptyCaseItems());
    }

    /**
     * Init pagination item names.
     */
    public void initPaginationItemNames() {
        nextPageItemName = ChatColor.translateAlternateColorCodes('&',
                ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".next_item_name")));
        previousPageItemName = ChatColor.translateAlternateColorCodes('&',
                ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".previous_item_name")));
    }

    public static String getPlayerQuestsInventoryName() {
        return playerQuestsInventoryName;
    }

    public static String getGlobalQuestsInventoryName() {
        return globalQuestsInventoryName;
    }

    public static String getEasyQuestsInventoryName() {
        return easyQuestsInventoryName;
    }

    public static String getMediumQuestsInventoryName() {
        return mediumQuestsInventoryName;
    }

    public static String getHardQuestsInventoryName() {
        return hardQuestsInventoryName;
    }
    public static CategorizedQuestsInterfaces getCategorizedQuestsInterfaces() { return  categorizedQuestsInterfaces; }
    public static GlobalQuestsInterface getGlobalQuestsInterface() { return globalQuestsInterface; }
    public static List<ItemStack> getEmptyCaseItems() { return emptyCaseItems; }
    public static String getNextPageItemName() { return nextPageItemName; }
    public static String getPreviousPageItemName() { return previousPageItemName; }
}


