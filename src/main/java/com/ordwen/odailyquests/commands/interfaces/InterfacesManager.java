package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import net.citizensnpcs.api.event.NPCClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Logger;

public class InterfacesManager implements Listener {


    /* Logger for stacktrace */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /**
     * Getting instance of classes.
     */
    private static ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public InterfacesManager(ConfigurationFiles configurationFiles) {
        InterfacesManager.configurationFiles = configurationFiles;
    }

    /* variables */
    private static String playerQuestsInventoryName;
    private static String globalQuestsInventoryName;
    private static String easyQuestsInventoryName;
    private static String mediumQuestsInventoryName;
    private static String hardQuestsInventoryName;

    /**
     * Init variables.
     */
    public static void initInventoryNames() {
        playerQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests").getString(".inventory_name"));
        globalQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests").getString(".inventory_name"));
        easyQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("interfaces.easy_quests").getString(".inventory_name"));
        mediumQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("interfaces.medium_quests").getString(".inventory_name"));
        hardQuestsInventoryName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("interfaces.hard_quests").getString(".inventory_name"));

        logger.info(ChatColor.GREEN + "Interfaces names successfully loaded.");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String inventoryName = event.getView().getTitle();
        if (inventoryName.equals(playerQuestsInventoryName)
                || inventoryName.equals(globalQuestsInventoryName)
                || inventoryName.equals(easyQuestsInventoryName)
                || inventoryName.equals(mediumQuestsInventoryName)
                || inventoryName.equals(hardQuestsInventoryName)) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null
                    && !event.getCurrentItem().getType().equals(PlayerQuestsInterface.getEmptyCaseItem())
                    && event.getClick().isLeftClick()
                    && event.getSlot() < event.getView().getTopInventory().getSize()
                    && !event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {
                ProgressionManager.validateGetQuestType(event.getWhoClicked().getName(), event.getCurrentItem().getType());
            }
        }
    }

    @EventHandler
    public void onNPCClickEvent(NPCClickEvent event) {
        String npcName = event.getNPC().getName();

        /* Global interface */
        if (npcName.equals(configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_global"))) {
            if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
                event.getClicker().openInventory(GlobalQuestsInterface.getGlobalQuestsInterface());
            }
        }

        /* Easy interface */
        if (npcName.equals(configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_easy"))) {
            if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
                event.getClicker().openInventory(CategorizedQuestsInterfaces.getEasyQuestsInterface());
            }
        }

        /* Medium interface */
        if (npcName.equals(configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_medium"))) {
            if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
                event.getClicker().openInventory(CategorizedQuestsInterfaces.getMediumQuestsInterface());
            }
        }

        /* Hard interface */
        if (npcName.equals(configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_hard"))) {
            if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
                event.getClicker().openInventory(CategorizedQuestsInterfaces.getHardQuestsInterface());
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

    public static String getMediumQuestsInventoryName() { return mediumQuestsInventoryName; }

    public static String getHardQuestsInventoryName() {
        return hardQuestsInventoryName;
    }
}


