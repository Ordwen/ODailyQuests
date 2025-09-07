package com.ordwen.odailyquests.events.listeners.integrations.npcs;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.configuration.integrations.NPCNames;
import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public abstract class AbstractNpcHook implements Listener {

    private static final String PERMISSION_PREFIX = "odailyquests.";
    protected final InterfacesManager interfacesManager;

    protected AbstractNpcHook(InterfacesManager interfacesManager) {
        this.interfacesManager = Objects.requireNonNull(interfacesManager, "interfacesManager");
    }

    protected final void handle(String npcName, Player player) {
        if (npcName == null || player == null) return;
        if (npcName.equals(NPCNames.getPlayerNPCName())) {
            if (player.hasPermission(PERMISSION_PREFIX + "player")) {
                final Inventory inv = interfacesManager.getPlayerQuestsInterface().getPlayerQuestsInterface(player);
                if (inv != null) {
                    player.openInventory(inv);
                }
            } else {
                sendNoPermission(player);
            }
            return;
        }

        if (!NPCNames.isCategoryForNPCName(npcName)) return;

        final String category = NPCNames.getCategoryByNPCName(npcName);
        if (player.hasPermission(PERMISSION_PREFIX + category)) {
            final Inventory inv = interfacesManager.getQuestsInterfaces().getInterfaceFirstPage(category, player);
            if (inv == null) {
                player.sendMessage(QuestsMessages.CONFIGURATION_ERROR.toString());
                return;
            }
            player.openInventory(inv);
        } else {
            sendNoPermission(player);
        }
    }

    protected void sendNoPermission(Player player) {
        final String msg = QuestsMessages.NO_PERMISSION_CATEGORY.toString();
        if (msg != null) player.sendMessage(msg);
    }
}
