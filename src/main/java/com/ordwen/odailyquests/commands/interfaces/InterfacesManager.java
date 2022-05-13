package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import com.ordwen.odailyquests.quests.player.progression.ValidateVillagerTradeQuest;
import com.ordwen.odailyquests.tools.ColorConvert;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InterfacesManager implements Listener {

    /* Logger for stacktrace */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;
    private final GlobalQuestsInterface globalQuestsInterface;
    private final CategorizedQuestsInterfaces categorizedQuestsInterfaces;

    /**
     * Class instance constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public InterfacesManager(ConfigurationFiles configurationFiles,
                             GlobalQuestsInterface globalQuestsInterface,
                             CategorizedQuestsInterfaces categorizedQuestsInterfaces) {
        this.configurationFiles = configurationFiles;
        this.globalQuestsInterface = globalQuestsInterface;
        this.categorizedQuestsInterfaces = categorizedQuestsInterfaces;
    }

    /* variables */
    private List<Material> emptyCaseItems;
    private static String playerQuestsInventoryName;
    private static String globalQuestsInventoryName;
    private static String easyQuestsInventoryName;
    private static String mediumQuestsInventoryName;
    private static String hardQuestsInventoryName;

    /**
     * Init variables.
     */
    public void initInventoryNames() {
        playerQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests").getString(".inventory_name")));
        globalQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests").getString(".inventory_name")));
        easyQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.easy_quests").getString(".inventory_name")));
        mediumQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.medium_quests").getString(".inventory_name")));
        hardQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces.hard_quests").getString(".inventory_name")));

        logger.info(ChatColor.GREEN + "Interfaces names successfully loaded.");
    }

    /**
     * Init empty case items.
     */
    public void initEmptyCaseItems() {
        emptyCaseItems = new ArrayList<>();
        emptyCaseItems.add(PlayerQuestsInterface.getEmptyCaseItem());
        if (GlobalQuestsInterface.getEmptyCaseItem() != null) {
            emptyCaseItems.add(GlobalQuestsInterface.getEmptyCaseItem().getType());
        }
        for (ItemStack item : CategorizedQuestsInterfaces.getEmptyCaseItems()) {
            emptyCaseItems.add(item.getType());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // check if player is trading
        if (event.getClickedInventory() != null
                && event.getInventory().getType() == InventoryType.MERCHANT
                && event.getSlotType() == InventoryType.SlotType.RESULT
                && event.getCurrentItem() != null
                && event.getCurrentItem().getType() != Material.AIR) {
            MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (event.getClickedInventory().getHolder() instanceof Villager villager) {
                if (merchantInventory.getSelectedRecipe() != null) {
                    ValidateVillagerTradeQuest.validateTradeQuestType(
                            event.getWhoClicked().getName(),
                            villager,
                            merchantInventory.getSelectedRecipe(),
                            event.getCurrentItem().getAmount());
                }
            }
            return;
        }

        String inventoryName = event.getView().getTitle();
        if (inventoryName.startsWith(playerQuestsInventoryName)
                || inventoryName.startsWith(globalQuestsInventoryName)
                || inventoryName.startsWith(easyQuestsInventoryName)
                || inventoryName.startsWith(mediumQuestsInventoryName)
                || inventoryName.startsWith(hardQuestsInventoryName)) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null
                    && !emptyCaseItems.contains(event.getCurrentItem().getType())
                    && event.getClick().isLeftClick()
                    && event.getSlot() < event.getView().getTopInventory().getSize()
                    && !event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {
                if (event.getCurrentItem().getType() != Material.PLAYER_HEAD
                        && inventoryName.startsWith(playerQuestsInventoryName)) {
                    int index;
                    switch (event.getSlot()) {
                        case 11 -> index = 1;
                        case 13 -> index = 2;
                        case 15 -> index = 3;
                        default -> {
                            return;
                        }
                    }
                    ProgressionManager.validateGetQuestType(event.getWhoClicked().getName(), index);
                } else if (!event.getCurrentItem().equals(Items.getPlayerHead((Player) event.getWhoClicked()))) {
                    int page = Integer.parseInt(inventoryName.substring(inventoryName.length() - 1));
                    if (event.getCurrentItem().getItemMeta() != null) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".next_item_name"))))) {
                            event.getWhoClicked().closeInventory();
                            if (inventoryName.startsWith(globalQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(globalQuestsInterface.getGlobalQuestsNextPage(page));
                            }
                            else if (inventoryName.startsWith(easyQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(categorizedQuestsInterfaces.getInterfaceNextPage(categorizedQuestsInterfaces.getEasyQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(mediumQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(categorizedQuestsInterfaces.getInterfaceNextPage(categorizedQuestsInterfaces.getMediumQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(hardQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(categorizedQuestsInterfaces.getInterfaceNextPage(categorizedQuestsInterfaces.getHardQuestsInventories(), page));
                            }
                        }
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".previous_item_name"))))) {
                            event.getWhoClicked().closeInventory();
                            if (inventoryName.startsWith(globalQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(globalQuestsInterface.getGlobalQuestsPreviousPage(page));
                            }
                            else if (inventoryName.startsWith(easyQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(categorizedQuestsInterfaces.getInterfacePreviousPage(categorizedQuestsInterfaces.getEasyQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(mediumQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(categorizedQuestsInterfaces.getInterfacePreviousPage(categorizedQuestsInterfaces.getMediumQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(hardQuestsInventoryName)) {
                                event.getWhoClicked().openInventory(categorizedQuestsInterfaces.getInterfacePreviousPage(categorizedQuestsInterfaces.getHardQuestsInventories(), page));
                            }
                        }
                    }
                }
            }
        }
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
}


