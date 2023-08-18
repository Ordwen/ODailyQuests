package com.ordwen.odailyquests.externs.hooks.npcs;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.integrations.NPCNames;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensHook implements Listener {
    
    /**
     * Setup CitizensAPI.
     */
    public static boolean setupCitizens() {
        Citizens citizensAPI = (Citizens) Bukkit.getServer().getPluginManager().getPlugin("Citizens");
        return citizensAPI != null;
    }

    @EventHandler
    public void onNPCClickEvent(NPCRightClickEvent event) {
        final String npcName = event.getNPC().getName();
        final Player player = event.getClicker();

        /* Player interface */
        if (npcName.equals(NPCNames.getPlayerNPCName())) {
            if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_PLAYER.getPermission())) {
                player.openInventory(PlayerQuestsInterface.getPlayerQuestsInterface(player));
            } else {
                final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
                if (msg != null) player.sendMessage(msg);
            }
        }

        /* Global interface */
        if (npcName.equals(NPCNames.getGlobalNPCName())) {
            if (Modes.getQuestsMode() == 1) {
                if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
                    player.openInventory(InterfacesManager.getInterfaceFirstPage("global", player));
                } else {
                    final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
                    if (msg != null) player.sendMessage(msg);
                }
            } else {
                final String msg = QuestsMessages.GLOBAL_DISABLED.toString();
                if (msg != null) player.sendMessage(msg);
            }
        }

        /* Easy interface */
        if (npcName.equals(NPCNames.getEasyNPCName())) {
            if (Modes.getQuestsMode() == 2) {
                if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
                    player.openInventory(InterfacesManager.getInterfaceFirstPage("easy", player));
                } else {
                    final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
                    if (msg != null) player.sendMessage(msg);
                }
            } else {
                final String msg = QuestsMessages.CATEGORIZED_DISABLED.toString();
                if (msg != null) player.sendMessage(msg);
            }
        }

        /* Medium interface */
        if (npcName.equals(NPCNames.getMediumNPCName())) {
            if (Modes.getQuestsMode() == 2) {
                if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
                    player.openInventory(InterfacesManager.getInterfaceFirstPage("medium", player));
                } else {
                    final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
                    if (msg != null) player.sendMessage(msg);
                }
            } else {
                final String msg = QuestsMessages.CATEGORIZED_DISABLED.toString();
                if (msg != null) player.sendMessage(msg);
            }
        }

        /* Hard interface */
        if (npcName.equals(NPCNames.getHardNPCName())) {
            if (Modes.getQuestsMode() == 2) {
                if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
                    player.openInventory(InterfacesManager.getInterfaceFirstPage("hard", player));
                } else {
                    final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
                    if (msg != null) player.sendMessage(msg);
                }
            } else {
                final String msg = QuestsMessages.CATEGORIZED_DISABLED.toString();
                if (msg != null) player.sendMessage(msg);
            }
        }
    }
}
