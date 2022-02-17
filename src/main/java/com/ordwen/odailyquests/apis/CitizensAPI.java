package com.ordwen.odailyquests.apis;

import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensAPI implements Listener {

    private final ConfigurationFiles configurationFiles;
    private static Citizens citizensAPI;

    /**
     * Setup CitizensAPI.
     */
    public static boolean setupCitizens() {
        citizensAPI = (Citizens) Bukkit.getServer().getPluginManager().getPlugin("Citizens");
        return citizensAPI != null;
    }

    /**
     * Class instance constructor.
     * @param configurationFiles configuration files class.
     */
    public CitizensAPI(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @EventHandler
    public void onNPCClickEvent(NPCRightClickEvent event) {
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
}
