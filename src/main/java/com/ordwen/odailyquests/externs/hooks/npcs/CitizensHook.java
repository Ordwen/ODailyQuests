package com.ordwen.odailyquests.externs.hooks.npcs;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.configuration.integrations.NPCNames;
import com.ordwen.odailyquests.enums.QuestsMessages;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensHook implements Listener {

    private static final String PERMISSION_PREFIX = "odailyquests.";

    private final InterfacesManager interfacesManager;

    public CitizensHook(InterfacesManager interfacesManager) {
        this.interfacesManager = interfacesManager;
    }

    @EventHandler
    public void onNPCClickEvent(NPCRightClickEvent event) {
        final String npcName = event.getNPC().getName();
        final Player player = event.getClicker();

        /* Player interface */
        if (npcName.equals(NPCNames.getPlayerNPCName())) {
            if (player.hasPermission(PERMISSION_PREFIX + "player")) {
                player.openInventory(interfacesManager.getPlayerQuestsInterface().getPlayerQuestsInterface(player));
            } else {
                final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
                if (msg != null) player.sendMessage(msg);
            }
        }

        /* Category interface */
        if (NPCNames.isCategoryForNPCName(npcName)) {
            final String category = NPCNames.getCategoryByNPCName(npcName);
            if (player.hasPermission(PERMISSION_PREFIX + category)) {
                player.openInventory(interfacesManager.getQuestsInterfaces().getInterfaceFirstPage(category, player));
            } else {
                final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
                if (msg != null) player.sendMessage(msg);
            }
        }
    }

    public static boolean isCitizensEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Citizens");
    }
}
