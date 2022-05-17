package com.ordwen.odailyquests.apis.hooks.npcs;

import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.integrations.NPCNames;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensHook implements Listener {

    //private final ConfigurationFiles configurationFiles;

    /**
     * Setup CitizensAPI.
     */
    public static boolean setupCitizens() {
        Citizens citizensAPI = (Citizens) Bukkit.getServer().getPluginManager().getPlugin("Citizens");
        return citizensAPI != null;
    }

    @EventHandler
    public void onNPCClickEvent(NPCRightClickEvent event) {
        String npcName = event.getNPC().getName();

        /* Player interface */
        if (npcName.equals(NPCNames.getPlayerNPCName())) {
            if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_PLAYER.getPermission())) {
                event.getClicker().openInventory(PlayerQuestsInterface.getPlayerQuestsInterface(event.getClicker().getName()));
            } else event.getClicker().sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
        }

        /* Global interface */
        if (npcName.equals(NPCNames.getGlobalNPCName())) {
            if (Modes.getQuestsMode() == 1) {
                if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
                    event.getClicker().openInventory(InterfacesManager.getGlobalQuestsInterface().getGlobalQuestsInterfaceFirstPage());
                } else event.getClicker().sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
            } else event.getClicker().sendMessage(QuestsMessages.GLOBAL_DISABLED.toString());
        }

        /* Easy interface */
        if (npcName.equals(NPCNames.getEasyNPCName())) {
            if (Modes.getQuestsMode() == 2) {
                if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
                    event.getClicker().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getEasyQuestsInterfaceFirstPage());
                } else event.getClicker().sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
            } else event.getClicker().sendMessage(QuestsMessages.CATEGORIZED_DISABLED.toString());
        }

        /* Medium interface */
        if (npcName.equals(NPCNames.getMediumNPCName())) {
            if (Modes.getQuestsMode() == 2) {
                if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
                    event.getClicker().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getMediumQuestsInterfaceFirstPage());
                } else event.getClicker().sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
            } else event.getClicker().sendMessage(QuestsMessages.CATEGORIZED_DISABLED.toString());
        }

        /* Hard interface */
        if (npcName.equals(NPCNames.getHardNPCName())) {
            if (Modes.getQuestsMode() == 2) {
                if (event.getClicker().hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
                    event.getClicker().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getHardQuestsInterfaceFirstPage());
                } else event.getClicker().sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
            } else event.getClicker().sendMessage(QuestsMessages.CATEGORIZED_DISABLED.toString());
        }
    }
}
